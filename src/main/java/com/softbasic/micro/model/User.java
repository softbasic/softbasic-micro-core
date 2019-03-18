package com.softbasic.micro.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * Created by LCR on 2018/12/20.
 */
@Data
public class User {
    private String id;
    private String userName;
    private String password;
    @JSONField(format="yyyy-MM-dd")
    private Date createTime;
}
