# 卷耳 微服务框架

接入nacos 1.1.3

## 发布到maven仓库

```cmd
mvn clean deploy
```

## 启动顺序
- 启动upms
- 启动authorization
- 启动authentication
- 启动gateway



## nacos配置
在部署前先在nacos内添加test命名空间
![截图](/doc/img/create_namespace.png)

并在test命名空间内初始化一个网关的配置
![截图](/doc/img/create_gateway_init_route_config.png)

内容为
![截图](/doc/img/init_route_config.png)
