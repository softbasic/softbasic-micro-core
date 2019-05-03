package com.github.softbasic.micro.model;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper=true)
public class SchedulerModel extends BaseDto{

    private String jobName;
    private String jobClassName;
    private String cron;
    private Object data;
}
