package com.github.softbasic.micro.a.config.mongodb;

import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfigSecondary {


    // 覆盖容器中默认的MongoDbFactory Bean
    @Bean
    @Autowired
    public MongoDbFactory mongoDbFactorySecondary(MongoConfigProperties properties) {

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
        builder.sslEnabled(properties.getSslEnabled());
        builder.sslInvalidHostNameAllowed(properties.getSslInvalidHostNameAllowed());
        builder.alwaysUseMBeans(properties.getAlwaysUseMBeans());
        builder.heartbeatFrequency(properties.getHeartbeatFrequency());
        builder.minHeartbeatFrequency(properties.getMinHeartbeatFrequency());
        builder.heartbeatConnectTimeout(properties.getHeartbeatConnectTimeout());
        builder.readPreference(ReadPreference.secondaryPreferred());//使用只在从服务器上读取
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
        MongoClient mongoClient = new MongoClient(serverAddresses, mongoCredential, mongoClientOptions);

        // 创建MongoDbFactory
        return new SimpleMongoDbFactory(mongoClient, properties.getDatabase());
    }

    @Bean
    MongoTemplate mongoSecondary(MongoDbFactory mongoDbFactorySecondary){
        return new MongoTemplate(mongoDbFactorySecondary);
    }
}