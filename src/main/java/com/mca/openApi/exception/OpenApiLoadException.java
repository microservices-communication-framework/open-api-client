package com.mca.openApi.exception;

public class OpenApiLoadException extends RuntimeException{

    private String openApiLocation;

    public OpenApiLoadException(String openApiLocation) {
        this.openApiLocation = openApiLocation;
    }

    @Override
    public String getMessage() {
        return "Unable to load openApi sepcification: "+this.openApiLocation;
    }
}
