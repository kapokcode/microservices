> 本文整理在这个框架中所涉及到的 maven 模块和实际应用，会根据教程顺序进行整理具体步骤中的实现

## 父工程管理

配置子模块的管理

```xml
	<modules>
        <module>customer</module>
        <module>fraud</module>
        <module>eureka-server</module>
        <module>clients</module>
        <module>notification</module>
        <module>apigw</module>
        <module>amqp</module>
        <module>payment</module>
    </modules>
```



### properties

在 `properties` 标签中我们可以配置整个项目的编译版本和编码格式，我们的版本管理应该统一在父工程中配置，通过父工程的版本配置 `dependencyManagement` 我们的子工程的依赖可以自动找到对应版本。

```xml
	<properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <spring.boot.maven.plugin.version>2.7.6</spring.boot.maven.plugin.version>
        <spring.boot.dependecies.version>2.7.6</spring.boot.dependecies.version>
        <spring.cloud.dependecies.version>2021.0.5</spring.cloud.dependecies.version>
    </properties>
```



### dependencyManagement

在 `dependencyManagement` 标签中我们用于配置整个工程版本管理

```xml
	<dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring.boot.dependecies.version}</version>
                <scope>import</scope>
                <type>pom</type>
            </dependency>

            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring.cloud.dependecies.version}</version>
                <scope>import</scope>
                <type>pom</type>
            </dependency>
        </dependencies>
    </dependencyManagement>
```



### dependencies

在父工程中的 `dependencies` 中，我们可以给每个管理的模块统一添加依赖。

```xml
	<dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
    </dependencies>
```



### build

在父工程中build使用两个子标签，分别是 `pluginManagement` 和 `plugins`  。

`spring-boot-maven-plugin` 插件配置了 spring boot 官方打包工具，版本与 spring-boot 版本一致

`jib-maven-plugin` 插件是一个 google 的 docker 镜像打包工具，用于生成和上传镜像

```xml
	<build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring.boot.maven.plugin.version}</version>
                    <executions>
                        <execution>
                            <goals>
                                <goal>repackage</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <groupId>com.google.cloud.tools</groupId>
                    <artifactId>jib-maven-plugin</artifactId>
                    <version>3.3.1</version>
                    <configuration>
                        <from>
                            <image>eclipse-temurin:17@sha256:2b47a8ea946ce1e5365a1562414ed576e378b7b670cadff3fb98ebecf2890cdc</image>
                            <platforms>
                                <platform>
                                    <architecture>arm64</architecture>
                                    <os>linux</os>
                                </platform>
                                <platform>
                                    <architecture>amd64</architecture>
                                    <os>linux</os>
                                </platform>
                            </platforms>
                        </from>
                        <to>
                            <tags>
                                <tag>latest</tag>
                            </tags>
                        </to>
                    </configuration>
                    <executions>
                        <execution>
                            <phase>package</phase>
                            <goals>
                                <goal>build</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </pluginManagement>

        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
```



## 子工程管理

### parent

配置导入父工程的依赖，配合

```xml
<parent>
    <artifactId>kapokservices</artifactId>
    <groupId>com.kapok</groupId>
    <version>1.0-SNAPSHOT</version>
</parent>
```

### profiles

配置镜像打包依赖

```xml
	<profiles>
        <profile>
            <id>build-docker-image</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.google.cloud.tools</groupId>
                        <artifactId>jib-maven-plugin</artifactId>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
```

### dependency

配置依赖引用

```xml
		<dependency>
            <groupId>com.kapok</groupId>
            <artifactId>amqp</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>com.kapok</groupId>
            <artifactId>clients</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>
```



