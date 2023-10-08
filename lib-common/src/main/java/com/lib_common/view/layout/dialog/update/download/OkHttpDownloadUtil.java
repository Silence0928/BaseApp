package com.lib_common.view.layout.dialog.update.download;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * author: zhaohongtao
 * time  :2021/6/24 15:20
 * desc : The constants of memory.
 */
public class OkHttpDownloadUtil {
    private final String TAG = this.getClass().getSimpleName();
    private Call mCall;
    private String downloadUrl;//url
    private long downloadLength = 0;//已经下载长度
    private long totalLength = 0;//整体文件大小
    private File file;//保存文件
    private HttpDownloadCallBack mHttpDownListener;//下载进度接口回调

    public void getDownloadRequest(final String downloadUrl, final File file, Long already, Long total, final HttpDownloadCallBack listener){
        this.downloadUrl = downloadUrl;
        this.downloadLength = already;
        this.totalLength = total;
        this.file = file;
        this.mHttpDownListener = listener;

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .header("RANGE", "bytes=" + downloadLength + "-")
                .build();
        mCall = okHttpClient.newCall(request);
        //发送请求
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (mHttpDownListener != null) {
                    mHttpDownListener.onFailure(call, e,totalLength, downloadLength);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                InputStream inputStream = responseBody.byteStream();
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                if (totalLength == 0){
                    totalLength = responseBody.contentLength();
                    randomAccessFile.setLength(totalLength);
                }
                if (downloadLength != 0){
                    randomAccessFile.seek(downloadLength);
                }
                byte[] bytes = new byte[2048];
                int len = 0;
                try {
                    while ((len = inputStream.read(bytes)) != -1) {
                        randomAccessFile.write(bytes,0,len);
                        downloadLength = downloadLength + len;
                        if (mHttpDownListener != null) {
                            mHttpDownListener.inProgress(call, response, totalLength, downloadLength);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    downloadLength = randomAccessFile.getFilePointer();
                    randomAccessFile.close();
                    inputStream.close();
                    if (mHttpDownListener != null) {
                        mHttpDownListener.onResponse(call, response, totalLength, downloadLength);
                    }
                }

            }
        });
    }

    /**
     * 停止下载
     */
    public void stopDownload(){
        if (mCall!=null){
            mCall.cancel();
        }
    }

}
