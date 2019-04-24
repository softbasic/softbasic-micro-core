package com.github.softbasic.micro.result;

public class SuccessResult extends MicroResult{
    public SuccessResult(Object data) {
        super(true,MicroStatus.OK, data);
    }
}
