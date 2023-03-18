> 本文整理在这个框架中所涉及到的 docker 模块和实际应用，会根据教程顺序进行整理具体步骤中的实现

## docker-compose.yml

这个文件用于调用docker服务的构建，我们的一些资源如 `postgres` `pgadmin` `zipkin` `rabbitmq` 都在这里面配置初始化。具体的初始化实现参照官方文档或者构建教程进行搭建。



### 通信构建

#### networks

构建通信的环境的配置，可以很方便实现服务通信的拆分避免数据泄露。

```yaml
zipkin:
    image: openzipkin/zipkin
    container_name: zipkin
    ports:
      - "9411:9411"
    networks:
      - spring

networks:
  postgres:
    driver: bridge
  spring:
    driver: bridge
```







