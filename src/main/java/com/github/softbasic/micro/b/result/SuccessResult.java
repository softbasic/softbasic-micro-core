package com.github.softbasic.micro.b.result;

public class SuccessResult extends MicroResult{
    public SuccessResult(Object data) {
        super(true,MicroStatus.OK, data);
    }
}
