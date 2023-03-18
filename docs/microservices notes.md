> 本文整理在这个框架中所涉及到的微服务模块和实际应用，会根据教程顺序进行整理具体步骤中的实现



## 第三方调用服务

1. 配置调用接口

```java
## CustomerClient
@FeignClient(
        name = "customer",
        url = "${clients.customer.url}"
)
public interface CustomerClient {

    @GetMapping(path = "api/v1/customers/{customerId}")
    CustomerCheckResponse checkCustomerExist(
            @PathVariable("customerId") UUID customerId);

}
```

2. 配置微服务调用

配置微服务调度可以实现在不同架构下的流量需求，需要根据具体业务进行选择具体实现方式。

      1. http 直接调用
      2. eureka 服务调用
      3. docker 服务调用
      4. kubernetes 服务调用

## 架构设计

在传统 springBoot 单体架构下的不同业务的文件都根据 MVC 架构合并在一个文件夹下进行管理，造成文件管理的不清晰，而在微服务下的文件管理跟多是在统一目录下进行配置，以具体服务进行命名，将同一类业务的文件放在一个微服务中进行管理。
