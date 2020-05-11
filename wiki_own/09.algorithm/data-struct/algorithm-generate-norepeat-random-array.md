---
title: Java 生成一组不重复的随机数
layout: post
date: 2016-01-26 00:00:00
category: Java
tags:
 - Java
 - random
 - stack

share: true
comments: true
---

- `create @ 2016年1月26日 11:12`


以前生成不重复随机数组的时候采用的方法是建立一个结果集，每生成一个新的随机数，就和结果集中的数比较，来做到不重复。

# 每生成一个与结果集比较

```java
    public int[] randomArrayWithObjectCollection(int min, int max, int count) {
        int[] result = new int[count];

        Random rd = new Random();
        for (int i = 0; i < count; i++) {
            int n = 0;

            while (true) {
                boolean flag = true;
                n = rd.nextInt(max - min) + min;
                for (int j = 0; j < i; j++) {
                    if (result[j] == n) {
                        flag = false;
                    }
                }
                if (flag) {
                    break;
                }
            }

            result[i] = n;
        }
        return result;
    }

```
1. 随机生成一个数n
2. n 与结果集的每一个数比较，不同则添加
3. 重复1，直到结果集达到指定的大小



# 采用递减采样长度
```java
/**
 * 随机指定范围内N个不重复的数
 * 在初始化的无重复待选数组中随机产生一个数放入结果中，
 * 将待选数组被随机到的数，用待选数组(len-1)下标对应的数替换
 * 然后从len-2里随机产生下一个随机数，如此类推
 * @param max  指定范围最大值
 * @param min  指定范围最小值
 * @param n  随机数个数
 * @return int[] 随机数结果集
 */  
public  int[] randomArray(int min,int max,int n){  
    int len = max-min+1;  

    if(max < min || n > len){  
        return null;  
    }  

    //初始化给定范围的待选数组  
    int[] source = new int[len];  
       for (int i = min; i < min+len; i++){  
        source[i-min] = i;  
       }  

       int[] result = new int[n];  
       Random rd = new Random();  
       int index = 0;  
       for (int i = 0; i < result.length; i++) {  
        //待选数组0到(len-2)随机一个下标  
           index = Math.abs(rd.nextInt() % len--);  
           //将随机到的数放入结果集  
           result[i] = source[index];  
           //将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换  
           source[index] = source[len];  
       }  
       return result;  
}  
```
过程如下：
1. 首先建立两个集合：一个是结果集，一个是采样集合（初始化为取值范围的所有的数）。len初始值为采样集合的长度。
2. 从采样集合中的前len个随机选择一个数num
3. 将num放入结果集
4. num与采样集合的第len-1个数互换
5. len自减1
6. 重复2直到生成的随机数个数足够。

perfect!


## 采用双向链表作为采样集合

1. 构建一个双向链表
2. 初始化双向链表，作为采样的集合
3. 从双向链表中，随机选取一个数，输出并从双向链表中删除




