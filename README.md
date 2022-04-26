# Skywalking 使用总结

## 0. 简要说明

> [下载地址](https://skywalking.apache.org/downloads/)

> [Skywalking-APM 配置说明文档](https://skywalking.apache.org/docs/main/v8.9.1/en/setup/backend/configuration-vocabulary/#configuration-vocabulary)

> [Skywalking-Agent 配置说明文档](https://skywalking.apache.org/docs/skywalking-java/latest/en/setup/service-agent/java-agent/configurations/)

> [kafka-reporter 配置](https://skywalking.apache.org/docs/skywalking-java/latest/en/setup/service-agent/java-agent/advanced-reporters/#kafka-reporter)

> [kafka-fetcher 配置](https://skywalking.apache.org/docs/main/latest/en/setup/backend/kafka-fetcher/#kafka-fetcher)

> [Skywalking-Agent的使用流程](https://skywalking.apache.org/docs/skywalking-java/latest/en/setup/service-agent/java-agent/readme/)

> 注意:
skywalking 使用到的端口:
config/application.yml
- gRPC端口: 11800
- rest端口: 12800
  webapp/webapp.yml
- web端口: 8080
  如果有修改, 则需要同步修改webapp/webapp.yml的配置
```
discovery:
      client:
        simple:
          instances:
            oap-service:
              - uri: http://127.0.0.1:12800
```



## 1. Skywalking-APM + mysql 部署
>
- [SkyWalking APM](https://archive.apache.org/dist/skywalking/8.9.1/apache-skywalking-apm-8.9.1.tar.gz)
- [Java Agent](https://www.apache.org/dyn/closer.cgi/skywalking/java-agent/8.9.0/apache-skywalking-java-agent-8.9.0.tgz)
- [mysql-connector-java](https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.28/mysql-connector-java-8.0.28.jar)
- [官方配置地址-mysql版](https://skywalking.apache.org/docs/main/v8.9.1/en/setup/backend/backend-storage/#mysql)

```bash
# 下载SkyWalking APM:
$ wget https://archive.apache.org/dist/skywalking/8.9.1/apache-skywalking-apm-8.9.1.tar.gz

#  解压SkyWalking APM:
$ tar -zxvf apache-skywalking-apm-8.9.1.tar.gz

# 移动到用户目录
$ mv apache-skywalking-apm-bin /usr/local/skywalking

# 进入目录
$ cd /usr/local/skywalking

# 编辑文档,并修改配置
$ vim config/application.yml

# 将h2改为mysql
...
storage:
  selector: ${SW_STORAGE:mysql}    #  selector: ${SW_STORAGE:h2}
  elasticsearch:
    namespace: ${SW_NAMESPACE:""}
    clusterNodes: ${SW_STORAGE_ES_CLUSTER_NODES:localhost:9200}
    protocol: ${SW_STORAGE_ES_HTTP_PROTOCOL:"http"}
    connectTimeout: ${SW_STORAGE_ES_CONNECT_TIMEOUT:500}
    socketTimeout: ${SW_STORAGE_ES_SOCKET_TIMEOUT:30000}
    numHttpClientThread: ${SW_STORAGE_ES_NUM_HTTP_CLIENT_THREAD:0}
...

# 修改mysql配置
...
mysql:
    properties:
      jdbcUrl: ${SW_JDBC_URL:"mysql链接地址"}
      dataSource.user: ${SW_DATA_SOURCE_USER:用户名}
      dataSource.password: ${SW_DATA_SOURCE_PASSWORD:用户密码}
      dataSource.cachePrepStmts: ${SW_DATA_SOURCE_CACHE_PREP_STMTS:true}
      dataSource.prepStmtCacheSize: ${SW_DATA_SOURCE_PREP_STMT_CACHE_SQL_SIZE:250}
      dataSource.prepStmtCacheSqlLimit: ${SW_DATA_SOURCE_PREP_STMT_CACHE_SQL_LIMIT:2048}
      dataSource.useServerPrepStmts: ${SW_DATA_SOURCE_USE_SERVER_PREP_STMTS:true}
    metadataQueryMaxSize: ${SW_STORAGE_MYSQL_QUERY_MAX_SIZE:5000}
    maxSizeOfArrayColumn: ${SW_STORAGE_MAX_SIZE_OF_ARRAY_COLUMN:20}
    numOfSearchableValuesPerTag: ${SW_STORAGE_NUM_OF_SEARCHABLE_VALUES_PER_TAG:2}
    maxSizeOfBatchSql: ${SW_STORAGE_MAX_SIZE_OF_BATCH_SQL:2000}
    asyncBatchPersistentPoolSize: ${SW_STORAGE_ASYNC_BATCH_PERSISTENT_POOL_SIZE:4}
    # mysql-connector-java-6.0 版本以上需要增加以下语句
    driver: com.mysql.cj.jdbc.Driver
...

# 下载 mysql-connector-java
$ cd
$ wget https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.28/mysql-connector-java-8.0.28.jar

# 将文件移动到skywalking下的oap-libs
$ mv mysql-connector-java-8.0.28.jar /usr/local/skywalking/oap-libs

# 进入脚本目录
$ cd /usr/local/skywalking/bin

# 初始化 mysql, 并等待初始化完成
$ sh oapServiceInit.sh

# 待初始化完成后, 启动 skywalking
$ sh startup.sh

```

## 2. 项目配置Skywalking-Agent
> [官方配置流程](https://skywalking.apache.org/docs/skywalking-java/latest/en/setup/service-agent/java-agent/readme/)

```bash

# 下载Java Agent:
$ wget https://www.apache.org/dyn/closer.cgi/skywalking/java-agent/8.9.0/apache-skywalking-java-agent-8.9.0.tgz

# 解压agent
$ tar -zxvf apache-skywalking-java-agent-8.9.0.tgz

# 修改agent配置文件
$ cd  skywalking-agent/config
$ vim agent.config

# The agent namespace
agent.namespace=${SW_AGENT_NAMESPACE:命名空间}

# The service name in UI
agent.service_name=${SW_AGENT_NAME:实例名称}

# Backend service addresses. 默认端口 11800, 如果有修改配置,则需要改为对应的端口
collector.backend_service=${SW_AGENT_COLLECTOR_BACKEND_SERVICES:部署了skywalking的地址:对应的端口}


# 项目增加jvm启动参数
java -javaagent:/Users/raven/Downloads/skywalking-agent/skywalking-agent.jar=agent.service_name=first_node -jar xxxxx.jar

```

**至此,已经可以使用skywalking监控项目**

## 3. kafka数据上报
```bash
#  将 kafka-reporter-plugin-8.9.0.jar 从 agent/optional-reporter-plugins/ 复制到 agent/plugins/
$ cp agent/optional-reporter-plugins/kafka-reporter-plugin-8.9.0.jar  agent/plugins/

# 配置 skywalking-APM 的 kafka-fetcher
$ vim /usr/local/skywalking/config/application.yml

...
# 修改启动kafka的配置为: default
#非集群模式还需要修改分区数量与副本数量
kafka-fetcher:
 # selector: ${SW_KAFKA_FETCHER:-}
  selector: ${SW_KAFKA_FETCHER:default}
  default:
    bootstrapServers: ${SW_KAFKA_FETCHER_SERVERS:localhost:9092}
    namespace: ${SW_NAMESPACE:""}
    # partitions: ${SW_KAFKA_FETCHER_PARTITIONS:3}
    # replicationFactor: ${SW_KAFKA_FETCHER_PARTITIONS_FACTOR:2}
    partitions: ${SW_KAFKA_FETCHER_PARTITIONS:1}
    replicationFactor: ${SW_KAFKA_FETCHER_PARTITIONS_FACTOR:1}
    enableNativeProtoLog: ${SW_KAFKA_FETCHER_ENABLE_NATIVE_PROTO_LOG:true}
    enableNativeJsonLog: ${SW_KAFKA_FETCHER_ENABLE_NATIVE_JSON_LOG:true}
    isSharding: ${SW_KAFKA_FETCHER_IS_SHARDING:false}
    consumePartitions: ${SW_KAFKA_FETCHER_CONSUME_PARTITIONS:""}
    kafkaHandlerThreadPoolSize: ${SW_KAFKA_HANDLER_THREAD_POOL_SIZE:-1}
    kafkaHandlerThreadPoolQueueSize: ${SW_KAFKA_HANDLER_THREAD_POOL_QUEUE_SIZE:-1}
...


# 配置 skywalking-Agent 的kafka-reporter
$ vim agent/config/agent.config

...
#  A list of host/port pairs to use for establishing the initial connection to the Kafka cluster.
plugin.kafka.bootstrap_servers=${SW_KAFKA_BOOTSTRAP_SERVERS:kafka的服务器地址:端口}

# 请注意，目前，代理仍需要配置 GRPC 接收器来交付 profiling 任务。也就是说，下面的配置不能省略
# Backend service addresses.
collector.backend_service=${SW_AGENT_COLLECTOR_BACKEND_SERVICES:127.0.0.1:11800}
...



# 如果外网无法访问kafka, 则需要修改kafka配置

# docker启动:
# 修改 KAFKA_ADVERTISED_LISTENERS 参数地址 为 外网地址
 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://${ip:port} \

# 正常部署启动:
# 修改 Server.properties
# 广播地址，主要用于我们外网连接kafka集群
# 在公司内网部署 kafka 集群只需要用到 listeners，内外网需要作区分时 才需要用到advertised.listeners。
advertised.listeners=PLAINTEXT://${ip:port}

```

## 4. 项目引入skywalking, 并 打印traceId 与 上报业务日志
> *[skywalking-log4j2 插件官方使用说明](https://skywalking.apache.org/docs/skywalking-java/v8.9.0/en/setup/service-agent/java-agent/application-toolkit-log4j-2.x/)*
Print SkyWalking context in your logs
Your only need to replace pattern `%traceId` with `%sw_ctx`.
When you use -javaagent to active the SkyWalking tracer, `log4j2 will output SW_CTX: [$serviceName,$instanceName,$traceId,$traceSegmentId,$spanId], if it existed`. If the tracer is inactive, the output will be SW_CTX: N/A.


### 父pom 引入依赖
```xml
<dependencyManagement>
    <dependencies>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <version>2.3.8.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>2.3.8.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <version>2.3.8.RELEASE</version>
            <scope>test</scope>
        </dependency>

        <!--log4j2-->
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-api</artifactId>
            <version>2.17.2</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>2.17.2</version>
        </dependency>

        <!--skywalking-apm-toolkit-log4j-2.x-->
        <dependency>
            <groupId>org.apache.skywalking</groupId>
            <artifactId>apm-toolkit-log4j-2.x</artifactId>
            <version>8.9.0</version>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.11</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.apache.skywalking</groupId>
        <artifactId>apm-toolkit-log4j-2.x</artifactId>
    </dependency>
</dependencies>

```

### 子pom引入依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
    </dependency>

</dependencies>
```


------

### log4j2.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--Configuration后面的status，这个用于设置log4j2自身内部的信息输出，可以不设置，当设置成trace时，你会看到log4j2内部各种详细输出-->
<!--monitorInterval：Log4j能够自动检测修改配置 文件和重新配置本身，设置间隔秒数-->

<!-- 配置skywalking定义的模板, 需要增加属性 packages, 并引用  org.apache.skywalking.apm.toolkit.log.log4j.v2.x -->
<configuration status="WARN" monitorInterval="30" packages="org.apache.skywalking.apm.toolkit.log.log4j.v2.x">

    <!--日志级别以及优先级排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL -->

    <!--变量配置-->
    <Properties>
        <!-- 格式化输出：%date表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度 %msg：日志消息，%n是换行符-->
        <!-- %logger{36} 表示 Logger 名字最长36个字符 -->
        <property name="LOG_PATTERN" value="[%traceId][%sw_ctx]>>>>>>%date{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n" />
        <!-- 定义日志存储的路径 -->
        <property name="FILE_PATH" value="./logs/first" />
        <property name="FILE_NAME" value="first" />
    </Properties>

    <appenders>

        <console name="Console" target="SYSTEM_OUT">
            <!--输出日志的格式-->
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <!--控制台只输出level及其以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
            <ThresholdFilter level="info" onMatch="ACCEPT" onMismatch="DENY"/>
        </console>

        <!--文件会打印出所有信息，这个log每次运行程序会自动清空，由append属性决定，适合临时测试用-->
        <File name="Filelog" fileName="${FILE_PATH}/test.log" append="false">
            <PatternLayout pattern="${LOG_PATTERN}"/>
        </File>

        <!-- 这个会打印出所有的info及以下级别的信息，每次大小超过size，则这size大小的日志会自动存入按年份-月份建立的文件夹下面并进行压缩，作为存档-->
        <RollingFile name="RollingFileInfo" fileName="${FILE_PATH}/info.log" filePattern="${FILE_PATH}/${FILE_NAME}-INFO-%d{yyyy-MM-dd}_%i.log.gz">
            <!--控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
            <ThresholdFilter level="info" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <!--interval属性用来指定多久滚动一次，默认是1 hour-->
                <TimeBasedTriggeringPolicy interval="1"/>
                <SizeBasedTriggeringPolicy size="10MB"/>
            </Policies>
            <!-- DefaultRolloverStrategy属性如不设置，则默认为最多同一文件夹下7个文件开始覆盖-->
            <DefaultRolloverStrategy max="15"/>
        </RollingFile>

        <!-- 这个会打印出所有的warn及以下级别的信息，每次大小超过size，则这size大小的日志会自动存入按年份-月份建立的文件夹下面并进行压缩，作为存档-->
        <RollingFile name="RollingFileWarn" fileName="${FILE_PATH}/warn.log" filePattern="${FILE_PATH}/${FILE_NAME}-WARN-%d{yyyy-MM-dd}_%i.log.gz">
            <!--控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
            <ThresholdFilter level="warn" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <!--interval属性用来指定多久滚动一次，默认是1 hour-->
                <TimeBasedTriggeringPolicy interval="1"/>
                <SizeBasedTriggeringPolicy size="10MB"/>
            </Policies>
            <!-- DefaultRolloverStrategy属性如不设置，则默认为最多同一文件夹下7个文件开始覆盖-->
            <DefaultRolloverStrategy max="15"/>
        </RollingFile>

        <!-- 这个会打印出所有的error及以下级别的信息，每次大小超过size，则这size大小的日志会自动存入按年份-月份建立的文件夹下面并进行压缩，作为存档-->
        <RollingFile name="RollingFileError" fileName="${FILE_PATH}/error.log" filePattern="${FILE_PATH}/${FILE_NAME}-ERROR-%d{yyyy-MM-dd}_%i.log.gz">
            <!--控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
            <ThresholdFilter level="error" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <!--interval属性用来指定多久滚动一次，默认是1 hour-->
                <TimeBasedTriggeringPolicy interval="1"/>
                <SizeBasedTriggeringPolicy size="10MB"/>
            </Policies>
            <!-- DefaultRolloverStrategy属性如不设置，则默认为最多同一文件夹下7个文件开始覆盖-->
            <DefaultRolloverStrategy max="15"/>
        </RollingFile>

        <!--使用 GRPCLogClientAppender 上传业务日志到skywalking-->
        <GRPCLogClientAppender name="grpc-log">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </GRPCLogClientAppender>


    </appenders>

    <!--Logger节点用来单独指定日志的形式，比如要为指定包下的class指定不同的日志级别等。-->
    <!--然后定义loggers，只有定义了logger并引入的appender，appender才会生效-->
    <loggers>

        <!--过滤掉spring和mybatis的一些无用的DEBUG信息-->
        <logger name="org.mybatis" level="info" additivity="false">
            <AppenderRef ref="Console"/>
        </logger>
        <!--监控系统信息-->
        <!--若是additivity设为false，则 子Logger 只会在自己的appender里输出，而不会在 父Logger 的appender里输出。-->
        <Logger name="org.springframework" level="info" additivity="false">
            <AppenderRef ref="Console"/>
        </Logger>

        <root level="info">
            <appender-ref ref="Console"/>
            <appender-ref ref="Filelog"/>
            <appender-ref ref="RollingFileInfo"/>
            <appender-ref ref="RollingFileWarn"/>
            <appender-ref ref="RollingFileError"/>

            <!-- 启用grpc-log -->
            <appender-ref ref="grpc-log"/>
        </root>
    </loggers>

</configuration>

```

## 5. skywalking 消费 kafka的 groupId 与 topic

```java
public class KafkaFetcherConfig extends ModuleConfig {

    /**
     * Kafka consumer config.
     */
    private Properties kafkaConsumerConfig = new Properties();

    /**
     *  <B>bootstrap.servers</B>: A list of host/port pairs to use for establishing the initial connection to the Kafka cluster.
     *  A list of host/port pairs to use for establishing the initial connection to the Kafka cluster.
     */
    private String bootstrapServers;

    /**
     * <B>group.id</B>: A unique string that identifies the consumer group this consumer belongs to.
     */
    private String groupId = "skywalking-consumer";

    /**
     * Which PartitionId(s) of the topics assign to the OAP server. If more than one, is separated by commas.
     */
    private String consumePartitions = "";

    /**
     * isSharding was true when OAP Server in cluster.
     */
    private boolean isSharding = false;

    /**
     * If true, create the Kafka topic when it does not exist.
     */
    private boolean createTopicIfNotExist = true;

    /**
     * The number of partitions for the topic being created.
     */
    private int partitions = 3;

    /**
     * The replication factor for each partition in the topic being created.
     */
    private int replicationFactor = 2;

    private boolean enableNativeProtoLog = true;

    private boolean enableNativeJsonLog = true;

    private String configPath = "meter-analyzer-config";

    private String topicNameOfMetrics = "skywalking-metrics";

    private String topicNameOfProfiling = "skywalking-profilings";

    private String topicNameOfTracingSegments = "skywalking-segments";

    private String topicNameOfManagements = "skywalking-managements";

    private String topicNameOfMeters = "skywalking-meters";

    private String topicNameOfLogs = "skywalking-logs";

    private String topicNameOfJsonLogs = "skywalking-logs-json";

    private int kafkaHandlerThreadPoolSize;

    private int kafkaHandlerThreadPoolQueueSize;

    private String namespace = "";

    private String mm2SourceAlias = "";

    private String mm2SourceSeparator = "";

}
```


## 6. protobuf
> 具体参考: https://github.com/apache/skywalking-data-collect-protocol/

## 7. kafka命令
```bash
# 查看消费组的堆积情况
$ ./kafka-consumer-groups.sh --bootstrap-server localhost:9092  --describe --group skywalking-consumer

# 查看trace信息
$ ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic skywalking-segments  --from-beginning

# 查看上报业务日志
$ ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic skywalking-logs --from-beginning
```



