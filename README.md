Xlore2.0
------

Xlore2.0 code. (包括BaiduExtractor, HudongExtractor, WikiExtractor, XloreData, XloreWeb.)


******



# BaiduExtractor
Baidu数据处理
- 输入：百度html文件
- 输出：百度的dump文件/分离出的属性文件
- 代码：Xlore2.0\src\BaiduExtractor，Xlore2.0\other

### Step1. BaiduExtractor
##### 抽取结构化信息
> 输入：给定的原 .json 文件。（位于66服务器的移动硬盘上，需 cp 到 66 挂载在68 的共享空间。一次不要太多，6-7个）
>> ```
>>  {"url":xxxx,"html":yyyy,"title":zzzz(可以没有)}
>>  ...[空行分隔]
>>  {"url":xxxx,"html":yyyy,"title":zzzz(可以没有)}
>> ```
> 输出：对应每个 .json 的抽取结果
>> ```
>>  {"images":[],"references":[],"infobox":{},"description":"","title":{"h1":"","h2":""},"url":"","content":"...","tags":[],"outline":{},"synonym":{},"polyseme":[],"links":[],"statistics":{"edit_times":"","creator":"","pv":"","last_modified":""}}
>>  ...[一行一条数据]
>>  {"images":[],"references":[],"infobox":{},"description":"","title":{"h1":"","h2":""},"url":"","content":"...","tags":[],"outline":{},"synonym":{},"polyseme":[],"links":[],"statistics":{"edit_times":"","creator":"","pv":"","last_modified":""}}
>> ```

  执行命令
```Bash
ant BaiduExtractor -DinputDir="/mnt/server66/lockeData/" -DoutputDir="/home/peter/BaiduBaikeDataProcess/result/"
```

### Step2. Dereplication
##### 数据去重
Java 调用 Python 脚本（可直接用 Python 运行 Xlore2.0\other\Dereplication.py）
（为不占用太多内存，修改 Xlore2.0\other\Dereplication.py 注释区间，5个步骤逐步运行）

执行命令
```Bash
ant Dereplication
```

### Step3. BDKG
##### 统计数据信息
> 输入：Step2 生成的 .json 文件
>> ```
>>  {"images":[],"references":[],"infobox":{},"description":"","title":{"h1":"金山街道","h2":"（河南省鹤壁市淇滨区金山街道）"},"url":"http://baike.baidu.com/subview/864132/7421230.htm","content":"...","tags":[],"outline":{"2~沿革":{},"3~行政区划":{},"1~概况":{}},"synonym":{},"polyseme":[],"links":[],"statistics":{"edit_times":"","creator":"","pv":"","last_modified":""}}
>>  ...[一行一条数据]
>>  {"images":[],"references":[],"infobox":{},"description":"","title":{"h1":"","h2":""},"url":"","content":"...","tags":[],"outline":{},"synonym":{},"polyseme":[],"links":[],"statistics":{"edit_times":"","creator":"","pv":"","last_modified":""}}
>> ```
> 输出：命令行输出数据统计信息(reference running time: 7'53'')
>> ```
>>  [java] \_______________Statistics_______________
>>  [java] urlSet	8934852
>>  ···
>>  [java] pageCount	8945131
>>  [java] \_______________End_______________
>>  [java] Done! :D
>> ```

执行命令
```Bash
ant BDKG -Dtype=getStatistics -DinputFile="/home/peter/BaiduBaikeDataProcess/key_url_title_final.result.json"
```

##### 生成 Dump 文件
> 输入：Step2 生成的 .json 文件
> 输出：Dump 文件(reference running time: 12'9'')
>> ```
>>  Title: h1#####h2
>>  URL: url
>>  Summary: texttexttext
>>  Infobox: key::=val::;key::=val
>>  Category: cate::;cate
>>  Innerlink: [[xxx||/view/18624.htm]]::;[[yyy||/view/4273527.htm]]
>>  FirstImage: [[xxx||url]]
>>  Images: [[xxx||url]]::;[[xxx||url]]
>>  FullText: texttexttext
>>  Outline: {"2~阿里巴巴亲友日":{},"3~阿里日":{"3_1~由来":{},"3_4~第九届阿里日":{},"3_2~特权":{},"3_3~决定":{}},"1~拳王阿里日":{"1_2~原因":{},"1_1~日期":{}}}
>>  ExternalLink: [[ref1||url]]::;[[ref2||url]]
>>  Synonym: xxx::;xxx
>>  Statistics: {"edit_times":"","creator":"","pv":"","last_modified":""}
>>  Polyseme: yyy::;yyy
>>  Tags: [[xxx||url]]::;[[yyy||url]]
>>  ...[空行分隔]
>> ```

执行命令
```Bash
ant BDKG -Dtype=getDumpFile -DinputFile="/home/peter/BaiduBaikeDataProcess/key_url_title_final.result.json" -DoutputFile="/home/peter/BaiduBaikeDataProcess/baidu-dump"
```

##### 生成属性文件
> 输入：Step2 生成的 .json 文件
> 输出：分离的属性文件(reference running time: 9'5'')
>> ```
>> $ ls -l separatedAttrs/
>>      baidu-abstract.dat
>>      baidu-conceptList-all.dat
>>      baidu-innerLink.dat
>>      baidu-instance-concept.dat[]
>>      baidu-instanceList-all.dat
>>      baidu-instanceof.dat[]
>>      baidu-propertyList-all.dat
>>      baidu-subclassof.dat(x)
>>      baidu-title-property.dat
>>      baidu-url.dat
>>      baidu-synonym.dat
>> ``` 

执行命令
```Bash
ant BDKG -Dtype=getSeparateAttrs -DinputFile="/home/peter/BaiduBaikeDataProcess/key_url_title_final.result.json" -DoutputDir="/home/peter/BaiduBaikeDataProcess/separatedAttrs/"
```
