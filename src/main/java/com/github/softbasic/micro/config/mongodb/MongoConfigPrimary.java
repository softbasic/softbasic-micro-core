package com.github.softbasic.micro.config.mongodb;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterType;
import org.bson.types.Decimal128;
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

@Configuration
@ConditionalOnProperty(prefix="spring.data.mongodb",name = "enable", havingValue = "true")
public class MongoConfigPrimary {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfigPrimary.class);

    @Bean
    @Autowired
    public MongoClient mongoClientPrimary(MongoConfigProperties properties) {
        //获取mongodb地址
        List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
        for (String address : properties.getAddress()) {
            String[] hostAndPort = address.split(":");
            String host = hostAndPort[0];
            Integer port = Integer.parseInt(hostAndPort[1]);
            ServerAddress serverAddress = new ServerAddress(host, port);
            serverAddresses.add(serverAddress);
        }

        // 连接认证
        MongoCredential mongoCredential = null;
        if (properties.getUsername() != null) {
            mongoCredential = MongoCredential.createScramSha1Credential(
                    properties.getUsername(), properties.getAuthenticationDatabase() != null
                            ? properties.getAuthenticationDatabase() : properties.getDatabase(),
                    properties.getPassword().toCharArray());
        }

        //设置mongo连接
        MongoClient mongoClientPrimary = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(clusterBuilder ->
                                clusterBuilder.serverSelectionTimeout(properties.getServerSelectionTimeout(), TimeUnit.SECONDS)
                                        .localThreshold(properties.getLocalThreshold(),TimeUnit.SECONDS)
                                        .hosts(serverAddresses)
                                        .mode(ClusterConnectionMode.MULTIPLE)
                                        .requiredClusterType(ClusterType.SHARDED))
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

        // 创建MongoDbFactory
        return mongoClientPrimary;
    }

    // 覆盖容器中默认的MongoDbFactory Bean
    @Bean
    @Autowired
    @Primary
    public MongoDatabaseFactory mongoDbFactoryPrimary(MongoClient mongoClientPrimary,MongoConfigProperties properties) {
        // 创建MongoDbFactory
        return new SimpleMongoClientDatabaseFactory(mongoClientPrimary, properties.getDatabase());
    }

    /**
     * 事务管理器
     * @return
     */
    @Bean
    MongoTransactionManager transactionManagerPrimary(MongoDatabaseFactory mongoDatabaseFactoryPrimary) {
        MongoTransactionManager mongoTransactionManager = new MongoTransactionManager(mongoDatabaseFactoryPrimary);
        TransactionOptions options = TransactionOptions.builder()
                .writeConcern(WriteConcern.MAJORITY)
                .readConcern(ReadConcern.LOCAL)
                .readPreference(ReadPreference.primary()).build();
        mongoTransactionManager.setOptions(options);
        mongoTransactionManager.setDefaultTimeout(3000);
        return mongoTransactionManager;
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