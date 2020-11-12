# 欢迎来到 micro-core

micro-core是JAVA版的微服务应用层封装框架，由softbasic组织维护


![micro-core](https://micro-1252168886.cos.ap-chengdu.myqcloud.com/softbasic%E5%95%86%E6%A0%87.jpg "markdown")


micro-core以spring boot为核心，封装了分布式系统中常用组件的调用方法，期望能够在“应用层”为企业项目的开发奠定一个更高层的基础，使业务逻辑的研发能够直接展开。
	
自冯·诺依曼状态机理论创立以来，计算机诞生，硬件性能不断提升，但无论其如何进化，始终存在物理极限，如果对大数据量、实时性等业务要求更高时，单机局限难以突破，于是多台计算机组成的集群应运而生。在软件层面需制定一套规则控制多台计算机共同完成任务，则为分布式软件架构。适应分布式架构的软件基础组件，诸如数据库、缓存、消息中间件、文件存储，直接使用尚存在难度，而微服务使得子项目解耦的同时，研发、部署和运维的复杂度也相应的提升，于是连带环境一起交付使用的容器化技术也需在分布式软件的研发、部署中善加使用。

micro-core不再深究各组件的内部实现机制，而是在理解其基本原理的基础上，致力于将各组件的使用方法封装起来形成一个整体框架，供应用层直接使用。并尝试提供一套研发、测试、部署、运维的软件开发流程范式，供企业项目管理参考。



------------


### 一、基础软件-版本规范
###### micro-core版本：1.x.x

| 软件名称  |软件版本   |
| ------------ | ------------ |
| Ubuntu | 16.04.4  |
| Oracle-JDK|1.8.0_202|
| MongoDB  |4.0.6   |
| Redis  |5.0.3|
| RocketMQ  |4.4.0 |
| FastDFS   |5.11   |
| Nginx   |1.14.2   |
| Git   |2.21.0   |
| Apache-Maven   |3.6.0   |
| Xftp   |6.0.0111p   |
| Xshell   |6.0.0117p   |
| IntelliJ IDEA   |IC-2018.3.5   |
| RedisDesktopManager   |0.8.8.384   |
| nosqlbooster4mongo   |5.1.7   |
| Xshell   |6.0.0117p   |

### 二、基础软件-下载与安装
###### 1、手动下载安装
```html
JDK
https://micro-1252168886.cos.ap-chengdu.myqcloud.com/jdk-8u202-windows-x64.exe

MAVEN
https://micro-1252168886.cos.ap-chengdu.myqcloud.com/apache-maven-3.6.0-bin.tar.gz

GIT
https://micro-1252168886.cos.ap-chengdu.myqcloud.com/git-2.21.0.tar.gz

SourceTree
https://micro-1252168886.cos.ap-chengdu.myqcloud.com/SourceTreeSetup-3.0.17.exe
	
Postman
https://micro-1252168886.cos.ap-chengdu.myqcloud.com/Postman-win64-7.0.5-Setup.exe

Xftp
https://micro-1252168886.cos.ap-chengdu.myqcloud.com/Xftp-6.0.0111p.exe

Xshell
https://micro-1252168886.cos.ap-chengdu.myqcloud.com/Xshell-6.0.0117p.exe

InteliiJ IDEA
https://micro-1252168886.cos.ap-chengdu.myqcloud.com/ideaIC-2018.3.5.exe

redis客户端
https://micro-1252168886.cos.ap-chengdu.myqcloud.com/RedisDesktopManager.zip

MongoDB客户端
https://micro-1252168886.cos.ap-chengdu.myqcloud.com/nosqlbooster4mongo-5.1.7.exe
```

###### 2、docker官方镜像-dockerHub仓库

|  镜像名称 | 主要功能  |  版本信息 |
| ------------ | ------------ | ------------ |
|  softbasic/micro-jdk:1.0.0.RELEASE | JDK  | Oracle-JDK1.8.0_202  |
|  softbasic/micro-jmg:1.0.0.RELEASE | JDK Maven Git  | Oracle-JDK1.8.0_202、apache-maven3.6.0、git2.21.0  |
|  softbasic/micro-deploy-jmg:1.0.0.RELEASE | JDK Maven git  | Oracle-JDK1.8.0_202、apache-maven3.6.0、git2.21.0、micro-core maven依赖包  |
|  softbasic/micro-nexus:1.0.0.RELEASE | NEXUS  | Nexus3.15.2  |

拉取jmg镜像

	docker pull softbasic/micro-jmg:1.0.0.RELEASE

启动容器

	docker run --name=microtest  -itd  -p 61006 softbasic/micro-deploy-jmg:1.0.0.RELEASE /bin/bash
	
### 三、micro-core的安装

Maven中央仓库依赖，在pom.xml中配置如下：

```html
<!-- https://mvnrepository.com/artifact/com.github.softbasic/micro-core -->
<dependency>
    <groupId>com.github.softbasic</groupId>
    <artifactId>micro-core</artifactId>
    <version>1.0.0.RELEASE</version>
</dependency>

```

### 四、micro-core的使用

|  主要功能 |说明   |
| ------------ | ------------ |
| MongoDB  | 副本集连接与基本操作，读写分离  |
| Redis  | 单节点缓存的基本配置和读写  |
| RocketMQ  | 消息中间件的发送与接收  |
| fastDFS  | 分布式文件服务器的存储与读取，及图片像素等比读取  |
| scheduling  | 定时器的触发及参数传递  |
| HibernateValidator  | 参数校验框架  |
| 分布式ID  | 雪花算法  |
| 安全验证  | token防火墙，需配合自定义算法，实现接口级别的访问控制  |



配置文件使用示例：application.properties

```html
#服务端口号
server.port=10300

#框架日志级别
logging.level.cn.microtest.secure=DEBUG

#MongoDB连接池副本集集群
spring.data.mongodb.address=172.16.2.79:27017,172.16.2.79:27017,172.16.2.79:27017
spring.data.mongodb.replica-set=replSet
spring.data.mongodb.database=goods
spring.data.mongodb.username=goods
spring.data.mongodb.password=goods1234

#Redis连接池，单节点
redisson.address=redis://172.16.2.79:6379
redisson.password=redis123
redisson.redissonCache.cacheConfigs=goods:GoodsBuyDetails&300000

#rocketMQ消息中间件
rocketmq.name-server=172.16.2.79:9876
#生产者组
rocketmq.producer.group=goods
#发送扣除库存结果
rocketmq.producer.topic.deductStock=deductResult
#接收扣除库存回滚消息
rocketmq.consumer.topic.goodsDeductRollBack=goodsDeductRollBack

#文件服务器fastdfs
fdfs.tracker-list=172.16.2.79:22122

#定时器线程池
spring.task.scheduling.pool.size=10

#HibernateValidator校验模式
hibernateValidator.failFast=true

#是否启用安全验证
micro.auth=true

#生成全局唯一id（18位数字） 每台机器 不能一样,且 他们取值范围  <=31  &&  >=0
idwork.workerId=3
idwork.dataCenterId=0

```




### 五、联系我们
| 作者        | 联系方式   |
| --------   | -----:  | 
| 师爷        | lcr_rcl@163.com   | 
| 张公子        |  z0710420116@163.com   | 
| 卡卡        |    zhuzixuan89@126.com   | 
| joker        |    mmfly01@163.com   | 
| 卓上        |    15620597118@163.com   | 



### 六、更多帮助文档
更多帮助文档，请到softbasic文档中心
	
	https://www.showdoc.com.cn/softbasic

softbasic社区官网

	http://www.softbasic.org
