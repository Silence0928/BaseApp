package com.lib_common.net.rxhttp.callback;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.hjq.toast.ToastUtils;
import com.lib_common.net.rxhttp.help.ExceptionHelper;
import com.lib_common.utils.BasicUtil;

import rxhttp.wrapper.exception.HttpStatusCodeException;
import rxhttp.wrapper.exception.ParseException;

/**
 * RxHttp异常处理工具类
 */
public class ErrorInfo {
    private String mErrorCode;  //仅指服务器返回的错误码
    private String mErrorMsg; //错误文案，网络错误、请求失败错误、服务器返回的错误等文案
    private Throwable mThrowable; //异常信息
    private Context mContext;
    private static ErrorInfo mInstance = null;
    private String mData; // 返回data结果


    public static ErrorInfo getInstance() {
        if (mInstance == null) {
            synchronized (ErrorInfo.class) {
                if (mInstance == null) {
                    mInstance = new ErrorInfo();
                }
            }
        }
        return mInstance;
    }

    public ErrorInfo() {
    }


    public void init(Context context) {
        mContext = context;
    }

    public ErrorInfo(Throwable throwable) {
        this.mThrowable = throwable;
        this.mErrorCode = throwable.getLocalizedMessage();
        String errorMsg = ExceptionHelper.handleNetworkException(throwable); //网络异常
        if (throwable instanceof HttpStatusCodeException) { //请求失败异常
            if ("401".equals(mErrorCode)) {
                this.mErrorMsg = BasicUtil.getStringById(com.lib_src.R.string.user_overdue);
//                Utils.loginAgain();
                return;
            }
            if ("416".equals(mErrorCode)) {
                errorMsg = BasicUtil.getStringById(com.lib_src.R.string.request_not_contain);
            } else {
                errorMsg = BasicUtil.getStringById(com.lib_src.R.string.server_error);
            }
        } else if (throwable instanceof JsonSyntaxException) { //请求成功，但Json语法异常,导致解析失败
            errorMsg = BasicUtil.getStringById(com.lib_src.R.string.data_parse_error);
        } else if (throwable instanceof ParseException) { // ParseException异常表明请求成功，但是数据不正确
            errorMsg = throwable.getMessage();
            if (TextUtils.isEmpty(errorMsg)) errorMsg = this.mErrorCode;//errorMsg为空，显示errorCode
        }
//        else if (throwable instanceof NetParseException) { // NetParseException异常表明请求成功，但是数据不正确
//            errorMsg = throwable.getMessage();
//            if (TextUtils.isEmpty(errorMsg)) errorMsg = this.mErrorCode;//errorMsg为空，显示errorCode
//            mData = ((NetParseException) throwable).getMData();
//        }
        this.mErrorMsg = errorMsg;
    }

    public String getErrorCode() {
        return mErrorCode == null ? "" : mErrorCode;
    }

    public String getErrorMsg() {
        return mErrorMsg == null ? "" : mErrorMsg;
    }

    public String getData() {
        return mData;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }

    public boolean show() {
        ToastUtils.showShort(TextUtils.isEmpty(mErrorMsg) ? mThrowable.getMessage() : mErrorMsg);
        return true;
    }

    public void setErrorCode(String mErrorCode) {
        this.mErrorCode = mErrorCode;
    }

    public void setErrorMsg(String mErrorMsg) {
        this.mErrorMsg = mErrorMsg;
    }
}
