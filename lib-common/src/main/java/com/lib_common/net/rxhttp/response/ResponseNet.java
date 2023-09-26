package com.lib_common.net.rxhttp.response;

public class ResponseNet<T> {
    private int ErrorCode;
    private String Reason;
    private T DataTableSubset;
    private String message;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(int errorCode) {
        this.ErrorCode = errorCode;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        this.Reason = reason;
    }

    public T getDataTableSubset() {
        return DataTableSubset;
    }

    public void setDataTableSubset(T dataTableSubset) {
        this.DataTableSubset = dataTableSubset;
    }
}
