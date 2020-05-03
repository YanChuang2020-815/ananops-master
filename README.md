# ananops-master

### 平台目录结构

```
├─ananops-master----------------------------父项目，公共依赖
│  │
│  ├─ananops-eureka--------------------------微服务注册中心
│  │
│  ├─ananops-discovery-----------------------微服务配置中心
│  │
│  ├─ananops-monitor-------------------------微服务监控中心
│  │
│  ├─ananops-zipkin--------------------------微服务日志采集中心
│  │
│  ├─ananops-gateway--------------------------微服务网关中心
│  │
│  ├─ananops-provider
│  │  │
│  │  ├─ananops-provider-mdc------------------数据服务中心
│  │  │
│  │  ├─ananops-provider-opc------------------对接服务中心
│  │  │
│  │  ├─ananops-provider-tpc------------------任务服务中心
│  │  │
│  │  └─ananops-provider-uac------------------用户服务中心
│  │
│  ├─ananops-provider-api
│  │  │
│  │  ├─ananops-provider-mdc-api------------------数据服务中心API
│  │  │
│  │  ├─ananops-provider-opc-api------------------对接服务中心API
│  │  │
│  │  ├─ananops-provider-tpc-api------------------任务服务中心API
│  │  │
│  │  ├─ananops-provider-sdk-api------------------可靠消息服务API
│  │  │
│  │  └─ananops-provider-uac-api------------------用户服务中心API
│  │
│  ├─ananops-common
│  │  │
│  │  ├─ananops-common-base------------------公共POJO基础包
│  │  │
│  │  ├─ananops-common-config------------------公共配置包
│  │  │
│  │  ├─ananops-common-core------------------微服务核心依赖包
│  │  │
│  │  ├─ananops-common-util------------------公共工具包
│  │  │
│  │  ├─ananops-common-zk------------------zookeeper配置
│  │  │
│  │  ├─ananops-security-app------------------公共无状态安全认证
│  │  │
│  │  ├─ananops-security-core------------------安全服务核心包
│  │  │
│  │  └─ananops-security-feign------------------基于auth2的feign配置
│  │
│  ├─ananops-generator



```

## 部署


### 配置域名

```
#ananops

127.0.0.1 ananops-discovery
127.0.0.1 ananops-eureka
127.0.0.1 ananops-gateway
127.0.0.1 ananops-monitor
127.0.0.1 ananops-zipkin
127.0.0.1 ananops-provider-uac
127.0.0.1 ananops-provider-mdc
127.0.0.1 ananops-provider-omc
127.0.0.1 ananops-provider-opc


127.0.0.1 ananops-db-mysql
127.0.0.1 ananops-db-redis
127.0.0.1 ananops-mq-rabbit
127.0.0.1 ananops-mq-rocket
127.0.0.1 ananops-provider-zk

```

### 配置环境

```

1. 克隆 ananops-config-repo 到 E:/AnanOps/ 目录下（对应配置在 ananops-discovery的bootstap.yml文件中）
   克隆地址：https://github.com/ananops/ananops-config-repo
2. 安装redis
3. 安装zookeeper
4. 安装rabbitmq
5. 安装rocketmq
6. 安装第三方jar包


```

### 注意事项
```

数据库默认登录名、密码，如不一致需修改,建议使用全局查找
1.username:root
2.password:123456


Redis默认无密码，如本地有密码需自行修改，修改位置
1. ananops-provider-uac 里的application.yml
2. ananops-config-repo 里的 application.yml


部署到本地时根据自己情况进行修改

```

### 微服务启动顺序

```
1. ananops-eureka
2. ananops-discovery
3. ananops-provider-uac
4. ananops-gateway
5. 剩下微服务无启动数序要求

说明：开启微服务前需要先把redis、zookeeper、rabbitmq、rocketmq 启动起来

```

