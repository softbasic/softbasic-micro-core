package com.github.softbasic.micro.service;


import com.github.tobato.fastdfs.domain.MataData;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

@Service
public class FastdfsService {
    private static Logger log = LoggerFactory.getLogger(FastdfsService.class);

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    public StorePath upload(MultipartFile file){
        try {
            // 设置文件信息
            Set<MataData> mataData = new HashSet<>();
            // 上传   （文件上传可不填文件信息，填入null即可）
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), mataData);

        }catch (Exception e){

        }
        return null;

    }
}
