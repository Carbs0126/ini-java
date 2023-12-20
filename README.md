# ini文件解析
## ini文件格式说明
https://zh.wikipedia.org/wiki/INI%E6%96%87%E4%BB%B6

``` java
; last modified 1 April 2001 by John Doe
[owner]
name=John Doe
organization=Acme Products

[database]
server=192.0.2.42 ; use IP address in case network name resolution is not working
port=143
file="acme payroll.dat"
```

## 附加说明
1. 键值对中，允许值中存在空格；
2. section 没有做嵌套处理


## 使用
```java
// 将ini文件中的内容转换成为一个INIObject
INIObject iniObject = INIFileParser.parseFileToINIObject(getINITestFile());
// 动态增加section
/*INISectionObject sectionObject = new INISectionObject();
INISectionHeader sectionHeader = new INISectionHeader("[new_section]", null);
sectionObject.setSectionHeader(sectionHeader);
INIEntryObject entryObject0 = new INIEntryObject(new INIKVPair("new_key", "new_value", null));
INIEntryObject entryObject1 = new INIEntryObject(new INIKVPair("new_key1", "new_value1", null));
sectionObject.addEntryObject(entryObject0);
sectionObject.addEntryObject(entryObject1);
iniObject.addSection(sectionObject);*/
// 将iniobject输出
INIFileGenerator.generateFileFromINIObject(iniObject, new File("test-output.ini").getAbsolutePath());
```
