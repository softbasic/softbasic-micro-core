package com.github.softbasic.micro.config.mongodb;

import com.mongodb.*;
import org.bson.types.Decimal128;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@ConditionalOnProperty(prefix="spring.data.mongodb",name = "enable", havingValue = "true")
public class MongoConfigPrimary {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfigPrimary.class);

    // 覆盖容器中默认的MongoDbFactory Bean
    @Bean
    @Autowired
    public MongoDbFactory mongoDbFactoryPrimary(MongoConfigProperties properties) {

        // 客户端配置（连接数，副本集群验证）
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.connectionsPerHost(properties.getMaxConnectionsPerHost());
        builder.minConnectionsPerHost(properties.getMinConnectionsPerHost());
        if (properties.getReplicaSet() != null) {
            builder.requiredReplicaSetName(properties.getReplicaSet());
        }
        builder.threadsAllowedToBlockForConnectionMultiplier(
                properties.getThreadsAllowedToBlockForConnectionMultiplier());
        builder.serverSelectionTimeout(properties.getServerSelectionTimeout());
        builder.maxWaitTime(properties.getMaxWaitTime());
        builder.maxConnectionIdleTime(properties.getMaxConnectionIdleTime());
        builder.maxConnectionLifeTime(properties.getMaxConnectionLifeTime());
        builder.connectTimeout(properties.getConnectTimeout());
        builder.socketTimeout(properties.getSocketTimeout());
        // builder.socketKeepAlive(properties.getSocketKeepAlive());
        builder.sslEnabled(properties.getSslEnabled());
        builder.sslInvalidHostNameAllowed(properties.getSslInvalidHostNameAllowed());
        builder.alwaysUseMBeans(properties.getAlwaysUseMBeans());
        builder.heartbeatFrequency(properties.getHeartbeatFrequency());
        builder.minHeartbeatFrequency(properties.getMinHeartbeatFrequency());
        builder.heartbeatConnectTimeout(properties.getHeartbeatConnectTimeout());
        builder.readPreference(ReadPreference.primary()); //使用只在主服务器上读取
        //builder.readPreference(ReadPreference.secondary());
        builder.heartbeatSocketTimeout(properties.getHeartbeatSocketTimeout());
        builder.localThreshold(properties.getLocalThreshold());
        builder.writeConcern(WriteConcern.JOURNALED);
        MongoClientOptions mongoClientOptions = builder.build();

        // MongoDB地址列表
        List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
        for (String address : properties.getAddress()) {
            String[] hostAndPort = address.split(":");
            String host = hostAndPort[0];
            Integer port = Integer.parseInt(hostAndPort[1]);
            ServerAddress serverAddress = new ServerAddress(host, port);
            serverAddresses.add(serverAddress);
        }

        logger.info("serverAddresses:" + serverAddresses.toString());

        // 连接认证
         MongoCredential mongoCredential = null;
         if (properties.getUsername() != null) {
         	mongoCredential = MongoCredential.createScramSha1Credential(
         			properties.getUsername(), properties.getAuthenticationDatabase() != null
         					? properties.getAuthenticationDatabase() : properties.getDatabase(),
         			properties.getPassword().toCharArray());
         }

        // 创建认证客户端
        assert mongoCredential != null;
        MongoClient mongoClientPrimary = new MongoClient(serverAddresses, mongoCredential, mongoClientOptions);


        // 创建MongoDbFactory
        return new SimpleMongoDbFactory(mongoClientPrimary, properties.getDatabase());
    }

    /**
     * 事务管理器
     * @return
     */
    @Bean
    MongoTransactionManager transactionManagerPrimary(MongoDbFactory mongoDbFactoryPrimary) {
        return new MongoTransactionManager(mongoDbFactoryPrimary);
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
    MongoTemplate mongoPrimary(MongoDbFactory mongoDbFactoryPrimary){
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactoryPrimary);
        MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
        mongoMapping.setCustomConversions(customConversions()); // tell mongodb to use the custom converters
        mongoMapping.afterPropertiesSet();
        return mongoTemplate;
    }




}