---
title: Java数组
layout: post
date: 2016-06-09 12:26:00
category: Java
tags:
- Java
share: true
comments: true
---

# 1.  声明一个数组

```java
String[] aArray = new String[5];  
String[] bArray = {"a","b","c", "d", "e"};  
String[] cArray = new String[]{"a","b","c","d","e"};  
```

# 2.  输出一个数组

```java
int[] intArray = { 1, 2, 3, 4, 5 };  
String intArrayString = Arrays.toString(intArray);  
// print directly will print reference value  
System.out.println(intArray);  
// [I@7150bd4d  
System.out.println(intArrayString);  
// [1, 2, 3, 4, 5]  
```

# 3.  从一个数组创建数组列表

```java
String[] stringArray = { "a", "b", "c", "d", "e" };  
ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(stringArray));  
System.out.println(arrayList);  
// [a, b, c, d, e]  
```

# 4.  检查一个数组是否包含某个值

```java
String[] stringArray = { "a", "b", "c", "d", "e" };  
boolean b = Arrays.asList(stringArray).contains("a");  
System.out.println(b);  
// true  
```

# 5.  连接两个数组

```java
int[] intArray = { 1, 2, 3, 4, 5 };  
int[] intArray2 = { 6, 7, 8, 9, 10 };  
// Apache Commons Lang library  
int[] combinedIntArray = ArrayUtils.addAll(intArray, intArray2);  
```

# 6.  声明一个内联数组（Array inline）

```java
method(new String[]{"a", "b", "c", "d", "e"});  
```

# 7.  把提供的数组元素放入一个字符串

```java
// containing the provided list of elements  
// Apache common lang  
String j = StringUtils.join(new String[] { "a", "b", "c" }, ", ");  
System.out.println(j);  
// a, b, c  
```

# 8.  将一个数组列表转换为数组

```java
String[] stringArray = { "a", "b", "c", "d", "e" };  
ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(stringArray));  
String[] stringArr = new String[arrayList.size()];  
arrayList.toArray(stringArr);  
for (String s : stringArr)  
    System.out.println(s);  
```

# 9.  将一个数组转换为集（set）

```java
Set<String> set = new HashSet<String>(Arrays.asList(stringArray));  
System.out.println(set);  
//[d, e, b, c, a]  
```

# 10.  逆向一个数组

```java
int[] intArray = { 1, 2, 3, 4, 5 };  
ArrayUtils.reverse(intArray);  
System.out.println(Arrays.toString(intArray));  
//[5, 4, 3, 2, 1]  
```

# 11.  移除数组中的元素

```java
int[] intArray = { 1, 2, 3, 4, 5 };  
int[] removed = ArrayUtils.removeElement(intArray, 3);//create a new array  
System.out.println(Arrays.toString(removed));  
```

# 12.  将整数转换为字节数组

```java
byte[] bytes = ByteBuffer.allocate(4).putInt(8).array();  
for (byte t : bytes) {  
   System.out.format("0x%x ", t);  
} 
``` 





