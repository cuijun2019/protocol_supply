package com.etone.protocolsupply.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @Description 通用包装返回值
 * @Date 2018/12/12 上午10:25
 * @Author maozhihui
 * @Version V1.0
 **/
@Data
public class ResponseValue<T> {

    private static int default_code = 100;
    private static String default_message = "success";

    /**
     * 应用错误码
     */
    private int code;
    /**
     * 应用错误信息
     */
    private String message;
    /**
     * 应用返回值内容
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private T data;

    private ResponseValue(){}

    public static ResponseBuilder createBuilder(){
        return new ResponseBuilder();
    }

    public static class ResponseBuilder<T>{
        private ResponseValue responseValue;

        private ResponseBuilder(){
            responseValue = new ResponseValue();
            responseValue.code = default_code;
            responseValue.message = default_message;
        }

        public ResponseBuilder code(int code){
            responseValue.code = code;
            return this;
        }

        public ResponseBuilder message(String message){
            responseValue.message = message;
            return this;
        }

        public ResponseBuilder data(T data){
            responseValue.data = data;
            return this;
        }

        public ResponseValue build(){
            return responseValue;
        }
    }
}
