# SAXBuilder

SAXBuilder是一个JDOM解析器，可以将路径中的**XML文件解析为Document对象**。

SAXBuilder使用第三方SAX解析器（默认情况下由JAXP选择，或者可以手动配置）来解析任务，并使用SAXHandler的实例来侦听SAX事件，以便使用JDOM内容构造文档一个JDOMFactory。

##### 1、指定解析器

```java
SAXBuilder builder=new SAXBuilder(false); //默认的解析器
```

##### 2、得到Document，以后所有操作都是基于Document之上的。

```java
StringReader returnQuote = new StringReader(rexml);
Document doc = builder.build(returnQuote);
```

##### 3、得到根元素

```java
Element books=doc.getRootElement();
```

JDOM中所有节点（DOM中的概念）都是一个org.jdom.Element 类，它的子节点也是一个org.jdom.Element类。

##### 4、得到节点的集合

```java
List booklist=books.getChildren(“book”);
```

这表示得到“books”元素的所在名称为“book”的元素，并把这些元素放到一个List集合中。

得到单个元素：

```java
Element segment= books.getChild(“Segment”);
```

##### 5、循环List集合

```java
for (Iterator iter = booklist.iterator(); iter.hasNext(); ) {
	Element book = (Element) iter.next();
｝
```

取得元素的属性：

```java
String email = book.getAttributeValue(“email”);
```

