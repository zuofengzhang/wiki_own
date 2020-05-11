---
title: 迭代器模式
layout: post
date: 2016-09-18 00:00:00
category: DesignPattern
tags:
 - Java
 - Design Pattern
 - Iterator Pattern

share: true
comments: true
---

    本文主要介绍迭代器模式以及使用

# 迭代器模式

# Collection中的Iterator



# Java中的迭代器

```java
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestList {
    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        list.add("a");
        list.add("a");
        list.add("b");
        list.add("b");
        list.add("c");
        list.add("c");
        list.add("d");
        list.add("e");
        list.add("f");

        list.remove("a");
        Iterator<String> iter = list.iterator();
        while (iter.hasNext()) {
            String s = iter.next();
            if (s.equals("a")) {
                iter.remove();
            }
        }
        for (String ele : list) {
            System.out.println(ele);
        }
    }
}
```

- 在调用`list.iterator()`时，迭代器会持有`list`对象，当在迭代器中删除一个item，会直接删除掉这个元素。
- 这就是边迭代边删除元素的方法
- Iterator.hasNext() 判断是否有下一个元素, 该方法可以反复调用多次
