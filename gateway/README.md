# API网关

## 启动
```cmd
java -Dloader.path=lib/ -Dcsp.sentinel.dashboard.server=172.16.10.17:8718 -Dcsp.sentinel.app.type=1 -jar gateway-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
```

```cmd
nohup java -Dloader.path=lib/ -Dcsp.sentinel.dashboard.server=172.16.10.17:8718 -Dcsp.sentinel.app.type=1 -jar gateway-0.0.1-SNAPSHOT.jar --spring.profiles.active=test > /dev/null 2>&1 &
```