---
title: pinyin4j
layout: post
date: 2016-07-13 08:58:00
category: Java
tags:
 - Java
 - pinyin

share: true
comments: true
---

```java
String cn2Spell(String chinese) {
    if (chinese == null || chinese.equals("")) {
        return "";
    } else {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] str = PinyinHelper.toHanyuPinyinStringArray(
                            arr[i], defaultFormat);
                    if (str == null || str.length == 0) {
                        break;
                    }
                    pybf.append(PinyinHelper.toHanyuPinyinStringArray(
                            arr[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    break;
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString();
    }
}
```