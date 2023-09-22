package com.lib_common.net.rxhttp.callback;

public interface RxHttpFile<T> {
    /**
     * 开始请求
     */
    void onStart();

    /**
     * 请求成功
     *
     * @param response
     */
    void onSuccess(T response);

    /**
     * 上传进度或下载进度
     *
     * @param progress    当前进度
     * @param currentSize 已上传或下载字节大小
     * @param totalSize   总字节大小
     */
    void onProgress(int progress, long currentSize, long totalSize);

    /**
     * 请求异常
     *
     * @param error
     */
    void onError(String error);

    /**
     * 请求结束
     */
    void onFinish();
}
