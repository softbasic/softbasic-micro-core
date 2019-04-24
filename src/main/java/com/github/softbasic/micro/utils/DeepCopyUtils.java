package com.github.softbasic.micro.utils;


import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.util.SerializationUtils;

import java.io.*;

public class DeepCopyUtils {

    /**
     * 默认采用SerializationUtils 需要bean 实现序列化 平均耗时10.1ms
     * @param source
     * @param target
     */
    public static void copy(Object source, Object target) {
        copySerialize(source,target);
    }

    /**
     * 采用手动io流序列化 ，需要bean 实现序列化 平均耗时9.8
     * @param source
     * @param target
     */
    public static void copyIo(Object source, Object target)  {
        try{
            //1、采用手动io流序列化 ，需要bean 实现序列化 平均耗时9.8
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(source);
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object temp = ois.readObject();

            //通过临时对象，将属性值，赋给b
            BeanUtils.copyProperties(temp,target);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 使用工具序列化SerializationUtils 需要bean 实现序列化 平均耗时10.1
     * @param source
     * @param target
     */
    public static void copySerialize(Object source, Object target)  {
        //2、使用工具序列化SerializationUtils 需要bean 实现序列化 平均耗时10.1
        Object temp = SerializationUtils.deserialize(SerializationUtils.serialize(source));

        //通过临时对象，将属性值，赋给b
        BeanUtils.copyProperties(temp,target);
    }

    /**
     * 依赖json包，bean无需实现序列化 平均耗时98.8
     * @param source
     * @param target
     */
    public static void copyJson(Object source, Object target)  {
        //3、下面转json得形式，依赖json包（反射原理） 依赖json包，bean无需实现序列化 平均耗时98.8
        Object temp = JSON.parseObject(JSON.toJSONString(source), source.getClass());

        //通过临时对象，将属性值，赋给b
        BeanUtils.copyProperties(temp,target);
    }

}
