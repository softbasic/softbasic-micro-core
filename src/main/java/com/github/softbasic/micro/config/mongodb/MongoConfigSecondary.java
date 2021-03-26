package com.github.softbasic.micro.config.mongodb;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty(prefix="spring.data.mongodb",name = "enable", havingValue = "true")
public class MongoConfigSecondary {

    // mongoClient
    @Bean
    @Autowired
    public MongoClient mongoClientSecondary(MongoConfigProperties properties) {

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
        MongoClient mongoClientSecondary = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(clusterBuilder ->
                                clusterBuilder.serverSelectionTimeout(properties.getServerSelectionTimeout(), TimeUnit.SECONDS)
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
                        .readPreference(ReadPreference.secondaryPreferred())
                        .writeConcern(WriteConcern.JOURNALED)
                        .credential(mongoCredential)
                        .build());

        // 创建MongoDbFactory
        return mongoClientSecondary;
    }


    // 覆盖容器中默认的MongoDbFactory Bean
    @Bean
    @Autowired
    public MongoDatabaseFactory mongoDbFactorySecondary(MongoClient mongoClientSecondary,MongoConfigProperties properties) {
        // 创建MongoDbFactory
        return new SimpleMongoClientDatabaseFactory(mongoClientSecondary,properties.getDatabase());
    }

    @Bean
    MongoTemplate mongoSecondary(MongoDatabaseFactory mongoDatabaseFactorySecondary){
        return new MongoTemplate(mongoDatabaseFactorySecondary);
    }
}