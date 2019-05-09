package com.github.softbasic.micro.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseDto implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private PageParam page;

    public PageParam getPage() {
        if(page==null){
            PageParam page=new PageParam();
            page.setPageNo(1);
            page.setPageSize(20);
            return page;
        }
        return page;
    }
}