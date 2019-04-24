package com.github.softbasic.micro.result;

import com.alibaba.fastjson.JSONObject;

public class ErrorResult extends MicroResult{
    public ErrorResult(Object data) {
        super(false,MicroStatus.ERROR, data);
    }
    public ErrorResult() {
        super(false,MicroStatus.ERROR, new JSONObject());
    }
}
