# Dockerfile指令

## 组成部分：

部分				命令

基础镜像信息		  FROM

维护者信息	             MAINTAINER

镜像操作指令	         RUN、COPY、ADD、EXPOSE、WORKDIR、ONBUILD、USER、VOLUME等

容器启动时执行指令      CMD、ENTRYPOINT

## 各个命令详解

#### FROM

​	指定哪种镜像作为新镜像的基础镜像，如：

```shell
FROM ubuntu:14.04
```

#### MAINTAINER

​	指明该镜像的作者和电子邮件，如：

```shell
MAINTAINER xxxx "xxxxxxx@qq.com"
```

#### RUN

​	在新镜像内部执行的命令。比如安装一些软件、配置一些基础环境，可以用\来换行，如：

```shell
RUN echo 'hello docker!' \
    > /usr/local/file.txt
```

​	也可以使用exec格式RUN ["executable", "param1", "param2"]的命令，如：

```shell
RUN ["apt-get","install","-y","nginx"]
```

​	注意：executable是命令，后面param是参数。

#### COPY

​	将主机的文件复制到镜像内，如果目的位置不存在，Docker会自动创建所有需要的目录结构，但是它只是**单纯的复制**，并不会去做文件提取和解压工作。如：

```shell
COPY application.yml /etc/springboot/hello-service/src/resources
```

​	注意：需要复制的目录一定要放在**Dockerfile文件的同级目录**下。

原因：因为构建环境将会上传到Docker守护进程，而复制是在Docker守护进程中进行的。任何位于构建环境之外的东西都是不可用的。COPY指令的目的的位置则必须是容器内部的一个绝对路径。

#### ADD

​	将主机的文件复制到镜像中，跟COPY一样，限制条件和使用方式都一样，如：

```shell
ADD application.yml /etc/springboot/hello-service/src/resources
```

​	但是ADD会对压缩文件（tar，gzip，bzip2，etc）做提取和解压操作。

#### EXPOSE

​	暴露镜像的端口供主机做映射，启动镜像时，使用**-p**参数来将镜像端口与宿主机的随机端口做映射，使用方式（可指定多个）：

```shell
EXPOSE 8080 
EXPOSE 8081
...
```

#### WORKDIR

​	在构建镜像时，指定镜像的**工作目录**，之后的命令都是基于此工作目录，如果不存在，则会创建目录，如：

```shell
WORKDIR /usr/local
WORKDIR webservice
RUN echo 'hello docker' > text.txt
...
```

最终会在/usr/local/webservice/目录下生成text.txt

#### ONBUILD

​	当一个包含ONBUILD命令的镜像被用作其他镜像的基础镜像时（比如用户的镜像需要从某为准备好的位置添加源代码，或者用户需要执行特定于构建镜像的环境的构建脚本），该命令就会执行。

​	如创建镜像image A：

```shell
FROM ubuntu
...
ONBUILD ADD . /var/www
...
```

​	然后创建镜像image-B，指定image-A为基础镜像，如：

```shell
FROM image-A
...
```

​	然后在构建image-B的时候，日志上显示如下：

```shell
Step 0 : FROM image-A
# Execting 1 build triggers
Step onbuild-0 : ADD . /var/www
...
```

#### USER

​	指定该镜像以什么样的用户去执行，如：

```she;;
USER mongo
```

#### VOLUME

​	用于向基于镜像创建的容器添加卷。比如你可以将mongodb镜像中存储数据的data文件指定为主机的某个文件。（容器内部建议不要存储任何数据）

```shell
VOLUME /data/db /data/configdb
```

​	注意:`VOLUME 主机目录 容器目录`

#### CMD

​	容器启动时需要执行的命令，如：

```shell
CMD /bin/bash
```

​	同样可以使用exec语法，如：

```shell
CMD ["/bin/bash"]
```

​	当有多个CMD的时候，**只有最后一个生效**。

#### ENTRYPOINT

作用和用法和CMD一模一样

#### CMD和ENTRYPOINT的区别

CMD和ENTRYPOINT同样作为容器启动时执行的命令，区别有以下几点：

​	①**CMD命令会被docker run的命令覆盖而ENTRYPOINT不会。**

​	如使用`CMD ["/bin/bash"]`或`ENTRYPOINT ["/bin/bash"]`后，再使用docker run -ti image启动容器，它会自动进入容器内部的交互终端，如同使用：

```shell
docker run -ti image /bin/bash
```

​	但是如果启动镜像的命令为docker run -ti image /bin/ps，使用CMD后面的命令就会被覆盖转而执行bin/ps命令，而*ENTRYPOINT的则不会*，而是会把docker run**后面的命令**当作ENTRYPOINT执行命令的**参数**。

​	例子：

​	Dokcerfile中为：

```shell
...
ENTRYPOINT ["/user/sbin/nginx"]
```

​	然后通过启动build之后的容器

```shell
docker run -ti image -g "daemon off"
```

​	此时-g "daemon off"会被当成参数传递给ENTRYPOINT，最终的命令变成了

```shell
/user/sbin/nginx -g "daemon off"
```

​	②**CMD和ENTRYPOINT都存在时**

​	CMD和ENTRYPOINT都存在时，**CMD的指令变成了ENTRYPOINT的参数**，并且此CMD提供的参数会被 docker run 后面的命令覆盖，如：

```shell
...
ENTRYPOINT ["echo","hello","i am"]
CMD ["docker"]
```

​	之后启动构建之后的容器

​	使用docker run -ti image：

​		输出“hello i am docker”

​	使用docker run -ti image world

​		输出“hello i am world”