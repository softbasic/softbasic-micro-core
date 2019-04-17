package com.github.softbasic.micro.model;

public class SuccessResult extends MicroResult{
    public SuccessResult(Object data) {
        super(SuccessStatus.OK, data);
    }
    public SuccessResult(SuccessStatus status,Object data) {
        super(status, data);
    }
}
