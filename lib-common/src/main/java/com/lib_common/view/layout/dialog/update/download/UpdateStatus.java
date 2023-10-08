package com.lib_common.view.layout.dialog.update.download;

/**
 * author: zhaoke
 * blog  :
 * time  :2020/4/16 11:08
 * desc  : 未下载，下载中，下载完成，下载失败，待安装.
 */
public interface UpdateStatus {
    public final static int UN_DOWNLOAD = -1;
    public final static int DOWNLOADING = 0;
    public final static int DOWNLOAD_ERROR = 1;
    public final static int UNINSTALL = 2;
}
