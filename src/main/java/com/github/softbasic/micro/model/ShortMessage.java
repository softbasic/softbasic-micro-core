package com.github.softbasic.micro.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 短信系统参数
 */
@Data
public class ShortMessage implements Serializable, Cloneable{
    private static final long serialVersionUID = 1L;

    private String phone;
    private String message;

    /**
     * 转换为JSON字符串
     * @return
     */
    public String toJsonString(){
        return JSON.toJSONString(this);
    }
}
