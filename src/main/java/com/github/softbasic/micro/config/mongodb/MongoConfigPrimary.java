package com.github.softbasic.micro.config.mongodb;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.types.Decimal128;

@Configuration
@ConditionalOnProperty(prefix="spring.data.mongodb",name = "enable", havingValue = "true")
public class MongoConfigPrimary {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfigPrimary.class);

    // 覆盖容器中默认的MongoDbFactory Bean
    @Bean
    @Autowired
    @Primary
    public MongoDatabaseFactory mongoDatabaseFactoryPrimary(MongoConfigProperties properties) {

        //builder.applyToConnectionPoolSettings(block);
        
        /*ConnectionPoolSettings.Builder poolBuilder = ConnectionPoolSettings.builder();
        poolBuilder.maxSize(properties.getMaxConnectionsPerHost()); //最大连接数
        poolBuilder.minSize(properties.getMinConnectionsPerHost()); //最小连接数
        poolBuilder.maxConnectionIdleTime(properties.getMaxConnectionIdleTime(), TimeUnit.SECONDS); 
        poolBuilder.maxWaitTime(properties.getMaxWaitTime(),TimeUnit.SECONDS);
        poolBuilder.maxConnectionLifeTime(properties.getMaxConnectionLifeTime(),TimeUnit.SECONDS);*/


        /*ServerSettings.Builder serverBuilder = ServerSettings.builder();
        serverBuilder.heartbeatFrequency(properties.getHeartbeatFrequency(),TimeUnit.SECONDS);
        serverBuilder.minHeartbeatFrequency(properties.getMinHeartbeatFrequency(),TimeUnit.SECONDS);*/


        List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
        for (String address : properties.getAddress()) {
            String[] hostAndPort = address.split(":");
            String host = hostAndPort[0];
            Integer port = Integer.parseInt(hostAndPort[1]);
            ServerAddress serverAddress = new ServerAddress(host, port);
            serverAddresses.add(serverAddress);
        }
        /*ClusterSettings.Builder clusterBuilder = ClusterSettings.builder();
        clusterBuilder.serverSelectionTimeout(properties.getServerSelectionTimeout(),TimeUnit.SECONDS);
        clusterBuilder.localThreshold(properties.getLocalThreshold(),TimeUnit.SECONDS);
        if (properties.getReplicaSet() != null) {
            clusterBuilder.requiredReplicaSetName(properties.getReplicaSet());
        }
        clusterBuilder.hosts(serverAddresses);
        clusterBuilder.mode(ClusterConnectionMode.MULTIPLE);
        clusterBuilder.requiredClusterType(ClusterType.REPLICA_SET);*/



        /*SocketSettings.Builder socketBuilder = SocketSettings.builder();
        socketBuilder.connectTimeout(properties.getConnectTimeout(),TimeUnit.SECONDS);*/
        //socketBuilder.readTimeout();
        //socketBuilder.receiveBufferSize();
        //socketBuilder.sendBufferSize();

        /*SslSettings.Builder sslBuilder = SslSettings.builder();
        sslBuilder.enabled(properties.getSslEnabled());
        sslBuilder.invalidHostNameAllowed(true);*/

        //builder.applyConnectionString();
        /*Block<ConnectionPoolSettings.Builder> poolBlock = new Block<ConnectionPoolSettings.Builder>() {
            @Override
            public void apply(ConnectionPoolSettings.Builder builder) {
                builder = poolBuilder;
            }
        };*/



        // 连接认证
        MongoCredential mongoCredential = null;
        if (properties.getUsername() != null) {
            mongoCredential = MongoCredential.createScramSha1Credential(
                    properties.getUsername(), properties.getAuthenticationDatabase() != null
                            ? properties.getAuthenticationDatabase() : properties.getDatabase(),
                    properties.getPassword().toCharArray());
        }

        //MongoClient mongoClientPrimary = new MongoClient(serverAddresses, mongoCredential,null);

        MongoClient mongoClientPrimary = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(clusterBuilder ->
                                clusterBuilder.serverSelectionTimeout(properties.getServerSelectionTimeout(),TimeUnit.SECONDS)
                                .localThreshold(properties.getLocalThreshold(),TimeUnit.SECONDS)
                                .requiredReplicaSetName(properties.getReplicaSet())
                                .hosts(serverAddresses)
                                .mode(ClusterConnectionMode.MULTIPLE)
                                .requiredClusterType(ClusterType.REPLICA_SET))
                        .applyToConnectionPoolSettings(poolBuilder ->
                                poolBuilder.maxSize(properties.getMaxConnectionsPerHost()) //最大连接数
                                .minSize(properties.getMinConnectionsPerHost()) //最小连接数
                                .maxConnectionIdleTime(properties.getMaxConnectionIdleTime(), TimeUnit.SECONDS)
                                .maxWaitTime(properties.getMaxWaitTime(),TimeUnit.SECONDS)
                                .maxConnectionLifeTime(properties.getMaxConnectionLifeTime(),TimeUnit.SECONDS))
                        .applyToSslSettings(sslBuilder ->
                                sslBuilder.enabled(properties.getSslEnabled())
                                .invalidHostNameAllowed(properties.getSslInvalidHostNameAllowed()))
                        .applyToServerSettings(serverBuilder ->
                                serverBuilder.heartbeatFrequency(properties.getHeartbeatFrequency(),TimeUnit.SECONDS)
                                .minHeartbeatFrequency(properties.getMinHeartbeatFrequency(),TimeUnit.SECONDS))

                        .applyToSocketSettings(socketBuilder ->
                                socketBuilder.connectTimeout(properties.getConnectTimeout(),TimeUnit.SECONDS))
                        .readPreference(ReadPreference.primary())
                        .writeConcern(WriteConcern.JOURNALED)
                        .credential(mongoCredential)
                        .build());
        //---------builder.connectionsPerHost(properties.getMaxConnectionsPerHost());
        //---------builder.minConnectionsPerHost(properties.getMinConnectionsPerHost());
        /*if (properties.getReplicaSet() != null) {
            //---------builder.requiredReplicaSetName(properties.getReplicaSet());
        }*/
        //builder.threadsAllowedToBlockForConnectionMultiplier(
        //        properties.getThreadsAllowedToBlockForConnectionMultiplier());
        //---------builder.serverSelectionTimeout(properties.getServerSelectionTimeout());
        //---------builder.maxWaitTime(properties.getMaxWaitTime());
        //---------builder.maxConnectionIdleTime(properties.getMaxConnectionIdleTime());
        //---------builder.maxConnectionLifeTime(properties.getMaxConnectionLifeTime());
        //---------builder.connectTimeout(properties.getConnectTimeout());
        //#########builder.socketTimeout(properties.getSocketTimeout());
        // builder.socketKeepAlive(properties.getSocketKeepAlive());
        //---------builder.sslEnabled(properties.getSslEnabled());
        //---------builder.sslInvalidHostNameAllowed(properties.getSslInvalidHostNameAllowed());
        //builder.alwaysUseMBeans(properties.getAlwaysUseMBeans());
        //---------builder.heartbeatFrequency(properties.getHeartbeatFrequency());
        //---------builder.minHeartbeatFrequency(properties.getMinHeartbeatFrequency());
        //#########builder.heartbeatConnectTimeout(properties.getHeartbeatConnectTimeout());
        //---------builder.readPreference(ReadPreference.primary()); //使用只在主服务器上读取
        //---------builder.readPreference(ReadPreference.secondary());
        //#########builder.heartbeatSocketTimeout(properties.getHeartbeatSocketTimeout());
        //---------builder.localThreshold(properties.getLocalThreshold());
        //---------builder.writeConcern(WriteConcern.JOURNALED);
        //MongoClientOptions mongoClientOptions = builder.build();

        // MongoDB地址列表
        /*List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
        for (String address : properties.getAddress()) {
            String[] hostAndPort = address.split(":");
            String host = hostAndPort[0];
            Integer port = Integer.parseInt(hostAndPort[1]);
            ServerAddress serverAddress = new ServerAddress(host, port);
            serverAddresses.add(serverAddress);
        }

        logger.info("serverAddresses:" + serverAddresses.toString());

        // 连接认证


        // 创建认证客户端
        assert mongoCredential != null;
        MongoClient mongoClientPrimary = new MongoClient(serverAddresses, mongoCredential, mongoClientOptions);*/


        // 创建MongoDbFactory
        return new SimpleMongoClientDatabaseFactory(mongoClientPrimary, properties.getDatabase());
    }

    /**
     * 事务管理器
     * @return
     */
    @Bean
    MongoTransactionManager transactionManagerPrimary(MongoDatabaseFactory mongoDatabaseFactoryPrimary) {
        return new MongoTransactionManager(mongoDatabaseFactoryPrimary);
    }


    // Direction: Java -> MongoDB
    @WritingConverter
    public class BigDecimalToDecimal128Converter  implements Converter<BigDecimal, Decimal128> {
        @Override
        public Decimal128  convert(BigDecimal source) {
            return new Decimal128(source);
        }
    }

    // Direction: MongoDB -> Java
    @ReadingConverter
    public class Decimal128ToBigDecimalConverter  implements Converter<Decimal128, BigDecimal> {
        @Override
        public BigDecimal convert(Decimal128  source) {
            return source.bigDecimalValue();
        }
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter> converterList = new ArrayList<Converter>();
        converterList.add(new BigDecimalToDecimal128Converter());
        converterList.add(new Decimal128ToBigDecimalConverter());
        return new MongoCustomConversions(converterList);
    }



    @Bean
    @Primary
    MongoTemplate mongoPrimary(MongoDatabaseFactory mongoDatabaseFactoryPrimary){
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDatabaseFactoryPrimary);
        MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
        mongoMapping.setCustomConversions(customConversions()); // tell mongodb to use the custom converters
        mongoMapping.afterPropertiesSet();
        return mongoTemplate;
    }
}