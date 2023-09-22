package com.stas.whms.utils;

import androidx.lifecycle.LifecycleOwner;

import com.lib_common.net.rxhttp.RxHttpRequestUtil;
import com.lib_common.net.rxhttp.callback.RxHttpCallBack;

import io.reactivex.disposables.Disposable;

public class StasHttpRequestUtil {
    /**
     * 登录
     * @param owner
     * @param req
     * @param callBack
     * @return
     */
    public static Disposable login(LifecycleOwner owner, String req, RxHttpCallBack<String> callBack) {
        return RxHttpRequestUtil.Companion.getInstance().postData("", req, String.class, owner, callBack);
    }
}
