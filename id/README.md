# 分布式发号器

## 启动脚本

```cmd
#!/bin/bash
nohup java -Dloader.path="lib/" -jar id-0.0.1-SNAPSHOT.jar --node.id=0 --spring.profiles.active=prodV1 > /dev/null 2>&1 &

```