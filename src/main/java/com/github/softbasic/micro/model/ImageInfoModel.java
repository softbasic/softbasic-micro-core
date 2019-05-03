package com.github.softbasic.micro.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ImageInfoModel extends BaseVo{
    private String path;
    private Integer width;
    private Integer height;
}
