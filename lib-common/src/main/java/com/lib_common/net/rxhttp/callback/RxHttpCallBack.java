package com.lib_common.net.rxhttp.callback;

public interface RxHttpCallBack<R extends Object> {
    /**
     * 开始请求
     */
    void onStart();

    /**
     * 请求成功
     *
     * @param response
     */
    void onSuccess(R response);

    /**
     * 请求异常
     *
     * @param error
     */
    void onError(ErrorInfo error);

    /**
     * 请求结束
     */
    void onFinish();
}
