# String与Date格式转换

#### 一、String——>Date

```java
SimpleDateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
Date date = fmt.parse("String");
```

#### 二、Date——>String

```java
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
String startTime = sdf.format(new Date());
```



