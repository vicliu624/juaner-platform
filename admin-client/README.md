# 发布到maven仓库

```cmd
mvn clean deploy
```

# 引用此模块

```xml

<project>
    <repositories>
        <repository>
            <id>mvn-repo</id>
            <url>https://raw.github.com/vicliu624/mvn-repo/1.0.2</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>
    
    <dependencies>
            <dependency>
                <groupId>indi.vicliu.juaner</groupId>
                <artifactId>admin-client</artifactId>
                <version>1.0.2</version>
            </dependency>
        </dependencies>
</project>
    
    
```