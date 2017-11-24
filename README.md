# mgicode-json

JSON是一种轻量级的数据交换格式,易于人阅读和编写,同时也易于机器解析和生成。目前也有很多工具类型进行JSON格式的解析转换工作，比如JsonLib，jackson,gson等，但是这些工具难于过滤不需要实体的属性或者对难于进行属性名变换。

mgicode-json是可配置表达式的JSON格式转换工具，通过配置表达式的方式来进行JSON格式转换，只要符合表达式的规则的属性可以进行JSON转换，另外可配置表达式还可能对实体属性名进行批量的配置变换。这样通过可配置表达式只要简单地制定规则，就可以过滤不需要传输的数据，直接进行命名转换，使用JSON转换更为方便



## 核心类图

![logo](https://github.com/mgicode/mgicode-json/blob/master/pic/mgicodejson.png)

图1 核心类图



## 基本build使用

```
List list = messageService.search(null, e);
String str =  new JsonCreator().build(list);
```
![logo](https://github.com/mgicode/mgicode-json/blob/master/pic/p2.png)


## buildArr
![logo](https://github.com/mgicode/mgicode-json/blob/master/pic/p3.png)


## exclude

```
String str = new JsonCreator().exclude("user").build(list);

```

![logo](https://github.com/mgicode/mgicode-json/blob/master/pic/p4.png)


## only

```
String s = new JsonCreator().only("code", "data", "id", "name").build(rd);

```
![logo](https://github.com/mgicode/mgicode-json/blob/master/pic/p5.png)


## replace

```
new JsonCreator().replace("name", "text").buildArr(dataList);

String str = new JsonCreator().replace("assetType.typename", "assetTypeName").exclude("assetType").build(up);

```
![logo](https://github.com/mgicode/mgicode-json/blob/master/pic/p6.png)


![logo](https://github.com/mgicode/mgicode-json/blob/master/pic/p7.png)

![logo](https://github.com/mgicode/mgicode-json/blob/master/pic/p8.png)


## add

![logo](https://github.com/mgicode/mgicode-json/blob/master/pic/p9.png)
