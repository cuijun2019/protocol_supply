package com.etone.protocolsupply.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description //TODO
 * @Date 2018/12/2 下午5:34
 * @Author maozhihui
 * @Version V1.0
 **/
@Getter
@Setter
@AllArgsConstructor
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = -8445943548965154778L;

    private String username;
    private String password;
    private String code;

    public LoginRequest() {
        super();
    }
}
