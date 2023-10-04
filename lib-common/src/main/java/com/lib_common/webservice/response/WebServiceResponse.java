package com.lib_common.webservice.response;

import java.io.Serializable;
import java.util.List;

public class WebServiceResponse implements Serializable {
    private String status;
    private int errorCode;
    private String reason;
    private String obj; // 单实体
    private String data; // json数组

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }

}
