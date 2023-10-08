package com.lib_common.view.layout.dialog.update.download;

/**
 * author: zhaoke
 * blog  :
 * time  :2020/4/16 11:06
 * desc  : 下载进度回调.
 */
public interface DownloadProgressCallBack {
    void downloadProgress(int progress);
    void downloadException(Exception e);
    void downloading();
    void onInstallStart();
}
