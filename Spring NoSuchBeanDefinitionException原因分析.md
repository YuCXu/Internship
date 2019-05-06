# Spring NoSuchBeanDefinitionException

### 一、No qualifying bean of type […] found for dependency

这个异常出现一般是因为要注入的bean**未定义**。

例如：有一个类BeanA.java

```java
package com.xyc.model;
@Component
public class BeanA {
    @Autowired
    private BeanB beanB;
}
```

有一个类BeanB.java

```java
package com.xyc.service;
@Component
public class BeanB {

}
```

配置文件applicationContext.xml

```xml
<context:component-scan base-package="com.xyc.model"></context:component-scan>
```

测试类：

```java
package com.xyc.test;

public class AppTest {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        BeanA beanA = (BeanA) context.getBean("beanA");
    }
}
```

自动扫描包路径缺少了BeanB，它与BeanA在不同路径下。

配置文件可以如下配置：

```xml
<context:component-scan base-package="com.xyc"></context:component-scan>
```

