---
title: 数据结构之栈及其使用
layout: post
date: 2016-04-03 11:29:00
category: DataStruct
tags:
 - Java
 - DataStruct

share: true
comments: true
---

# 栈的应用

## 括号匹配

假设存在三种括号'(){}[]', 成对出现并且嵌套关系正确

### C 语言版本

```c
#include<stdio.h>
/*
* 使用 GCC 4.9.2 编译通过
*/
typedef  struct
{
    char sign[10010];  //栈中的数据都保存在数组
    int top;  //用于标记栈顶元素的位置
} sStack;
/**
* 初始化栈
*/
void initStack(sStack *s)
{
    s->top = -1;
}
/**
*    判空，空返回1，非空返回0
*/
int isStackEmpty(sStack *s)
{
    return s->top == -1 ? 1 : 0;
}
/**
*    将字符c压入栈s
*/
int pushStack(sStack *s, char c)
{
    s->sign[++s->top] = c;
    return 1;
}
/**
*   出栈，
* 成功,返回1;否则 0
*/
int popStack(sStack *s)
{
    if(isStackEmpty(s))
        return 0;
    s->top--;
    return 1;
}
/**
*获取栈顶元素，栈空返回0
*/
char topStack(sStack *s)
{
    if(isStackEmpty(s))
  {
        return 0;
  }
    return s->sign[s->top];
}
/**
* 判断括号是否匹配
*/
int match(char *sample)
{
    int i;
    int location; //出错括号的地址
    sStack stack;  //栈
    static char NOT_MATCH_FLAG=0;//不是匹配字符的标记
    static int ERROR_CHAR_LOCATION = -1;//无法匹配的字符的位置
  int length=strlen(sample); //待处理字符串的长度
  initStack(&stack);//初始化栈
  for(i=0;i<length;i++){
    char c=sample[i];
    if(c == '(' || c == '[' || c == '{')
    {
          pushStack(&stack, c);  //左括号直接压入栈
    }else{
      // 判断是否是右括号
      char matchChar=NOT_MATCH_FLAG;
      switch(c){
        case ')':
          matchChar='(';
          break;
        case ']':
          matchChar='[';
          break;
        case '}':
          matchChar='{';
          break;
      }
      if(matchChar!=NOT_MATCH_FLAG){//是右括号
                if (isStackEmpty(&stack) || topStack(&stack) != matchChar) {
        //没有匹配的元素,报错
                    printf("\' %c \' at %d can not find the match \'%c\'",c,i+1,matchChar);
                    location = i;
                    break;
                } else {
                    //匹配成功，出栈
                    popStack(&stack);
                }
      }
    }
  }
  if (location != ERROR_CHAR_LOCATION) {
        printf("\n%s\n",sample);
        for (i = 0; i < location; i++) {
            printf(" ");
        }
        printf("^\n");//标记错误的位置
        return 0;
    }
    return 1;

}
int main(){
  match("sdjfasjkl(;[f)js]fjsk;ljf");
}
```

### Java版本

实现一个快速的栈

```java
import java.util.Arrays;

/**
 * 栈<br/>
 * 内部采用数组实现，栈顶始终保存在最后。<br/>
 * 当容量不够，可以自己扩容
 * 张作峰
 * 2016-11-03 20:38:53
 */
public class MyStack<E> {
    /**
     * 栈顶的位置 即为保存元素的个数
     */
    private int top = -1;
    /**
     * 真正保存元素的位置
     */
    private transient Object[] items;
    /**
     * 空元素数组，用于初始化
     */
    private static final Object[] EMPTY_ITEMS = {};
    /**
     * 最小容量为10，只要添加第一个元素，保证大小要不小于10
     */
    private final int miniCapacity = 10;

    /**
     * 创建栈，默认为空。添加元素则根据元素个数扩容
     */
    public MyStack() {
        items = EMPTY_ITEMS;
    }

    /**
     * 创建默认大小为<code>size</code>的栈
     *
     * @param size 指定的初始大小：准确预测，避免扩容
     */
    public MyStack(int size) {
        if (size == 0) {
            throw new IllegalArgumentException("item count can not be " + size);
        }
        items = new Object[size];
    }

    /**
     * 出栈
     *
     * @return 栈顶元素
     */
    public E pop() {
        if (isEmpty()) {
            return null;
        }
        return (E) items[top--];
    }

    /**
     * 获取栈顶元素，非出栈
     *
     * @return 栈顶元素
     */
    public E getTopItem() {
        if (isEmpty()) {
            return null;
        }
        return (E) items[top];
    }

    /**
     * 入栈
     *
     * @param e 压入栈的元素
     */
    public void push(E e) {
        final int max = Math.max(miniCapacity, top + 1);
        final int oldSize = items.length;
        if (max > oldSize) {
            int newSize = oldSize + oldSize >> 1;
            if (newSize < max) {
                newSize = max;
            }
            items = Arrays.copyOf(items, newSize);//调用本地函数扩容
        }
        items[++top] = e;
    }

    /**
     * 判空
     *
     * @return 空则true; 非空则false
     */
    public boolean isEmpty() {
        return top == -1;
    }
}
```

二元组

```java
/**
 * 保存两个数据的类
 *
 * @author 张作峰
 *         2016-11-03 20:40:03
 */
public class TwoPairs<T1, T2> {
    private T1 a;
    private T2 b;

    public TwoPairs() {
    }

    /**
     * 保存两个数据的类
     *
     * @param c 数据1
     * @param b 数据2
     */
    public TwoPairs(T1 c, T2 b) {
        this.a = c;
        this.b = b;
    }

    public T1 getA() {
        return a;
    }

    public void setA(T1 a) {
        this.a = a;
    }

    public T2 getB() {
        return b;
    }

    public void setB(T2 b) {
        this.b = b;
    }
}
```

主要的代码

```java
/**
 * 括号匹配工具类
 *
 * @author 张作峰
 *         2016-11-03 20:40:03
 */
public class BracketsMatcher {
    /**
     * 判断传入字符串的匹配情况，
     *
     * @param sample 待判定的字符串
     * @return 0表示正确，N表示第一对出错括号的起始位置（从1开始计数,空格忽略）
     */
    public static int match(String sample) {
        final char NOT_MATCH_FLAG = 0;//不是匹配字符的标记
        final int sampleLength = sample.length();
        final MyStack<TwoPairs<Character, Integer>> stack = new MyStack<TwoPairs<Character, Integer>>();
        int location = 0;
        int charCount = 0; //统计非空格字符的个数
        for (int i = 0; i < sampleLength; i++) {
            final char c = sample.charAt(i);
            if (c != ' ') {//非空格 count 1
                charCount++;
            }
            if ('(' == c || '[' == c || '{' == c) {//左括号 压入栈
                stack.push(new TwoPairs<Character, Integer>(c, charCount));
            } else {//不是左括号
                char matchChar = NOT_MATCH_FLAG;//保存配对的字符：右括号
                switch (c) {
                    case ')':
                        matchChar = '(';
                        break;
                    case ']':
                        matchChar = '[';
                        break;
                    case '}':
                        matchChar = '{';
                        break;
                }
                if (matchChar != NOT_MATCH_FLAG) {//是右括号
                    final TwoPairs<Character, Integer> topItem = stack.getTopItem(); //获取栈顶元素
                    if (topItem == null || topItem.getA() != matchChar) {//不匹配
                        location = charCount;
                        break;
                    } else {//匹配，出栈
                        stack.pop();
                    }
                }
            }
        }
        if (!stack.isEmpty()) {//存在还没有匹配的字符，则删除位置
            return stack.getTopItem().getB();
        }
        return location;
    }

}
```

调用代码

```java
Scanner cin = new Scanner(System.in);
while (cin.hasNext()) {
    String s = cin.nextLine();
    if (s != null && s.length() > 0) {
        System.out.println(BracketsMatcher.match(s));
    }
}
```




