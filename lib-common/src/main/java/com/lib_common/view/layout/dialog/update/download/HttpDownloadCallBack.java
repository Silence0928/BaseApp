package com.lib_common.view.layout.dialog.update.download;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * author: zhaohongtao
 * time  :2021/6/24 15:21
 * desc : The constants of memory.
 */
public interface HttpDownloadCallBack {
    void onFailure(Call call, IOException e, long totalLength, long downloadLength);
    void inProgress(Call call, Response response, long totalLength, long downloadLength);
    void onResponse(Call call, Response response, long totalLength, long downloadLength);
}
