# SpringBoot2.0之redis（Lettuce版本）

java操作redis的客户端有**jedis**和**Lettuce**。在SpringBoot1.x系列中，其中使用的是**jedis**，SpringBoot2.x系列中，其中使用的是**Lettuce**。

jedis与lettcue的区别是：

①lettcue和jedis的定位都是redis的client，可以直接连接redis server。

②jedis在实现上是直接连接redis server，如果在多线程环境下是非线程安全的，这个时候只有使用连接池，为每个jedis实例增加物理连接。

③Lettcue的连接是基于Netty的，连接实例（StatefulRedisConnection）可以在多个线程间并发访问。因为StatefulRedisConnection是线程安全的，所以一个连接实例（StatefulRedisConnection）就可以满足多线程环境下的并发访问。

### 实现

#### 1、添加依赖

```xml
<dependencies>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- redis依赖commons-pool-->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
    </dependencies>
```

在application.yml配置一下redis服务器的地址：

```yaml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    # 密码 没有则可以不填
    password: 123456
    # 如果使用的jedis 则将lettuce改成jedis即可
    lettuce:
      pool:
        # 最大活跃链接数 默认8
        max-active: 8
        # 最大空闲连接数 默认8
        max-idle: 8
        # 最小空闲连接数 默认0
        min-idle: 0

```

#### 2、redis配置

下面配置redis的key和value的序列化方式，默认使用的JdkSerializationRedisSerializer，这样会导致我们通过`redis desktop manager`显示的key和value的时候显示不是正常字符。因此需要手动配置一下序列化方式，新建一个config包，在下面新建一个RedisConfig.java，具体代码如下：

```java
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfig {

    /**
     * 配置自定义redisTemplate
     * @return
     */
    @Bean
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);

        template.setValueSerializer(serializer);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

}
```

其中`@Configuration` 代表这个类是一个配置类，然后@AutoConfigureAfter(RedisAutoConfiguration.class)是让我们这个配置类在内置的配置类之后在配置，这样就保证我们的配置类生效，并且不会被覆盖配置。其中需要注意的就是方法名一定要叫**redisTemplate**，因为`@Bean`注解是根据方法名配置这个bean的name的。

#### 3、测试

定义一个实体类：

```java
import java.io.Serializable;

public class User implements Serializable{

    private static final long serialVersionUID = -8599034029971781830L;

    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别 1=男 2=女 其他=保密
     */
    private Integer sex;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public Integer getSex() {
        return sex;
    }
    public void setSex(Integer sex) {
        this.sex = sex;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                '}';
    }
}
```

测试代码：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class Chapter6ApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void redisTest() {
        // redis存储数据
        String key = "name";
        redisTemplate.opsForValue().set(key, "xyc");
        // 获取数据
        String value = (String) redisTemplate.opsForValue().get(key);
        System.out.println("获取缓存中key为" + key + "的值为：" + value);

        User user = new User();
        user.setUsername("xyc");
        user.setSex(18);
        user.setId(1L);
        String userKey = "xyc";
        redisTemplate.opsForValue().set(userKey, user);
        User newUser = (User) redisTemplate.opsForValue().get(userKey);
        System.out.println("获取缓存中key为" + userKey + "的值为：" + newUser);

    }
}
```

测试结果：

![redis1](https://github.com/YuCXu/Internship/blob/master/redis/redis1.PNG)

redis中缓存：

![redis2](https://github.com/YuCXu/Internship/blob/master/redis/redis2.PNG)

![redis3](https://github.com/YuCXu/Internship/blob/master/redis/redis3.PNG)

中文成功显示，并且对象在redis以json方式存储，代表我们配置成功。
下列的就是Redis其它类型所对应的操作方式

opsForValue： 对应 String（字符串）
opsForZSet： 对应 ZSet（有序集合）
opsForHash： 对应 Hash（哈希）
opsForList： 对应 List（列表）
opsForSet： 对应 Set（集合）

​									来源：作者：余空啊  链接：https://www.jianshu.com/p/feef1421ab0b
