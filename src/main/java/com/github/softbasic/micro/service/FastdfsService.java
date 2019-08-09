package com.github.softbasic.micro.service;


import com.github.softbasic.micro.exception.BusinessException;
import com.github.softbasic.micro.model.ImageInfoModel;
import com.github.softbasic.micro.result.MicroStatus;
import com.github.tobato.fastdfs.domain.MataData;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;

import static com.github.softbasic.micro.result.MicroStatus.FASTDFS_UPLOAD_ERROR;
import static com.github.softbasic.micro.result.MicroStatus.FASTDFS_UPLOAD_NOT_IMAGE;

/**
 * 文件上传服务
 */
@Service
public class FastdfsService {
    private static Logger log = LoggerFactory.getLogger(FastdfsService.class);

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    /**
     * 上传文件
     * @param file
     * @return 文件路径，例如："group1/M00/00/00/wKgU6Vy4G6-AZEaJAAAnQiwQQ-E39.xlsx"
     */
    public String upload(MultipartFile file){
        try {
            // 设置文件信息
            Set<MataData> mataData = new HashSet<>();
            // 上传   （文件上传可不填文件信息，填入null即可）
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), mataData);
            return storePath.getFullPath();
        }catch (Exception exception){
            throw new BusinessException(FASTDFS_UPLOAD_ERROR,exception);
        }
    }

    /**
     * 上传文件,本方法没有扩展名，底层会报错.请使用另一个重载方法
     * @param inputStream
     * @return 文件路径，例如："group1/M00/00/00/wKgU6Vy4G6-AZEaJAAAnQiwQQ-E39.xlsx"
     */
    @Deprecated
    public String upload(InputStream inputStream){
        try {
            // 设置文件信息
            Set<MataData> mataData = new HashSet<>();
            // 上传   （文件上传可不填文件信息，填入null即可）
            StorePath storePath = fastFileStorageClient.uploadFile(inputStream, 0, "", mataData);
            return storePath.getFullPath();
        }catch (Exception exception){
            throw new BusinessException(FASTDFS_UPLOAD_ERROR,exception);
        }
    }
    /**
     * 上传文件
     * @param inputStream
     * @return 文件路径，例如："group1/M00/00/00/wKgU6Vy4G6-AZEaJAAAnQiwQQ-E39.xlsx"
     */
    public String upload(InputStream inputStream,String fileExtName){
        try {
            // 设置文件信息
            Set<MataData> mataData = new HashSet<>();
            // 上传   （文件上传可不填文件信息，填入null即可）
            StorePath storePath = fastFileStorageClient.uploadFile(inputStream, inputStream.available(), fileExtName, mataData);
            return storePath.getFullPath();
        }catch (Exception exception){
            throw new BusinessException(FASTDFS_UPLOAD_ERROR,exception);
        }
    }
    /**
     * 上传文件
     * @param file
     * @return 文件路径，例如："group1/M00/00/00/wKgU6Vy4G6-AZEaJAAAnQiwQQ-E39.xlsx"
     */
    public String upload(File file){

        try {
            // 设置文件信息
            Set<MataData> mataData = new HashSet<>();
            if(file.isDirectory()){
                throw new BusinessException(MicroStatus.FASTDFS_UPLOAD_DIR_ERROR);
            }
            String fileName = file.getName();
            String fileExtName=fileName.substring(fileName.lastIndexOf("."));
            StorePath storePath = fastFileStorageClient.uploadFile(new FileInputStream(file), file.length(), fileExtName, mataData);
            return storePath.getFullPath();
        }catch (Exception exception){
            throw new BusinessException(FASTDFS_UPLOAD_ERROR,exception);
        }
    }

    /**
     * 文件上传（带缩略图的）
     *
     * @param file
     * @return
     */
    public ImageInfoModel uploadImage(@RequestParam MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String prefix=fileName.substring(fileName.lastIndexOf("."));
            // 创建临时文件
            File files = File.createTempFile(prefix, String.valueOf(System.currentTimeMillis()));
            FileUtils.copyInputStreamToFile(file.getInputStream(), files);
            // 通过临时文件获取图片流
            BufferedImage bufferedImage = ImageIO.read(files);
            if (bufferedImage == null) {
                // 证明上传的文件不是图片，获取图片流失败，不进行下面的操作
                throw new BusinessException(FASTDFS_UPLOAD_NOT_IMAGE);
            }

            Integer width = bufferedImage.getWidth(); // 通过图片流获取图片宽度
            Integer height = bufferedImage.getHeight(); // 通过图片流获取图片高度

            //上传到文件服务器
            String path=upload(file);

            //构建返回值
            ImageInfoModel imageInfoModel=new ImageInfoModel();
            imageInfoModel.setPath(path);
            imageInfoModel.setWidth(width);
            imageInfoModel.setHeight(height);

            return imageInfoModel;
        } catch (Exception exception) {
            throw new BusinessException(FASTDFS_UPLOAD_ERROR,exception);
        }
    }
}
