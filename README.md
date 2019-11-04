# 卷耳 微服务框架

接入nacos 1.1.3

## 发布到maven仓库

```cmd
mvn clean deploy
```

## 启动顺序
- 启动admin
- 启动id
- 启动upms
- 启动authorization
- 启动authentication
- 启动gateway
