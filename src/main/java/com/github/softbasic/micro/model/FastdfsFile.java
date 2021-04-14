package com.github.softbasic.micro.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Data
public class FastdfsFile {
    private String name;
    private byte[] content;
    private String ext;
    private String md5;
    private String author;

    public FastdfsFile(){}

    public FastdfsFile(String name, byte[] content){
        this.name = name;
        this.content = content;
        this.ext = name.substring(name.lastIndexOf(".")+1,name.length());
    }

    public FastdfsFile(String name, String ext, byte[] content){
        this.name = name;
        this.content = content;
        this.ext = ext;
    }

    public FastdfsFile(String name, byte[] content, String ext, String md5, String author) {
        this.name = name;
        this.content = content;
        this.ext = ext;
        this.md5 = md5;
        this.author = author;
    }

    public FastdfsFile(MultipartFile file) throws Exception{
        this.name = file.getOriginalFilename();
        this.content = file.getBytes();
        this.ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1,file.getOriginalFilename().length());
    }

    public FastdfsFile(File file) throws Exception{
        this.name = file.getName();
        InputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        this.content = bytes;
        this.ext = name.substring(name.lastIndexOf(".")+1,name.length());
    }

    public FastdfsFile(InputStream inputStream, String fileName) throws Exception{
        this.name = fileName;
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        this.content = bytes;
        this.ext = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
    }
}
