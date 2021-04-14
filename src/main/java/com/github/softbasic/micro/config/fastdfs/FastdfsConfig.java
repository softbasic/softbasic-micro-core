package com.github.softbasic.micro.config.fastdfs;

import org.csource.fastdfs.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jmx.support.RegistrationPolicy;

@Configuration
// 解决jmx重复注册bean的问题
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@ConditionalOnProperty(prefix="fastdfs",name = "enable", havingValue = "true")
public class FastdfsConfig {

    @Bean
    public StorageClient storageClient() throws Exception{
        ClientGlobal.initByProperties("fastdfs.properties");
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);
        return storageClient;
    }
}