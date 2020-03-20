package com.etone.protocolsupply.exception;

import lombok.Getter;

/**
 * @Description //TODO
 * @Date 2018/12/27 下午4:01
 * @Author maozhihui
 * @Version V1.0
 **/
public enum GlobalExceptionCode {

    SUCCESS(100, "success"),
    LOGOUT_ERROR(201,"logout failed"),
    SERVICE_ERROR(300, "service internal handle error"),
    NOTNULL_ERROR(400, "不能为空"),
    IS_DIGIT_ERROR(302, "应该为数字"),
    IS_DOUBLE_ERROR(303, "应该为浮点类型"),
    NOT_FOUND_ERROR(422, "找不到对应的数据"),
    FORMAT_ERROR(305, "");

    @Getter
    private int code;
    @Getter
    private String cause;

    public String getCause(String field) {
        return field + cause;
    }

    GlobalExceptionCode(int code, String cause) {
        this.code = code;
        this.cause = cause;
    }
}
