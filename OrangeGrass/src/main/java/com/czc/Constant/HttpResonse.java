package com.czc.Constant;

import static com.czc.Constant.HttpConstant.*;
import static com.czc.Constant.RequestConstant.*;

public class HttpResonse<T>{
    private int code;
    private int status;
    private String msg;
    private T data;

    public static HttpResonse success(){
        HttpResonse httpResonse = new HttpResonse();
        httpResonse.code = REQUEST_SUCCESS;
        httpResonse.status = HTTP_SUCCESS;
        httpResonse.msg = "success";
        return httpResonse;
    }

    public static <T> HttpResonse success(T data){
        HttpResonse httpResonse = new HttpResonse();
        httpResonse.code = REQUEST_SUCCESS;
        httpResonse.status = HTTP_SUCCESS;
        httpResonse.msg = "success";
        httpResonse.data = data;
        return httpResonse;
    }

    public static  HttpResonse fail(){
        HttpResonse httpResonse = new HttpResonse();
        httpResonse.code = REQUEST_FAILED;
        httpResonse.status = HTTP_SUCCESS;
        httpResonse.msg = "fail";
        return httpResonse;
    }

    public static <T> HttpResonse fail(T data){
        HttpResonse httpResonse = new HttpResonse();
        httpResonse.code = REQUEST_FAILED;
        httpResonse.status = HTTP_SUCCESS;
        httpResonse.msg = "fail";
        httpResonse.data = data;
        return httpResonse;
    }

    public int getCode() {
        return code;
    }

    public HttpResonse setCode(int code) {
        this.code = code;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public HttpResonse setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public HttpResonse setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public HttpResonse setData(T data) {
        this.data = data;
        return this;
    }

}
