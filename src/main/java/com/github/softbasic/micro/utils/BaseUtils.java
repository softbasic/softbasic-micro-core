package com.github.softbasic.micro.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.*;

public final class BaseUtils {
    public static boolean isNotBlank(String str) {
        return StringUtils.isNotBlank(str);
    }

    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }
    /**
     * 解析JSON字符串到对象的每个字段
     * @param obj
     * @param request
     * @return
     */
    public static Object parseObject(Object obj, HttpServletRequest request) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            try {
                if (!field.getName().equalsIgnoreCase("serialVersionUID")) {
                    field.setAccessible(true);
                    Class cls = field.getType();
                    String value = request.getParameter(field.getName());
                    if (field.getType().isArray()) {
                        if (value != null) {
                            List<?> list = JSON.parseArray(value, cls.getComponentType());
                            field.set(obj, list.toArray());
                        }
                    } else if (field.getType() == List.class) {
                        if (value != null) {
                            ParameterizedType pt = (ParameterizedType) field.getGenericType();
                            Class genericClazz = (Class) pt.getActualTypeArguments()[0];
                            List<?> list = JSON.parseArray(value, genericClazz);
                            field.set(obj, list);
                        }
                    } else if (field.getType() == Set.class) {
                        if (value != null) {
                            ParameterizedType pt = (ParameterizedType) field.getGenericType();
                            Class genericClazz = (Class) pt.getActualTypeArguments()[0];
                            List<?> list = JSON.parseArray(value, genericClazz);
                            field.set(obj, new HashSet(list));
                        }
                    } else if (field.getType() == Collection.class) {
                        if (value != null) {
                            ParameterizedType pt = (ParameterizedType) field.getGenericType();
                            Class genericClazz = (Class) pt.getActualTypeArguments()[0];
                            List<?> list = JSON.parseArray(value, genericClazz);
                            field.set(obj, list);
                        }
                    } else if (cls == String.class) {
                        field.set(obj, value);
                    } else if (cls == Date.class) {
                        field.set(obj, value == null ? null : new Date(Long.parseLong(value)));
                    } else if (cls == Integer.class) {
                        field.set(obj, value == null ? null : Integer.valueOf(value));
                    } else if (cls == Long.class) {
                        field.set(obj, value == null ? null : Long.valueOf(value));
                    } else if (cls == Float.class) {
                        field.set(obj, value == null ? null : Float.valueOf(value));
                    } else if (cls == Double.class) {
                        field.set(obj, value == null ? null : Double.valueOf(value));
                    } else if (cls == BigDecimal.class) {
                        field.set(obj, value == null ? null : new BigDecimal(value));
                    } else if (cls == Byte.class) {
                        field.set(obj, value == null ? null : Byte.valueOf(value));
                    } else if (cls == Boolean.class) {
                        field.set(obj, value == null ? null : Boolean.valueOf(value));
                    } else if (cls == Short.class) {
                        field.set(obj, value == null ? null : Short.valueOf(value));
                    } else if (cls == Character.class) {
                        field.set(obj, value == null ? null : Character.valueOf(value.charAt(0)));
                    } else if (cls == JSONObject.class) {
                        field.set(obj, value == null ? null : JSON.parseObject(value));
                    } else if (cls.isEnum()) {
                        field.set(obj, value == null ? null : Enum.valueOf(cls, value));
                    } else {
                        field.set(obj, value == null ? null : JSON.parseObject(value, cls));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return obj;
    }
}