package com.lib_common.net.rxhttp.help;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import com.lib_common.app.BaseApplication;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class ExceptionHelper {
    //处理网络异常
    public static <T> String handleNetworkException(T throwable) {
        int stringId = -1;
        if (throwable instanceof UnknownHostException) {
            if (!isNetworkConnected(BaseApplication.getApplication())) {
                stringId = com.lib_src.R.string.network_error;
            } else {
                stringId = com.lib_src.R.string.notify_no_network;
            }
        } else if (throwable instanceof SocketTimeoutException || throwable instanceof TimeoutException) {
            //前者是通过OkHttpClient设置的超时引发的异常，后者是对单个请求调用timeout方法引发的超时异常
            stringId = com.lib_src.R.string.time_out_please_try_again_later;
        } else if (throwable instanceof ConnectException) {
            stringId = com.lib_src.R.string.esky_service_exception;
        }
        return stringId == -1 ? null : BaseApplication.getApplication().getString(stringId);
    }

    @SuppressWarnings("deprecation")
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission")
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }
}
