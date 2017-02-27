package com.riddleandcode.nfcbasic.api;

public class RCApiResponse<T> {

    private String status;
    private int code;
    private Object data;
    private String message = "";


    public String getStatus(){
        return status;
    }

    public int getCode(){
        return code;
    }

    public Object getData(){
        return data;
    }

    public String getMessage(){
        return message;
    }

    public boolean isSuccessful(){
        return code >= 200 && code < 300;
    }

    public boolean credentialsAreInvalid(){
        return code == 401;
    }

    public boolean isNotFound(){
        return code == 404;
    }


}
