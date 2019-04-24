package com.github.softbasic.micro.model;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class SchedulerModel {

    private String jobName;
    private String jobClass;
    private String cron;
    private Object data;
}
