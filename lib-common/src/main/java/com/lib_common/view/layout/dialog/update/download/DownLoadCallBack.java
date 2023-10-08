package com.lib_common.view.layout.dialog.update.download;

/**
 * author: zhaohongtao
 * time  :2021/6/25 15:07
 * desc : The constants of memory.
 */
public interface DownLoadCallBack {
    /**
     * 开始下载Apk
     * @param length 已下载大小
     */
    void downLoadApk(long length);

    /**
     * 下载完成
     */
    void downLoadComplete();

    /**
     * 下载中
     * @param progress 下载进度（0-100）
     */
    void downLoading(int progress);

    /**
     * 下载失败
     */
    void downLoadFailed(String errMsg);
}
