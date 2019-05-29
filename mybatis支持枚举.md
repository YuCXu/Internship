# mybatis 支持枚举

mybatis对枚举类型提供了两种类型支持：**EnumTypeHandler**和**EnumOrdinalTypeHandler**。

#### 1、EnumTypeHandler

这是mybatis**默认的枚举类型转换器**，如果pojo类中使用了枚举类型，而配置类中没有指定类型转换类，mybatis将使用EnumTypeHandler处理枚举属性。它将用枚举类的name进行存储，枚举类的name即枚举类名。

#### 2、EnumOrdinalTypeHandler

这是mybatis提供的另一种转换器，使用了枚举类的ordinal属性（索引位置，从0开始）作为数据库存储信息，由于ordinal属性是int类型的，数据库对应的资源是int或double类型的。

总结：

EnumTypeHandler和EnumOridinalTypeHandler的区别主要是**数据库中存储字段的类型差别**，由于EnumOrdinalTypeHandler使用枚举类型的ordinal作为存储，所以必须使用**数字类型**字段存储。

示例：

建表语句：

```sql
CREATE TABLE `t_user` (
  `id` varchar(45) NOT NULL,
  `accountID` varchar(45) DEFAULT NULL,
  `userName` varchar(45) DEFAULT NULL,
  `statusDef` varchar(45) DEFAULT NULL,
  `statusOrdinal` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';
```

实体类：

```java
package com.xyc.bean;

public class User {

    private String id;

    private String accountID;

    private String userName;

    private EnumStatus statusDef; //枚举属性，使用mybatis默认转换类

    private EnumStatus statusOrdinal; //枚举属性，使用EnumOrdinalTypeHandler转换

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public EnumStatus getStatusDef() {
        return statusDef;
    }

    public void setStatusDef(EnumStatus statusDef) {
        this.statusDef = statusDef;
    }

    public EnumStatus getStatusOrdinal() {
        return statusOrdinal;
    }

    public void setStatusOrdinal(EnumStatus statusOrdinal) {
        this.statusOrdinal = statusOrdinal;
    }

    @Override
    public String toString() {
        return "id:" + id + "\naccountID:" + accountID + "\nuserName:" + userName + "\nstatusDef:" + statusDef.getDescription() + "\nstatusOrdinal:" + statusOrdinal.getDescription();
    }
}
```

枚举类：

```java
package com.xyc.bean;

public enum EnumStatus {
    NORMAL(1, "正常"),
    DELETE(0, "删除"),
    CANCEL(2, "注销");

    private EnumStatus(int code, String description) {
        this.code = new Integer(code);
        this.description = description;
    }
    private Integer code;

    private String description;

    public Integer getCode() {

        return code;
    }

    public String getDescription() {

        return description;
    }
}
```

mybatis配置文件：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.sg.bean.User">

  <resultMap type="User" id="userMap">
    <id column="id" property="id"/>
    <result column="accountID" property="accountID"/>
    <result column="userName" property="userName"/>
    <result column="statusDef" property="statusDef"/>
    <result column="statusOrdinal" property="statusOrdinal" typeHandler="org.apache.ibatis.type.EnumOrdinalTypeHandler"/>
  </resultMap>

  <select id="selectUser" resultMap="userMap">
    select * from t_user where id = #{id}
  </select>

  <insert id="insertUser" parameterType="User">
      insert into t_user(id,accountID,userName,statusDef,statusOrdinal)
      values(
      #{id}, #{accountID}, #{userName},
      #{statusDef},
      #{statusOrdinal, typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}
      )
  </insert>
</mapper>
```



