---
title: 数据结构之排序和搜索算法
layout: post
date: 2016-04-03 11:29:00
category: DataStruct
tags:
 - Java
 - DataStruct

share: true
comments: true
---

# 排序算法

各种排序算法时间复杂度

| 排序算法 |	平均时间复杂度  |
|   ---   |     ---     |
| 插入排序 |	O(n^2)     |
| 希尔排序 |	O(n^1.5)   |
| 冒泡排序 |	O(n^2)     |
| 选择排序 |	O(n^2)     |
| 快速排序 |	O(N*logN)  |
| 归并排序 |	O(N*logN)  |
| 堆排序   |	 O(N*logN)  |
| 基数排序 |	O(d(n+r))  |

## 插入排序(直接插入排序、希尔排序)
插入排序,不是通过交换实现排序的, 而是找到合适的位置插入元素来排序的.

   在下面的例子中, 由于排序的元素是存储在数组中的, 因此在找到合适的位置时, 需要后续的元素后移, 故也使用了交换.

## 直接插入排序
1. 排序的基本思想:

  在要排序的一组数中，假定前n-1个数已经排好序，现在将第n个数插到前面的有序数列中，使得这n个数也是排好顺序的。如此反复循环，直到全部排好顺序。(在实现上，拿着第n个数与前面的数逐个比较和交换，直到大于第n个数的数)
1. 排序的过程:

    ![][p-InsertionSortProcess1]

1. java代码实现:
  ```java
  public int[] insertionSort(int[] source) {
    int length = source.length;
    for (int i = 0; i < length; i++) {
        int target = source[i];// 要插入的元素
        int j = i;
        while (j > 0 && target < source[j - 1]) {
            source[j] = source[j - 1];
            j--;
        }
        source[j] = target;
    }
    return source;
  }
  ```
1. 平均时间复杂度: O(n^2)
  最差的情况下， 数组是逆序排列的，必须比较和交换所有的数，时间复杂度是`n×n`, 即`O(n^2)`

## 希尔排序
希尔排序是插入排序的一种高效率的实现，也叫缩小增量排序。简单的插入排序中，如果待排序列是正序时，时间复杂度是O(n)，如果序列是基本有序的，使用直接插入排序效率就非常高。希尔排序就利用了这个特点。基本思想是：先将整个待排记录序列分割成为若干子序列分别进行直接插入排序，待整个序列中的记录基本有序时再对全体记录进行一次直接插入排序。

1. 基本思想：
  在要排序的一组数中，根据某一增量分为若干子序列，并对子序列分别进行插入排序。
  然后逐渐将增量减小,并重复上述过程。直至增量为1,此时数据序列基本有序,最后进行插入排序。
1. 过程

  ![][p-ShellSortProcess02]
  ![][p-ShellSortProcess01]

1. java代码实现
  ```java
  public int[] shellSort(int[] source) {
        int length = source.length;
        int h = length;//增量

        while ((h = h / 2) > 0) {
            for (int i = h; i < length; i+=h) {
                int j = i - h;
                int temp = source[i];
                while (j >= 0 && source[j] > temp) {
                    source[j + h] = source[j];
                    j -= h;
                }
                if (j != i - h) {
                    source[j + h] = temp;
                }
            }
        }
        return source;
    }
  ```
希尔排序的时间复杂度是: `O(n^1.5)`


## 选择排序(直接选择排序、堆排序)

## 直接选择排序
1. 基本思想

  从待排序的序列中选择最小的元素放在待排序的位置

1. 过程

  ![][p-SelectionSort01]

1. 代码实现
  ```java
  public int[] SelectSort(int[] source) {
      int length = source.length;
      for (int i = 0; i < length; i++) {
          int minValue = source[i];
          int minIndex = i;
          for (int j = i; j < length; j++) {
              if (minValue > source[j]) {
                  minValue = source[j];
                  minIndex = j;
              }
          }
          if (minIndex != i) {
              int temp = source[i];
              source[i] = source[minIndex];
              source[minIndex] = temp;
          }
      }
      return source;
  }
  ```

## 堆排序
堆排序是一种选择排序, 堆排序是利用堆的性质, 从父节点和左右子节点中选择最大或者最小的元素.
并且这种比较是从最后一个非叶子节点开始, 逐个比较直到根节点.

### 堆的定义

![][p-heap]

### 堆的性质
一个长度为n的数组, 构造成`完全二叉树`(首元素序号为0)
1. 最后一个非叶子节点的序号是n/2的向下取整.
1. i节点的父节点下标为`(i-1)/2`, i节点的左右子节点下标分别为: `2*i+1`和`2*i+2`

### 堆排序的原理
1. 构建堆:
 + 从最后一个非叶子节点开始, 比较父节点和左右子节点, 选择其中最大或者最小值放在父节点.  每次减一直到根节点.
1. 堆排序:
 + 将堆分为有序曲和无序区
 + 增序排序需要构建小顶堆

![][p-heap-sort]

### 代码实现


```java
//堆筛选数
//已知H[start~end]中除了start之外均满足堆的定义
//本函数进行调整，使H[start~end]成为一个大顶堆
void HeapAdjust(int H[], int start, int end) {
   int temp = H[start];
   for (int i = 2 * start + 1; i < end; i *= 2) {
       //因为假设根结点的序号为0而不是1，所以i结点左孩子和右孩子分别为2i+1和2i+2
       if (i < end && H[i] < H[i + 1])//左右孩子的比较
       {
           ++i;//i为较大的记录的下标
       }
       if (temp > H[i])//左右孩子中获胜者与父亲的比较
       {
           break;
       }
       //将孩子结点上位，则以孩子结点的位置进行下一轮的筛选
       H[start] = H[i];
       start = i;
   }
   H[start] = temp; //插入最开始不和谐的元素}
}
int[] HeapSort(int A[], int n) {
   //先建立大顶堆
   for (int i = n / 2; i >= 0; --i) {
       HeapAdjust(A, i, n);
   }
   //进行排序
   for (int i = n - 1; i > 0; --i) {
       //最后一个元素和第一元素进行交换
       int temp = A[i];
       A[i] = A[0];
       A[0] = temp;
       //然后将剩下的无序元素继续调整为大顶堆
       HeapAdjust(A, 0, i - 1);
   }
   return A;
}
```

### 平均时间复杂度

由于每次重新恢复堆的时间复杂度为O(logN)，共N - 1次重新恢复堆操作，再加上前面建立堆时N / 2次向下调整，每次调整时间复杂度也为O(logN)。二次操作时间相加还是O(N * logN)。


## 交换排序(冒泡排序、快速排序)

## 冒泡排序
### 基本思想
两个数比较, 较大的数下沉,较小的就冒出来

![][p-bubble-sort]

### 代码实现

```java
int[] bubbleSort(int[] a) {
    int length = a.length;
    for (int i = length - 1; i > 0; i--) {
        for (int j = 0; j < i; j++) {
            if (a[j] > a[j + 1]) {
                int temp = a[j];
                a[j] = a[j + 1];
                a[j + 1] = temp;
            }
        }
    }
    return a;
}
```

## 快速排序
### 基本思想
选择一个基准元素, 通过一趟排序将所有的元素分为两部分, 一部分比基准元素大, 另一部分比基准元素小.
然后再对两部分元素用同样的方法排序.

![][p-quick-sort]

### 代码实现

```java
public static void quickSort(int a[],int l,int r){
   if(l>=r)
     return;
   int i = l; int j = r; int key = a[l];//选择第一个数为key
   while(i<j){
       while(i<j && a[j]>=key)//从右向左找第一个小于key的值
           j--;
       if(i<j){
           a[i] = a[j];
           i++;
       }
       while(i<j && a[i]<key)//从左向右找第一个大于key的值
           i++;
       if(i<j){
           a[j] = a[i];
           j--;
       }
   }
   //i == j
   a[i] = key;
   quickSort(a, l, i-1);//递归调用
   quickSort(a, i+1, r);//递归调用
}
```

### 时间复杂度
O(N*logN)

## 归并排序(Merge Sort)
### 基本思想
归并排序是建立在归并操作上的一种有效的排序算法。该算法是采用分治法的一个非常典型的应用。
首先考虑下如何将2个有序数列合并。这个非常简单，只要从比较2个数列的第一个数，谁小就先取谁，取了后就在对应数列中删除这个数。然后再进行比较，如果有数列为空，那直接将另一个数列的数据依次取出即可。

![][p-merge-sort]

### 代码实现

```java
public static void merge_sort(int a[],int first,int last,int temp[]){
  if(first < last){
      int middle = (first + last)/2;
      merge_sort(a,first,middle,temp);//左半部分排好序
      merge_sort(a,middle+1,last,temp);//右半部分排好序
      mergeArray(a,first,middle,last,temp); //合并左右部分
  }
}

//合并 ：将两个序列a[first-middle],a[middle+1-end]合并
public static void mergeArray(int a[],int first,int middle,int end,int temp[]){     
  int i = first;
  int m = middle;
  int j = middle+1;
  int n = end;
  int k = 0;
  while(i<=m && j<=n){
      if(a[i] <= a[j]){
          temp[k] = a[i];
          k++;
          i++;
      }else{
          temp[k] = a[j];
          k++;
          j++;
      }
  }     
  while(i<=m){
      temp[k] = a[i];
      k++;
      i++;
  }     
  while(j<=n){
      temp[k] = a[j];
      k++;
      j++;
  }
  for(int ii=0;ii<k;ii++){
      a[first + ii] = temp[ii];
  }
}
```
### 时间复杂度
O(N*logN)

## 基数排序(RadixSort)

### 基本思想
基数排序（以整形为例），将整形10进制按每位拆分，然后从低位到高位依次比较各个位。主要分为两个过程：
+ (1) 分配，先从个位开始，根据位值(0-9)分别放到0~9号桶中（比如53,个位为3，则放入3号桶中）
+ (2) 收集，再将放置在0~9号桶中的数据按顺序放到数组中

重复(1)(2)过程，从个位到最高位（比如32位无符号整形最大数4294967296，最高位10位）

![][p-radix-sort]

### 代码实现

```java
void RadixSort(int A[],int temp[],int n,int k,int r,int cnt[]){
   //A:原数组
   //temp:临时数组
   //n:序列的数字个数
   //k:最大的位数2
   //r:基数10
   //cnt:存储bin[i]的个数
   for(int i=0 , rtok=1; i<k ; i++ ,rtok = rtok*r){
       //初始化
       Arrays.fill(cnt, 0);
       //计算每个箱子的数字个数
       for(int j=0;j<n;j++){
           cnt[(A[j]/rtok)%r]++;
       }
       //cnt[j]的个数修改为前j个箱子一共有几个数字
       for(int j=1; j<r; j++){
           cnt[j] = cnt[j-1] + cnt[j];
       }
       for(int j = n-1; j>=0; j--){      //重点理解
           cnt[(A[j]/rtok)%r]--;
           temp[cnt[(A[j]/rtok)%r]] = A[j];
       }
       for(int j=0; j<n; j++){
           A[j] = temp[j];
       }
   }
}
```

### 时间复杂度
`O(d(n+r))` (d即表示整形的最高位数)

# 搜索算法

## 顺序查找
逐个遍历

```java
int search(int a[], int key){
  for (int i=0;i<a.length; i++ ) {
    if (a[i]==key) {
      return i;
    }    
  }
  return -1;
}
```

## 二分查找
对于已经排序的序列,采用二分查找

```java
int search(int a[],int key){
  int low=0, high=a.length-1, middle;
  while(low<=high){
    middle=(low+high)/2;
    if (x[middle]==key) {
      return middle;
    }else if (x[middle]<key) {
      low=middle+1;
    }else{
      high=middle-1;
    }
  }
  return -1;
}
```

## 哈希查找

```java
int[] hash = new int[13];
void InsertHash() {
     // 除法取余法
     for (int i = 0; i < array.length; i++) {
         int value = array[i];
         int address = value % m;
         while (hash[address] != 0) {
             address = (++address) % m;
         }
         hash[address] = value;
     }
 }

 public static int SearchHash(int key) {
     // 开放地址法
     int address = key % m;
     while (hash[address] != 0 && hash[address] != key) {
         address = (++address) % m;
     }
     if (hash[address] == 0) {
         return -1;
     }
     return address;
 }
```



----
参考文献

1. [排序算法总结](http://www.jianshu.com/p/ae97c3ceea8d)
1. [八大排序算法 ](http://blog.csdn.net/hguisu/article/details/7776068)
1. [堆排序 Heap Sort](http://www.cnblogs.com/mengdd/archive/2012/11/30/2796845.html)
1. [顺序查找，二分查找，哈希查找 ](http://blog.csdn.net/smallsun_229/article/details/40648619)

[p-InsertionSortProcess1]: /images/DataStruct/SortAndSearch/InsertionSortProcess.png
[p-ShellSortProcess01]: /images/DataStruct/SortAndSearch/ShellSortProcess01.png
[p-ShellSortProcess02]: /images/DataStruct/SortAndSearch/ShellSortProcess02.png
[p-SelectionSort01]: /images/DataStruct/SortAndSearch/Selection_sort_animation.gif
[p-heap]: /images/DataStruct/SortAndSearch/heap.png
[p-heap-sort]: /images/DataStruct/SortAndSearch/Sorting_heapsort_anim.gif
[p-bubble-sort]: /images/DataStruct/SortAndSearch/Bubble_sort_animation.gif
[p-quick-sort]: /images/DataStruct/SortAndSearch/Sorting_quicksort_anim.gif
[p-merge-sort]: /images/DataStruct/SortAndSearch/Merge_sort_animation2.gif
[p-radix-sort]: /images/DataStruct/SortAndSearch/radix_sort_sample.png
