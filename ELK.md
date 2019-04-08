# ELK原理与介绍

#### 为什么用到ELK？

一般我们需要进行日志分析场景：直接在日志文件中grep、awk就可以获得自己想要的信息。但在规模较大的场景这，此方法效率低下，面临问题包括日志量太大如何归档、文本搜索太慢怎么办、如何多维度查询。需要集中化的日志管理，所有服务器上的日志收集汇总。常见解决思路是建立**集中式日志收集系统**，将所有节点上的日志统一收集、管理、访问。

一般大型系统是一个**分布式**部署的架构，不同的服务模块部署在不同的服务器上，问题出现时，大部分情况需要根据问题暴露的**关键信息**，定位到具体的服务器和服务模块，构建一套集中式日志系统，可以提高定位问题的效率。

一个完整的集中式日志系统，需要包含以下几个主要特点：

​	①收集：能够采集多种来源的日志数据；

​	②传输：能够稳定的把日志数据传输到中央系统；

​	③存储：能够存储日志数据；

​	④分析：可以支持UI分析；

​	⑤警告：能够提供错误报告、监控机制。

ELK提供了一整套解决方案，并且都是开源软件，之间互相配合使用，完美衔接，高效的满足了很多场合的应用。目前主流的一种日志系统。

#### **ELK简介**

ELK是三个开源软件的缩写，分别表示为：Elasticsearch、Logstash、Kibana，它们都是开源软件。新增了一个FileBeat，它是一个轻量级的**日志收集处理工具**(Agent)，Filebeat占用资源少，适合于在各个服务器上搜集日志后传输给Logstash，官方也推荐此工具。

Elasticsearch是个**开源分布式搜索引擎**，提供搜集、分析、存储数据三大功能。它的特点有：分布式，零配置，自动发现，索引自动分片，索引副本机制，restful风格接口，多数据源，自动搜索负载等。

Logstash 主要是用来**日志的搜集、分析、过滤日志**的工具，支持大量的数据获取方式。一般工作方式为c/s架构，client端安装在需要收集日志的主机上，server端负责将收到的各节点日志进行过滤、修改等操作在一并发往elasticsearch上去。

Kibana 也是一个开源和免费的工具，Kibana可以为 Logstash 和 ElasticSearch 提供的日志分析友好的 Web 界面，可以帮助汇总、分析和搜索重要数据日志。

Filebeat隶属于**Beats**。目前Beats包含四种工具：

1. 1. Packetbeat（搜集网络流量数据）
   2. Topbeat（搜集系统、进程和文件系统级别的 CPU 和内存使用情况等数据）
   3. Filebeat（**搜集文件数据**）
   4. Winlogbeat（搜集 Windows 事件日志数据）