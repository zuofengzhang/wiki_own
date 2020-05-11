---
title: "无锁队列与Disruptor"
layout: post
date: 2019-03-20 00:00:00
category: Java
tags:
 - Java
 - Thread
 - disruptor 

share: true
comments: true
---

转自[剖析Disruptor为什么会这么快](http://www.importnew.com/19877.html)

**Disruptor如何解决这些问题。**

首先，Disruptor根本就不用锁。

取而代之的是，在需要确保操作是线程安全的（特别是，在[多生产者](http://mechanitis.blogspot.com/2011/07/dissecting-disruptor-writing-to-ring.html)的环境下，更新下一个可用的序列号）地方，我们使用[CAS](http://en.wikipedia.org/wiki/Compare-and-swap)（Compare And Swap/Set）操作。这是一个CPU级别的指令，在我的意识中，它的工作方式有点像乐观锁——CPU去更新一个值，但如果想改的值不再是原来的值，操作就失败，因为很明显，有其它操作先改变了这个值。

[![img](http://ifeve.com/wp-content/uploads/2013/01/ConcurrencyCAS.png)](http://ifeve.com/wp-content/uploads/2013/01/ConcurrencyCAS.png)

注意，这可以是CPU的两个不同的核心，但不会是两个独立的CPU。

CAS操作比锁消耗资源少的多，因为它们不牵涉操作系统，它们直接在CPU上操作。但它们并非没有代价——在上面的试验中，单线程无锁耗时300ms，单线程有锁耗时10000ms，单线程使用CAS耗时5700ms。所以它比使用锁耗时少，但比不需要考虑竞争的单线程耗时多。

回到Disruptor，在我[讲生产者](http://ifeve.com/disruptor-writing-ringbuffer/)时讲过[ClaimStrategy](https://github.com/LMAX-Exchange/disruptor/blob/version-2.x/code/src/main/com/lmax/disruptor/ClaimStrategy.java)。在这些代码中，你可以看见两个策略，一个是SingleThreadedStrategy（单线程策略）另一个是MultiThreadedStrategy（多线程策略）。你可能会有疑问，为什么在只有单个生产者时不用多线程的那个策略？它是否能够处理这种场景？当然可以。但多线程的那个使用了[AtomicLong](http://download.oracle.com/javase/6/docs/api/java/util/concurrent/atomic/AtomicLong.html)（Java提供的CAS操作），而单线程的使用long，没有锁也没有CAS。这意味着单线程版本会非常快，因为它只有一个生产者，不会产生序号上的冲突。

我知道，你可能在想：把一个数字转成AtomicLong不可能是Disruptor速度快的唯一秘密。当然，它不是，否则它不可能称为“为什么这么快（第一部分）”。

但这是非常重要的一点

——在整个复杂的框架中，只有这一个地方出现多线程竞争修改同一个变量值。这就是秘密。还记得所有的访问对象都拥有序号吗？如果只有一个生产者，那么系统中的每一个序列号只会由一个线程写入。这意味着没有竞争、不需要锁、甚至不需要CAS。在ClaimStrategy中，如果存在多个生产者，唯一会被多线程竞争写入的序号就是 ClaimStrategy 对象里的那个。

这也是为什么Entry中的每一个变量都[只能被一个消费者写](http://ifeve.com/dissecting-disruptor-wiring-up/)。它确保了没有写竞争，因此不需要锁或者CAS。

**回到为什么队列不能胜任这个工作**

因此你可能会有疑问，为什么队列底层用RingBuffer来实现，仍然在性能上无法与 Disruptor 相比。队列和[最简单的ring buffer](http://en.wikipedia.org/wiki/Circular_buffer)只有两个指针——一个指向队列的头，一个指向队尾：

[![img](http://ifeve.com/wp-content/uploads/2013/01/QueueMultiple.png)](http://ifeve.com/wp-content/uploads/2013/01/QueueMultiple.png)

如果有超过一个生产者想要往队列里放东西，尾指针就将成为一个冲突点，因为有多个线程要更新它。如果有多个消费者，那么头指针就会产生竞争，因为元素被消费之后，需要更新指针，所以不仅有读操作还有写操作了。

等等，我听到你喊冤了！因为我们已经知道这些了，所以队列常常是单生产者和单消费者（或者至少在我们的测试里是）。
队列的目的就是为生产者和消费者提供一个地方存放要交互的数据，帮助缓冲它们之间传递的消息。这意味着缓冲常常是满的（生产者比消费者快）或者空的（消费者比生产者快）。生产者和消费者能够步调一致的情况非常少见。

所以，这就是事情的真面目。一个空的队列：

[![img](http://ifeve.com/wp-content/uploads/2013/01/QueueEmpty.png)](http://ifeve.com/wp-content/uploads/2013/01/QueueEmpty.png)



…

一个满的队列：

[![img](http://ifeve.com/wp-content/uploads/2013/01/QueueFull.png)](http://ifeve.com/wp-content/uploads/2013/01/QueueFull.png)



*(校对注：这应该是一个双向队列)*

队列需要保存一个关于大小的变量，以便区分队列是空还是满。否则，它需要根据队列中的元素的内容来判断，这样的话，消费一个节点（Entry）后需要做一次写入来清除标记，或者标记节点已经被消费过了。无论采用何种方式实现，在头、尾和大小变量上总是会有很多竞争，或者如果消费操作移除元素时需要使用一个写操作，那元素本身也包含竞争。

基于以上，这三个变量常常在一个[cache line](http://en.wikipedia.org/wiki/CPU_cache)里面，有可能导致伪分享[false sharing](http://en.wikipedia.org/wiki/False_sharing)。因此，不仅要担心生产者和消费者同时写size变量（或者元素），还要注意由于头指针尾指针在同一位置，当头指针更新时，更新尾指针会导致缓存不命中。这篇文章已经很长了，所以我就不再详述细节了。

这就是我们所说的“分离竞争点问题”或者队列的“合并竞争点问题”。通过将所有的东西都赋予私有的序列号，并且只允许一个消费者写Entry对象中的变量来消除竞争，Disruptor 唯一需要处理访问冲突的地方，是多个生产者写入 Ring Buffer 的场景。

**总结**

Disruptor相对于传统方式的优点：

1. 没有竞争=没有锁=非常快。
2. 所有访问者都记录自己的序号的实现方式，允许多个生产者与多个消费者共享相同的数据结构。
3. 在每个对象中都能跟踪序列号（ring buffer，claim Strategy，生产者和消费者），加上神奇的[cache line padding](http://code.google.com/p/disruptor/source/browse/trunk/code/src/main/com/lmax/disruptor/RingBuffer.java)，就意味着没有为伪共享和非预期的竞争。