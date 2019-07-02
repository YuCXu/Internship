# SQL优化

## 一、建立索引

### 1、大量重复的不要建索引

sql根据表中的数据来进行查询优化的，当索引列有大量数据重复时，sql查询可能不会去利用索引，例如：字段sex，male、female几乎各一半，即使在sex上建立了索引对查询也不起作用。

### 2、索引不是越多越好，增改操作会因重建索引而变慢

索引并不是越多越好，虽然可以提高相应的select效率，但同时降低insert或update的效率。

### 3、避免对索引进行以下操作

①避免对索引字段进行计算操作

②避免在索引字段上使用not、<>、!=

③避免在索引列上使用is null和is not null

④避免在索引列上出现数据类型转换

⑤避免在索引字段上使用函数

⑥避免简历索引的列中使用空值

## 二、where之后的优化

对查询进行优化，应尽量避免全表扫描，首先应考虑在 where 及 order by 涉及的列上建立索引。就是说索引最好能建在where之后要用到的字段上面，但同时也要注意避免上述索引的问题。

### 1、应尽量避免在 where 子句中使用 != 或 < > 操作符

否则引擎将放弃使用索引而进行全表扫描。

### 2、应尽量避免在 where 子句中使用 or 来连接条件

否则将导致引擎放弃使用索引而进行全表扫描，如：

```java
select id from t where num=10 or num=20
```

可以这样查询，将 `or` 用 `union all` 来替换：

```java
select id from t where num=10 union all select id from t where num=20
```

### 3.慎用 in 和 not in，改用 exists 和 between

否则会导致全表扫描，如：
 `select id from t where num in(1,2,3)`对于连续的数值，能用 `between` 就不要用 in 了：`select id from t where num between 1 and 3`。
 很多时候用 `exists`代替 in 是一个好的选择：select num from a where num in(select num from b)

### 4.应尽量避免在 where 子句中对字段进行表达式操作或者函数

不要在 where 子句中的“=”左边进行函数、算术运算或其他表达式运算，否则系统将可能无法正确使用索引。
如：`select id from t where num/2=100`应改为:`select id from t where num=100*2`

### 三、select优化

### 1、任何地方都不要使用select * from t

不要使用select * from t，用具体的字段列表代替“*”。

### 2、推荐使用UNION ALL，尽量避免使用UNION

`UNION` 因为会将各查询子集的记录**做比较**，故比起`UNION ALL` ，通常速度都会慢上许多。