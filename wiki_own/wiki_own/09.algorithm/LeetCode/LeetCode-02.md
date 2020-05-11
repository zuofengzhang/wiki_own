# LeetCode-02


## string-to-integer-atoi

请你来实现一个 atoi 函数，使其能将字符串转换成整数。

首先，该函数会根据需要丢弃无用的开头空格字符，直到寻找到第一个非空格的字符为止。

当我们寻找到的第一个非空字符为正或者负号时，则将该符号与之后面尽可能多的连续数字组合起来，作为该整数的正负号；
假如第一个非空字符是数字，则直接将其与之后连续的数字字符组合起来，形成整数。

该字符串除了有效的整数部分之后也可能会存在多余的字符，这些字符可以被忽略，它们对于函数不应该造成影响。

> 注意：假如该字符串中的第一个非空格字符不是一个有效整数字符、字符串为空或字符串仅包含空白字符时，则你的函数不需要进行转换。

在任何情况下，若函数不能进行有效的转换时，请返回 0。

> 说明：
> 假设我们的环境只能存储 32 位大小的有符号整数，那么其数值范围为 $[−2^{31},  2^{31} − 1]$。如果数值超过这个范围，qing返回  $INT\_MAX (2^{31} − 1) 或 INT\_MIN (−2^{31})$ 。

**示例 1:**

    输入: "42"
    输出: 42
    示例 2:

    输入: "   -42"
    输出: -42
    解释: 第一个非空白字符为 '-', 它是一个负号。
         我们尽可能将负号与后面所有连续出现的数字组合起来，最后得到 -42 。
**示例 3:**

    输入: "4193 with words"
    输出: 4193
    解释: 转换截止于数字 '3' ，因为它的下一个字符不为数字。
**示例 4:**

    输入: "words and 987"
    输出: 0
    解释: 第一个非空字符是 'w', 但它不是数字或正、负号。
         因此无法执行有效的转换。
**示例 5:**

    输入: "-91283472332"
    输出: -2147483648
    解释: 数字 "-91283472332" 超过 32 位有符号整数范围。
         因此返回 $INT_MIN (−2^{31})$ 。


### 我的答案

```java
class Solution {
    public int myAtoi(String str) {
        final int max = 2147483647;
        final int min = -2147483648;
        int result = 0;
        int unit = 1;
        boolean containValue = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (containValue) {
                int cv = c - '0';
                if (cv > 9 || cv < 0) {
                    break;
                } else {
                    if (unit == 1 && (result > max / 10 || (result == max / 10 && cv >= 7))) {
                        result = max;
                        break;
                    } else if (unit == -1 && (result > (max / 10) || (result == (max / 10) && cv >= 8))) {
                        result = min;
                        break;
                    }
                    result = result * 10 + cv;
                }
            } else {
                if (c == '+') {
                    containValue = true;
                } else if (c == '-') {
                    unit = -1;
                    containValue = true;
                } else if (c == ' ') {
                    continue;
                } else {
                    int cv = c - '0';
                    if (cv > 9 || cv < 0) {
                        break;
                    } else {
                        result = result * 10 + cv;
                        containValue = true;
                    }
                }
            }
        }
        return unit * result;
    }
}
```

![执行结果](_v_images/20190722150712248_1043780989.png =717x)



## longest-palindromic-substring

给定一个字符串 s，找到 s 中最长的回文子串。你可以假设 s 的最大长度为 1000。

示例 1：

    输入: "babad"
    输出: "bab"
    注意: "aba" 也是一个有效答案。
示例 2：

    输入: "cbbd"
    输出: "bb"


### 中心扩展算法

![中心扩展算法](_v_images/20190724100346564_1210427928.png =780x)

事实上，只需使用恒定的空间，我们就可以在 $O(n^2)$ 的时间内解决这个问题。

我们观察到回文中心的两侧互为镜像。因此，回文可以从它的中心展开，并且只有 $2n - 1$ 个这样的中心。

你可能会问，为什么会是 $2n - 1$ 个，而不是 nn 个中心？原因在于所含字母数为偶数的回文的中心可以处于两字母之间（例如 $\textrm{“abba”}$的中心在两个 $\textrm{‘b’}$ 之间）。


```java
class Solution {
    /**
     *
     * @param s
     * @return
     */
    public String longestPalindrome(String s) {
        if (s.length() == 0) {
            return "";
        }
        int start = 0;
        int end = 0;
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int tm1 = max(s, i, i);
            int tm2 = max(s, i, i + 1);
            length = Math.max(tm1, tm2);
            if (length > end - start) {
                // 取位置时，整体后移了一位
                // 当为偶数时，开始的位置需要减掉
                // 当为奇数是，减掉不影响
                start = i - (length - 1) / 2;
                end = i + length / 2;
            }

        }
        return s.substring(start, end + 1);
    }

    int max(String s, int start, int end) {
        int L = start;
        int R = end;
        while (L >= 0 & R < s.length() && s.charAt(L) == s.charAt(R)) {
            L--;
            R++;
        }
        return R - L - 1;
    }
}
```

### 最快的答案

```java
class Solution {
    public String longestPalindrome(String s) {
        if(s==null||s.length()==0){
            return "";
        }
        // 用以接收返回值，最长串的起始坐标
        int [] range=new int[2];
        char[] str=s.toCharArray();
        for(int i=0;i<s.length();i++){
            i=findLongest(str,i,range);
        }
        return s.substring(range[0], range[1] + 1);
    }
    public static int findLongest(char[] str,int low,int[] range){
        int high=low;
        // 偶数对称
        while(high<str.length-1&&str[high+1]==str[low]){
            high++;
        }
        int ans=high;
        // 奇数对称
        while(low>0&&high<str.length-1&&str[low - 1] == str[high + 1]){
            low--;
            high++;
        }
        // 超过最大长度则更新
        if(high - low > range[1] - range[0]){
            range[0]=low;
            range[1]=high;
        }
        return ans;
    }
}
```


### 动态规划（推荐）

推荐理由：暴力解法太 naive，中心扩散不普适，Manacher 就更不普适了，是专门解这个问题的方法。而用动态规划我认为是最有用的，可以帮助你举一反三的方法。

补充说明：Manacher 算法有兴趣的朋友们可以了解一下，有人就借助它的第一步字符串预处理思想，解决了 LeetCode 第 4 题。因此以上推荐仅代表个人观点。

解决这类 “最优子结构” 问题，可以考虑使用 “动态规划”：

1、定义 “状态”；
2、找到 “状态转移方程”。

记号说明： 下文中，使用记号 s[l, r] 表示原始字符串的一个子串，l、r 分别是区间的左右边界的索引值，使用左闭、右闭区间表示左右边界可以取到。举个例子，当 s = 'babad' 时，s[0, 1] = 'ba' ，s[2, 4] = 'bad'。

1. 定义 “状态”，这里 “状态”数组是二维数组。
    dp[l][r] 表示子串 s[l, r]（包括区间左右端点）是否构成回文串，是一个二维布尔型数组。即如果子串 s[l, r] 是回文串，那么 dp[l][r] = true。
2. 找到 “状态转移方程”。

首先，我们很清楚一个事实：

1. 当子串只包含 1个字符，它一定是回文子串；
2. 当子串包含 2 个以上字符的时候：如果 s[l, r] 是一个回文串，例如 “abccba”，那么这个回文串两边各往里面收缩一个字符（如果可以的话）的子串 s[l + 1, r - 1] 也一定是回文串，即：如果 dp[l][r] == true 成立，一定有 dp[l + 1][r - 1] = true 成立。

根据这一点，我们可以知道，给出一个子串 s[l, r] ，如果 s[l] != s[r]，那么这个子串就一定不是回文串。如果 s[l] == s[r] 成立，就接着判断 s[l + 1] 与 s[r - 1]，这很像中心扩散法的逆方法。

事实上，当 `s[l] == s[r]` 成立的时候，`dp[l][r]` 的值由 `dp[l + 1][r - l]` 决定，这一点也不难思考：
  当左右边界字符串相等的时候，整个字符串是否是回文就完全由“原字符串去掉左右边界”的子串是否回文决定。但是这里还需要再多考虑一点点：“原字符串去掉左右边界”的子串的边界情况。

1. 当原字符串的元素个数为 3个的时候，如果左右边界相等，那么去掉它们以后，只剩下 1 个字符，它一定是回文串，故原字符串也一定是回文串；
2. 当原字符串的元素个数为 2个的时候，如果左右边界相等，那么去掉它们以后，只剩下 0个字符，显然原字符串也一定是回文串。

把上面两点归纳一下，只要 `s[l + 1, r - 1]` 至少包含两个元素，就有必要继续做判断，否则直接根据左右边界是否相等就能得到原字符串的回文性。
而“`s[l + 1, r - 1]` 至少包含两个元素”等价于 `l + 1 < r - 1`，整理得` l - r < -2`，或者 `r - l > 2`。

综上，如果一个字符串的左右边界相等，以下二者之一成立即可：

1. 去掉左右边界以后的字符串不构成区间，即“ `s[l + 1, r - 1]` 至少包含两个元素”的反面，即 `l - r >= -2`，或者 `r - l <= 2`； 
2. 去掉左右边界以后的字符串是回文串，具体说，它的回文性决定了原字符串的回文性。

于是整理成“状态转移方程”：

`dp[l, r] = (s[l] == s[r] and (l - r >= -2 or dp[l + 1, r - 1]))`
或者

`dp[l, r] = (s[l] == s[r] and (r - l <= 2 or dp[l + 1, r - 1]))`

编码实现细节：因为要构成子串 l 一定小于等于 r ，我们只关心 “状态”数组“上三角”的那部分取值。理解上面的“状态转移方程”中的 (`r - l <= 2 or dp[l + 1, r - 1]`) 这部分是关键，因为 or 是短路运算，因此，如果收缩以后不构成区间，那么就没有必要看继续 `dp[l + 1, r - 1]` 的取值。

读者可以思考一下：为什么在动态规划的算法中，不用考虑回文串长度的奇偶性呢。想一想，答案就在状态转移方程里面。

具体编码细节在代码的注释中已经体现。

参考代码：

```Java
public class Solution {

    public String longestPalindrome(String s) {
        int len = s.length();
        if (len <= 1) {
            return s;
        }
        int longestPalindrome = 1;
        String longestPalindromeStr = s.substring(0, 1);
        boolean[][] dp = new boolean[len][len];
        // abcdedcba
        //   l   r
        // 如果 dp[l, r] = true 那么 dp[l + 1, r - 1] 也一定为 true
        // 关键在这里：[l + 1, r - 1] 一定至少有 2 个元素才有判断的必要
        // 因为如果 [l + 1, r - 1] 只有一个元素，不用判断，一定是回文串
        // 如果 [l + 1, r - 1] 表示的区间为空，不用判断，也一定是回文串
        // [l + 1, r - 1] 一定至少有 2 个元素 等价于 l + 1 < r - 1，即 r - l >  2

        // 写代码的时候这样写：如果 [l + 1, r - 1]  的元素小于等于 1 个，即 r - l <=  2 ，就不用做判断了

        // 因为只有 1 个字符的情况在最开始做了判断
        // 左边界一定要比右边界小，因此右边界从 1 开始
        for (int r = 1; r < len; r++) {
            for (int l = 0; l < r; l++) {
                // 区间应该慢慢放大
                // 状态转移方程：如果头尾字符相等并且中间也是回文
                // 在头尾字符相等的前提下，如果收缩以后不构成区间（最多只有 1 个元素），直接返回 True 即可
                // 否则要继续看收缩以后的区间的回文性
                // 重点理解 or 的短路性质在这里的作用
                if (s.charAt(l) == s.charAt(r) && (r - l <= 2 || dp[l + 1][r - 1])) {
                    dp[l][r] = true;
                    if (r - l + 1 > longestPalindrome) {
                        longestPalindrome = r - l + 1;
                        longestPalindromeStr = s.substring(l, r + 1);
                    }
                }
            }
        }
        return longestPalindromeStr;
    }
}
```
写完代码以后，请读者在纸上写下代码运行的流程，以字符串 'babad' 为例：

![](_v_images/20190724101948729_1978928559.png =780x)

（草稿太潦草了，大家将就看吧，懒得绘图了，原因是太麻烦，并且我觉得展示手写草稿可能更有意义一些。）

说明：上面示例代码填写 dp 数组（二维状态数组）是按照“从左到右、从上到下”的方向依次填写的（你不妨打开我上面的 Python 示例代码中的调试语句运行一下验证），当 “ s[l + 1, r - 1] 至少包含两个元素” 即 r - l > 2 时，dp[l, r] 的值要看 d[[l + 1, r - 1] ，即在 r - l > 2 的时候，dp[l, r] 的值看“左下角”的值，只要按照“从左到右、从上到下”的方向依次填写，当 r - l > 2 时，左下角就一定有值，这一点是动态规划算法得以有效的重要原因。

根据一个具体例子，在草稿纸上写下（绘图）代码的运行流程，有时是够加深我们对算法的理解，并且也有助于调试代码。

复杂度分析：

时间复杂度：$O(N^{2})$。
空间复杂度：$O(N^{2})$，
二维 dp 问题，一个状态得用二维有序数对表示，因此空间复杂度是 $O(N^{2})$。


## zigzag-conversion

将一个给定字符串根据给定的行数，以从上往下、从左到右进行 Z 字形排列。

比如输入字符串为 "LEETCODEISHIRING" 行数为 3 时，排列如下：

    L   C   I   R
    E T O E S I I G
    E   D   H   N
之后，你的输出需要从左往右逐行读取，产生出一个新的字符串，比如："LCIRETOESIIGEDHN"。

请你实现这个将字符串进行指定行数变换的函数：

string convert(string s, int numRows);
示例 1:

    输入: s = "LEETCODEISHIRING", numRows = 3
    输出: "LCIRETOESIIGEDHN"
示例 2:

    输入: s = "LEETCODEISHIRING", numRows = 4
    输出: "LDREOEIIECIHNTSG"
    解释:

    L     D     R
    E   O E   I I
    E C   I H   N
    T     S     G

### 我的答案 按行排序
```java
class Solution {

    public String convert(String s, int numRows) {
        if (numRows == 1 || s.length() <= 1) {
            return s;
        }
        int length = s.length();
        int[] hIndics = new int[numRows];
        int[][] values = new int[numRows][];
        for (int i = 0; i < numRows; i++) {
            values[i] = new int[length];
        }
        int hv = 1;
        int vIndex = 0;
        for (int i = 0; i < length; i++) {
            char v = s.charAt(i);
            //本次要写的x坐标
            int x = hIndics[vIndex];
            //本次要写的y坐标
            int y = vIndex;
            values[y][x] = v;
            hIndics[vIndex]++;
            vIndex += hv;
            if (vIndex == numRows) {
                vIndex = numRows - 2;
                hv = -1;
            }
            if (vIndex < 0) {
                vIndex = 1;
                hv = 1;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numRows; i++) {
            final int[] hvalues = values[i];
            for (int j = 0; j < hIndics[i]; j++) {
                final char name = (char) hvalues[j];
                System.out.print(name + "\t");
                sb.append(name);
            }
            System.out.println("");
        }
        return sb.toString();
    }
}
```

![z](_v_images/20190725002729631_958012208.png)

### 按行访问

思路

按照与逐行读取 Z 字形图案相同的顺序访问字符串。

算法

首先访问 行 0 中的所有字符，接着访问 行 1，然后 行 2，依此类推...

对于所有整数 k，

行0 中的字符位于索引$k(2⋅numRows−2)$ 处;
行$numRows−1$ 中的字符位于索引$k(2⋅numRows−2)+numRows−1$ 处;
内部的 行$i$中的字符位于索引$k(2⋅numRows−2)+i$ 以及$(k+1)(2⋅numRows−2)−i$ 处;

```Java
class Solution {
public:
    string convert(string s, int numRows) {

        if (numRows == 1) return s;

        string ret;
        int n = s.size();
        int cycleLen = 2 * numRows - 2;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j + i < n; j += cycleLen) {
                ret += s[j + i];
                if (i != 0 && i != numRows - 1 && j + cycleLen - i < n)
                    ret += s[j + cycleLen - i];
            }
        }
        return ret;
    }
};
```
复杂度分析

时间复杂度：
$O(n)$，其中 n==len(s)。每个索引被访问一次。
空间复杂度：
$O(n)$