# program-01

```java
//有一个字符串它的构成是词+空格的组合，如“北京 杭州 杭州 北京”， 
//要求输入一个匹配模式（简单的以字符来写）， 比如 aabb, 来判断该字符串是否符合该模式， 举个例子：
//1. pattern = "abba", str="北京 杭州 杭州 北京" 返回 ture
//2. pattern = "aabb", str="北京 杭州 杭州 北京" 返回 false
//3. pattern = "baab", str="北京 杭州 杭州 北京" 返回 ture

public class Solution {
    public boolean wordPattern(String pattern, String str) {
          if (str == null || str.isEmpty() || pattern == null || pattern.isEmpty()) {
            return false;
          }
          List<String> allPatternPairs = Arrays.stream(pattern.split("|")).distinct().collect(Collectors.toList());
          List<String> allValues = Arrays.stream(str.split(" ")).distinct().collect(Collectors.toList());
          if (allPatternPairs.size() != allValues.size()) {
              return false;
          }
          String objStr = str;
          for (int i = 0; i < allPatternPairs.size(); i++) {
              objStr = objStr.replaceAll(allValues.get(i), allPatternPairs.get(i));
          }
          return pattern.equals(objStr.replaceAll(" ", ""));
        }
}
```

```java
public static boolean wordPattern(String pattern, String str) {
    if (str == null || str.isEmpty() || pattern == null || pattern.isEmpty()) {
        return false;
    }

    List<String> allPatternPairs = Arrays.stream(pattern.split("|")).distinct().collect(Collectors.toList());
    List<String> allValues = Arrays.stream(str.split(" ")).distinct().collect(Collectors.toList());
    if (allPatternPairs.size() != allValues.size()) {
        return false;
    }
    Map<String, String> map = new HashMap<>(allPatternPairs.size());
    for (int i = 0; i < allPatternPairs.size(); i++) {
        map.put(allValues.get(i), allPatternPairs.get(i));
    }
    StringBuilder builder = new StringBuilder();
    for (String s : str.split(" ")) {
        builder.append(map.get(s));
    }
    return builder.toString().equals(pattern);
}
```