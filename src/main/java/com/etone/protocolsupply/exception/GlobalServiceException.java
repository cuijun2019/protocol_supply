package com.etone.protocolsupply.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalServiceException extends QuarkServiceException {
    // members
    public static String COMPONENT_NAME = "global";

    private int code;

    // static block

    // constructors

    public GlobalServiceException(int code, String message) {
        super(COMPONENT_NAME, message);
        this.code = code;
    }
}
