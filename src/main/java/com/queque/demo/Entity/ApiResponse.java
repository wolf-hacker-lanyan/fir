package com.queque.demo.Entity;

public class ApiResponse<T> {
    private int code;
    private T data;
    private String errorMessage;

    public ApiResponse(int code, T data, String errorMessage) {
        this.code = code;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
