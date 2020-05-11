# LeetCode Solution 解题笔记

## 01.two-sum

[两数求和](https://leetcode-cn.com/problems/two-sum/submissions/)

给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。

你可以假设每种输入只会对应一个答案。但是，你不能重复利用这个数组中同样的元素。

示例:

给定 nums = [2, 7, 11, 15], target = 9

因为 nums[0] + nums[1] = 2 + 7 = 9
所以返回 [0, 1]

### 解决方案一: 硬解
```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        for(int i=0;i<nums.length;i++){
            int left=nums[i];
            for(int j=i+1;j<nums.length;j++){
                int right=nums[j];
                if(left+right==target){
                    return new int[]{i,j};
                }
            }
        }
        throw new IllegalArgumentException("No two sum solution");
    }
}
```
### 最优方案: 解读

```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        int indexArrayMax = 2047;
        int[] indexArrays = new int[indexArrayMax + 1];
        int diff = 0;
        for (int i = 0; i < nums.length; i++) {
          diff = target - nums[i];
          if (indexArrays[diff & indexArrayMax] != 0) {
            return new int[]{indexArrays[diff & indexArrayMax] - 1, i};
          }
          indexArrays[nums[i] & indexArrayMax] = i + 1;
        }
        throw new IllegalArgumentException("No two sum value");
    }
}
```

### 问题分析



对于这个问题，先审题了解问题的边界:
1. 数组中的元素是int型
2. target是int型
3. 返回值是int型的两个元素的数组
4. int类型的范围是包括负数、0和正数的

#### Java中的基本类型字节和长度

|   类型  | 字节 |                                 取值范围                                 |
| ------- | --- | ----------------------------------------------------------------------- |
| byte    | 1   | -128(-2^7) ~ 127(2^7-1)                                                |
| short   | 2   | -32768（-2^15）~ 32767（2^15 - 1）                                      |
| int     | 4   | -2,147,483,648（-2^31）~ 2,147,483,647（2^31 - 1）                      |
| long    | 8   | -9,223,372,036,854,775,808(-2^63) ~ 9,223,372,036,854,775,807(2^63-1) |
| float   | 4   | 单精度浮点数字长32位，尾数长度23，指数长度8,指数偏移量127                     |
| double  | 8   | 双精度浮点数字长64位，尾数长度52，指数长度11，指数偏移量1023；                 |
| char    | 2   | 0(\u0000) ~ 65,535(\uffff)                                             |
| boolean | 1   | true/false                                                              |


#### Java中整数存储(原码、反码、补码)

有符号数:  第一位为0标示正数，为1代表是负数



1. 原码
     [+1]原 = 0000 0001
      [-1]原 = 1000 0001
2. 反码
    [+1] = [00000001]原 = [00000001]反
    [-1]  = [10000001]原 = [11111110]反
3. 补码
    [+1] = [00000001]原 = [00000001]反 = [00000001]补
    [-1] = [10000001]原 = [11111110]反 = [11111111]补

计算机中计算减法，是需要`通过加法来实现的`，
1-1 = 1 + (-1) = [0000 0001]原 + [1000 0001]原 = [0000 0001]补 + [1111 1111]补 = [0000 0000]补=[0000 0000]原
(-1) + (-127) = [1000 0001]原 + [1111 1111]原 = [1111 1111]补 + [1000 0001]补 = [1000 0000]补
计算机巧妙地把`符号位参与运算`, 并且将减法变成了加法



#### 匹配数据
判断新数据和旧数据的匹配的关系，一般两种方法：
- 暴力法: 遍历数据，直到找到符合要求的
- Hash法: 将数据都压入内存，或逐个压入内存，然后，逐个遍历并判断数据是否存在


<解法二>采用一个数组，将数据Hash后压入数组
遍历源数组，寻找当前差的位置有没有处理过，处理过，则返回响应的值

该解法巧妙的是使用数组，数组的长度`indexArrayMax`设计巧妙，为什么是`indexArrayMax = 2047`，

在java中，2047是正整数，对应原码和补码一样，都是`11111111111`

然后开了个大小为2048的数组，`int[] indexArrays = new int[indexArrayMax + 1]`，
为的是考虑有0的情况。这个也是预设了样本空间的大小，
如果不够，只要再找一个原码全是1，大小比2047大的就行了。方法很简单，原码多加一个1，就行了。可以用
```java
System.out.println(Integer.toBinaryString(2047));
System.out.println(0b11111111);
```
来找你合适的大小数字。

`diff`，存储差的变量。然后开始循环数组计算差。注意，差有可能是负数！

之后这里是关键
```java
if (indexArrays[diff & indexArrayMax] != 0) {
    return new int[] { indexArrays[diff & indexArrayMax] - 1, i };
}
```
`indexArrays` 这个数组默认初始化都是0，所以用`diff & indexArrayMax`去做下标处理。`diff & indexArrayMax`的结果有三种

- 差是正数，那么结果=差
- 差是0，那么结果还是0（因为用的是补码，不像反码那样区分正负0）
- 差是负数，那么结果是会被转化为0-2047之间的正数，存放到相应的位置，负值越大，越接近2047，反之越接近0。

```java
System.out.println(Integer.toBinaryString(-3));
System.out.println(0b11111111111111111111111111111101);
System.out.println(0b11111111111111111111111111111101 & 2047);
```
其中`11111111111111111111111111111101`是`-3`的补码，与2047 按位与之后，得到的结果是正数2045！可以自己试验一下。

然后继续看`if (indexArrays[diff & indexArrayMax] != 0)`，如果没找，也就是数字对应的位置标记数组值为0，那么

```java
indexArrays[nums[i] & indexArrayMax] = i + 1;
```

说明此位数字（不是差）之前没有计算过，那么它的下标位置+1处理，防止i=0的时候与初始值0区分不开。

反之，如果`if (indexArrays[diff & indexArrayMax] != 0)`成立，那么说明 `nums[i] = diff`之前有记录，就直接取其`记录对应的下标-1`，就是原始输入数组的位置了。

总结，通过额外开空间和将正负数利用补码转化成0-2047之间的正数进行位置记录，省去了一步判断，而且按位与相当于做了一个快速哈希，极大的提高了运行速度。所以，这个解法是目前是最快的。

说说缺点，① 无用的内存开销比较大 ② 数组设置长度不足，存在碰撞的风险


>   **加强位运算的基本功，位运算是最基础且强大的工具**

##  02.add-two-numbers 两数相加

给出两个 非空 的链表用来表示两个非负的整数。其中，它们各自的位数是按照 逆序 的方式存储的，并且它们的每个节点只能存储 一位 数字。

如果，我们将这两个数相加起来，则会返回一个新的链表来表示它们的和。

您可以假设除了数字 0 之外，这两个数都不会以 0 开头。

示例：

输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
输出：7 -> 0 -> 8
原因：342 + 465 = 807


```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        // 进位: 两个个位数相加最多为1，取值范围为0或1
        int x=0;
        // 头指针
        ListNode h=null;
        // 当前节点，游标指针
        ListNode r=h;
        while(l1!=null||l2!=null){
            int v=x;
            // 为空，既不加值，也不游动，l1循环内部没有再使用，利用一次判断
            if(l1!=null){
                v+=l1.val;
                l1=l1.next;
            }
            if(l2!=null){
                v+=l2.val;
                l2=l2.next;
            }
            // v取个位数,两个个位数相加，范围为[0,18]
            // 大于9则进位1，剩下的值减1，取模方法耗性能
            if(v>9){
                x=1;
                v=v-10;
            } else{
                x=0;
            }
            // 判断头结点
            if(h==null){
                h=new ListNode(v);
                r=h;
            }else{
                r.next=new ListNode(v);
                r=r.next;
            }
        }
        // 清理最高位进位
        if(x>0){
            r.next=new ListNode(1);
        }
        return h;
    }
}
```

## 03.longest-substring-without-repeating-characters 无重复字符的最长子串

给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度。

示例 1:

    输入: "abcabcbb"
    输出: 3
    解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
示例 2:

    输入: "bbbbb"
    输出: 1
    解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。
示例 3:

    输入: "pwwkew"
    输出: 3
    解释: 因为无重复字符的最长子串是 "wke"，所以其长度为 3。
         请注意，你的答案必须是 子串 的长度，"pwke" 是一个子序列，不是子串。


### 方法一：暴力法
题目更新后由于时间限制，会出现 TLE。

**思路**

逐个检查所有的子字符串，看它是否不含有重复的字符。

**算法**

假设我们有一个函数 `boolean allUnique(String substring)` ，如果子字符串中的字符都是唯一的，它会返回 `true`，否则会返回 `false`。 我们可以遍历给定字符串 `s` 的所有可能的子字符串并调用函数 `allUnique`。 如果事实证明返回值为 `true`，那么我们将会更新无重复字符子串的最大长度的答案。

现在让我们填补缺少的部分：

为了枚举给定字符串的所有子字符串，我们需要枚举它们开始和结束的索引。假设开始和结束的索引分别为 $i$ 和 $j$。那么我们有 $0 \leq i \lt j \leq n$ 这里的结束索引 $j$ 是按惯例排除的）。因此，使用 $i$从 0 到 $n - 1$ 以及 $j$ 从$i+1$ 到 $n$这两个嵌套的循环，我们可以枚举出 $s$ 的所有子字符串。

要检查一个字符串是否有重复字符，我们可以使用集合。我们遍历字符串中的所有字符，并将它们逐个放入` set` 中。在放置一个字符之前，我们检查该集合是否已经包含它。如果包含，我们会返回 false。循环结束后，我们返回 true。

```Java
public class Solution {
    public int lengthOfLongestSubstring(String s) {
        int n = s.length();
        int ans = 0;
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j <= n; j++)
                if (allUnique(s, i, j)) ans = Math.max(ans, j - i);
        return ans;
    }

    public boolean allUnique(String s, int start, int end) {
        Set<Character> set = new HashSet<>();
        for (int i = start; i < end; i++) {
            Character ch = s.charAt(i);
            if (set.contains(ch)) return false;
            set.add(ch);
        }
        return true;
    }
}
```

**复杂度分析**

时间复杂度：$O(n^3)$。

要验证索引范围在 $[i, j)$ 内的字符是否都是唯一的，我们需要检查该范围中的所有字符。 因此，它将花费 $O(j−i) $的时间。

对于给定的 $i$，对于所有 $j \in [i+1, n] $所耗费的时间总和为：

$\sum_{i+1}^{n}O(j - i)$

因此，执行所有步骤耗去的时间总和为：

$O\left(\sum_{i = 0}^{n - 1}\left(\sum_{j = i + 1}^{n}(j - i)\right)\right) = O\left(\sum_{i = 0}^{n - 1}\frac{(1 + n - i)(n - i)}{2}\right) = O(n^3)$


空间复杂度：$O(min(n, m))$，我们需要 $O(k)$的空间来检查子字符串中是否有重复字符，其中 $k$ 表示 `Set` 的大小。而 `Set` 的大小取决于字符串 $n$ 的大小以及字符集/字母$m$ 的大小。

### 方法二：滑动窗口


![maxLength](_v_images/20190720222950598_653591315.gif =517x)
**算法**

暴力法非常简单，但它太慢了。那么我们该如何优化它呢？

在暴力法中，我们会反复检查一个子字符串是否含有有重复的字符，但这是没有必要的。如果从索引 $i$ 到 $j - 1$ 之间的子字符串 $s_{ij}$
​
已经被检查为没有重复字符。我们只需要检查 $s[j]$ 对应的字符是否已经存在于子字符串 $s_{ij}$中。

要检查一个字符是否已经在子字符串中，我们可以检查整个子字符串，这将产生一个复杂度为 $O(n^2)$的算法，但我们可以做得更好。

通过使用 `HashSet` 作为滑动窗口，我们可以用 O(1)O(1) 的时间来完成对字符是否在当前的子字符串中的检查。

滑动窗口是数组/字符串问题中常用的抽象概念。 窗口通常是在数组/字符串中由开始和结束索引定义的一系列元素的集合，即 $[i, j)$（左闭，右开）。而滑动窗口是可以将两个边界向某一方向“滑动”的窗口。例如，我们将 [i, j)[i,j) 向右滑动 1 个元素，则它将变为 $[i+1, j+1)$（左闭，右开）。

回到我们的问题，我们使用 `HashSet` 将字符存储在当前窗口 $[i, j)$（最初$j = i$）中。 然后我们向右侧滑动索引 jj，如果它不在 `HashSet` 中，我们会继续滑动 $j$。直到 $s[j]$ 已经存在于 `HashSet` 中。此时，我们找到的没有重复字符的最长子字符串将会以索引 $i$ 开头。如果我们对所有的 $i$ 这样做，就可以得到答案。

```Java
public class Solution {
    public int lengthOfLongestSubstring(String s) {
        int n = s.length();
        Set<Character> set = new HashSet<>();
        int ans = 0, i = 0, j = 0;
        while (i < n && j < n) {
            // try to extend the range [i, j]
            if (!set.contains(s.charAt(j))){
                set.add(s.charAt(j++));
                ans = Math.max(ans, j - i);
            }
            else {
                set.remove(s.charAt(i++));
            }
        }
        return ans;
    }
}
```
**复杂度分析**

**时间复杂度**：

$$
O(2n) = O(n)
$$

在最糟糕的情况下，每个字符将被 $i$和 $j$ 访问两次。

**空间复杂度**：

$$
O(min(m, n))
$$

与之前的方法相同。滑动窗口法需要 $O(k)$的空间，其中 $k$ 表示 Set 的大小。而 Set 的大小取决于字符串 $n$ 的大小以及字符集 / 字母 $m$ 的大小。

### 方法三：优化的滑动窗口

上述的方法最多需要执行 2n 个步骤。事实上，它可以被进一步优化为仅需要 n 个步骤。我们可以定义字符到索引的映射，而不是使用集合来判断一个字符是否存在。 当我们找到重复的字符时，我们可以立即跳过该窗口。

也就是说，如果 $s[j]$ 在 $[i, j)$范围内有与 $j'$ ,重复的字符，我们不需要逐渐增加 $i$。 我们可以直接跳过 $[i，j']$范围内的所有元素，并将 $i$变为 $j' + 1$。

```Java
public class Solution {
    public int lengthOfLongestSubstring(String s) {
        int n = s.length(), ans = 0;
        Map<Character, Integer> map = new HashMap<>(); // current index of character
        // try to extend the range [i, j]
        for (int j = 0, i = 0; j < n; j++) {
            if (map.containsKey(s.charAt(j))) {
                i = Math.max(map.get(s.charAt(j)), i);
            }
            ans = Math.max(ans, j - i + 1);
            map.put(s.charAt(j), j + 1);
        }
        return ans;
    }
}
```
（假设字符集为 ASCII 128）

以前的我们都没有对字符串 s 所使用的字符集进行假设。

当我们知道该字符集比较小的时侯，我们可以用一个整数数组作为直接访问表来替换 Map。

常用的表如下所示：

int [26] 用于字母 ‘a’ - ‘z’ 或 ‘A’ - ‘Z’
int [128] 用于ASCII码
int [256] 用于扩展ASCII码

```Java
public class Solution {
    public int lengthOfLongestSubstring(String s) {
        int n = s.length(), ans = 0;
        int[] index = new int[128]; // current index of character
        // try to extend the range [i, j]
        for (int j = 0, i = 0; j < n; j++) {
            i = Math.max(index[s.charAt(j)], i);
            ans = Math.max(ans, j - i + 1);
            index[s.charAt(j)] = j + 1;
        }
        return ans;
    }
}
```
**复杂度分析**

- 时间复杂度：$O(n)$，索引 $j$ 将会迭代 $n$ 次。

- 空间复杂度（HashMap）：$O(min(m, n))$，与之前的方法相同。

- 空间复杂度（Table）：$O(m)$，$m$ 是字符集的大小。

### 最快的解法

```java
class Solution {
    public int lengthOfLongestSubstring(String s) {
        char[] charArr =  s.toCharArray();
        if(charArr.length == 0){
            return 0;
         }
        int maxLength = 0;
        int baseIndex = 0;
        int i;
        // baseIndex 不重复字符串的开始位置
        // i 不重复字符串的结束位置
        for( i = baseIndex + 1; i < charArr.length; i++ ){
            // 遍历最短串
            for(int j = baseIndex; j < i; j++){
                if(charArr[j] == charArr[i]){
                    maxLength = Math.max((i - baseIndex) > maxLength,maxLength);
                    baseIndex = j + 1;
                    break;
                }
            }
        }
        maxLength = Math.max((i - baseIndex) > maxLength, maxLength);
        return maxLength;
    }
}
```


