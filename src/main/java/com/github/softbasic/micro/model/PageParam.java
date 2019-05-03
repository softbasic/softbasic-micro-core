package com.github.softbasic.micro.model;

import com.github.softbasic.micro.utils.BaseUtils;
import lombok.Data;

@Data
public class PageParam {
    private Integer pageNo;
    private Integer pageSize;
    private String  property;
    private String  direction;

    public String getDirection() {
        if(BaseUtils.isBlank(direction)){
            return "ASC".trim();
        }else if(direction.trim().equals("ASC".trim())||direction.trim().equals("DESC".trim())){
            return direction.trim();
        }else {
            return "ASC".trim();
        }
    }

    public String getProperty(){
        if(BaseUtils.isBlank(property)){
            return "_id".trim();
        }else {
            return property.trim();
        }
    }
}