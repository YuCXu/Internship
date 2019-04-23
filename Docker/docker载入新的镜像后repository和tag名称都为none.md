## docker导出镜像需要注意的一个问题

①正确：docker save <repository>:<tag> -o <repository>.tar

错误：docker save <IMAGE ID> -o <repository>.tar（会导致载入镜像后名字标签都为<none>）

②如果docker载入新的镜像后repository和tag名称都为none，那么可以通过tag的方法增加名字标签：

docker tag <IMAGE ID> <repository>:<tag>

