# Python

## 三元云算符
为什么提示报错，一般情况下是三元运算符的整体类型引起的。
```python
a=10
b=11
str01=""
str01=a<b?True:False   # 返回值是Boolean型，不是str类型，需要强转
str01=str(a<b?True:False )
```


##  urllib2 发起 Http请求

```python
class HttpUtil:
    @staticmethod
    def get(url):
        req = urllib2.Request(url)
        res_data = urllib2.urlopen(req)
        res = res_data.read()
        return res
        
    @staticmethod
    def post(url, data):
        req = urllib2.Request(url=url, data=json.dumps(data))
        res_data = urllib2.urlopen(req)
        return res_data.read()
        
    @staticmethod
    def post2(url, param, header):
        req = urllib2.Request(url=url, data=urllib.urlencode(param), headers=header)
        res_data = urllib2.urlopen(req)
        return res_data.read()
```