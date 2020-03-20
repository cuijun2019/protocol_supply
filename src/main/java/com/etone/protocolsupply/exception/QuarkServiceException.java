package com.etone.protocolsupply.exception;

public class QuarkServiceException extends RuntimeException {
    // members
    private static final long   serialVersionUID = 5190641530448500359L;
    private              String componentName;

    // static block

    // constructors
    protected QuarkServiceException(String componentName) {
        this.componentName = componentName;
    }

    protected QuarkServiceException(String componentName, String message) {
        super(message);
        this.componentName = componentName;
    }

    protected QuarkServiceException(String componentName, Throwable cause) {
        super(cause);
        this.componentName = componentName;
    }

    public QuarkServiceException(String componentName, String message, Throwable cause) {
        super(message, cause);
        this.componentName = componentName;
    }

    // properties
    public String getComponentName() {
        return this.componentName;
    }
}
