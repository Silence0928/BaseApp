package com.lib_common.view.layout.dialog.update;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.FileProvider;


import com.hjq.toast.ToastUtils;
import com.lib_common.R;
import com.lib_common.constants.Constants;
import com.lib_common.dialog.LoadingDialog;
import com.lib_common.view.layout.dialog.update.action.AnimAction;
import com.lib_common.view.layout.dialog.update.download.AppUtils;
import com.lib_common.view.layout.dialog.update.download.DownLoadCallBack;
import com.lib_common.view.layout.dialog.update.download.DownLoadState;
import com.lib_common.view.layout.dialog.update.download.HttpDownloadCallBack;
import com.lib_common.view.layout.dialog.update.download.OkHttpDownloadUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

import okhttp3.Call;
import okhttp3.Response;


/**
 * author: zhaoke
 * blog  :
 * time  :2020/6/12 20:26
 * desc  : The constants of memory.
 */
public class UpdateDialog {

    public static final class Builder
            extends BaseDialog.Builder<Builder> implements DownLoadCallBack {

        private final TextView mNameView;
        private final TextView mContentView;
        private final ProgressBar mProgressView;
        private final ImageView mImgLine;
        private final TextView mUpdateView;
        private final TextView mCloseView;
        private boolean forceUpdate = false;
        /**
         * Apk 文件
         */
        private File mApkFile;
        /**
         * 下载地址
         */
        private String mDownloadUrl;
        /**
         * 文件 MD5
         */
        private String mFileMD5;
        /**
         * 当前是否下载中
         */
        private boolean mDownloading;
        /**
         * 当前是否下载完毕
         */
        private boolean mDownloadComplete;
        private Context context;
        private LoadingDialog progressLoading;
        private updateClick updateClick;
        private String newVersion;

        public Builder(Context context) {
            super(context);
            this.context = context;
            progressLoading = new LoadingDialog(context);
            setContentView(R.layout.dialog_update);
            setAnimStyle(AnimAction.ANIM_BOTTOM);
            setCancelable(false);

            mNameView = findViewById(R.id.txt_title);
            mContentView = findViewById(R.id.txt_msg);
            mProgressView = findViewById(R.id.pb_update_progress);

            mUpdateView = findViewById(R.id.btn_pos);
            mCloseView = findViewById(R.id.btn_neg);
            mImgLine = findViewById(R.id.img_line);
            setOnClickListener(R.id.btn_pos, R.id.btn_neg);
        }

        @Override
        public void downLoadApk(long length) {
            downLoad(length);
        }

        @Override
        public void downLoadComplete() {
            mDownloadComplete = true;
            mUpdateView.setText("下载完成");
            //下载成功
            installApk();
        }

        @Override
        public void downLoading(int progress) {
            mDownloading = true;
            progressLoading.dismissDialog();
            mProgressView.setVisibility(View.VISIBLE);
            mUpdateView.setText(String.format(getString(com.lib_src.R.string.update_status_running), progress));
            mProgressView.setProgress(progress);
        }

        @Override
        public void downLoadFailed(String errMsg) {
            mDownloading = false;
            progressLoading.dismissDialog();
            mUpdateView.setText("下载出错啦,请重试");
        }

        private static class UIHandler extends Handler {

            private final WeakReference<DownLoadCallBack> calBack;

            public UIHandler(Looper looper, DownLoadCallBack downLoadCallBack) {
                super(looper);
                this.calBack = new WeakReference<>(downLoadCallBack);
            }

            @Override
            public void handleMessage(@NonNull Message msg) {

                switch (msg.what) {
                    case DownLoadState.START_DOWN_LOAD: // 开始下载
                        if (calBack != null && calBack.get() != null) {
                            calBack.get().downLoadApk((Long) msg.obj);
                        }
                        break;
                    case DownLoadState.DOWN_LOAD_COMPLETE: // 下载完成
                        if (calBack != null && calBack.get() != null) {
                            calBack.get().downLoadComplete();
                        }
                        break;
                    case DownLoadState.DOWN_LOAD_FAILED: // 下载失败
                        if (calBack != null && calBack.get() != null) {
                            calBack.get().downLoadFailed("");
                        }
                        break;
                    case DownLoadState.DOWN_LOADING: // 下载中
                        if (calBack != null && calBack.get() != null) {
                            calBack.get().downLoading((Integer) msg.obj);
                        }
                        break;
                    default:
                        super.handleMessage(msg);
                        break;
                }
            }
        }

        private final Handler mHandler = new UIHandler(Looper.getMainLooper(), this);

        /**
         * 设置版本名
         */
        public Builder setVersionName(CharSequence name) {
            mNameView.setText(name);
            return this;
        }

        /**
         * 设置新版本号
         */
        public Builder setNewVersion(String version) {
            newVersion = version;
            return this;
        }

        /**
         * 设置更新日志
         */
        public Builder setUpdateLog(CharSequence text) {
            if (TextUtils.isEmpty(text)){
                mContentView.setVisibility(View.GONE);
                return this;
            }
            mContentView.setText(((String) text).replace("\\n", "\n"));
            mContentView.setVisibility(View.VISIBLE);
            return this;
        }

        /**
         * 设置强制更新
         */
        public Builder setForceUpdate(boolean force) {
            forceUpdate = force;
            mCloseView.setVisibility(force ? View.GONE : View.VISIBLE);
            mImgLine.setVisibility(force ? View.GONE : View.VISIBLE);
            return this;
        }

        /**
         * 设置下载 url
         */
        public Builder setDownloadUrl(String url) {
            mDownloadUrl = url;
            return this;
        }

        public Builder setClickUpDate(updateClick clickUpDate) {
            updateClick = clickUpDate;
            return this;
        }

        /**
         * 设置文件 md5
         */
        public Builder setFileMD5(String md5) {
            mFileMD5 = md5;
            return this;
        }

        @Override
        public void onClick(View v) {
            if (v == mCloseView) {
                Constants.isShowUpdateDialog = false;
                dismiss();
                updateClick.onDismiss();
            } else if (v == mUpdateView) {
                if (!forceUpdate) {
                    updateClick.onClickUpDate();
                    dismiss();
                    return;
                }

                if (mDownloadComplete) {
                    installApk();
                } else if (mDownloading) {
                    ToastUtils.showShort("正在下载中,请稍等!");
                } else {
                    downloadApk();
                }
            }
        }

        /**
         * 下载 Apk
         */
        @SuppressLint("CheckResult")
        private void downloadApk() {
            progressLoading.showDialog("正在创建下载任务");
            // 获取APK大小
            new Thread(() -> {
                Looper.prepare();
                try {
                    URL url = new URL(mDownloadUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    final long total = conn.getContentLength();
                    Message message = new Message();
                    message.what = DownLoadState.START_DOWN_LOAD;
                    message.obj = total;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    if (e instanceof FileNotFoundException) {
                        toastError(com.lib_src.R.string.download_failure_file_not_found);
                    } else if (e instanceof ConnectException) {
                        toastError(com.lib_src.R.string.download_failure_net_deny);
                    } else if (e instanceof UnknownHostException) {
                        toastError(com.lib_src.R.string.download_failure_net_deny);
                    } else if (e instanceof UnknownServiceException) {
                        toastError(com.lib_src.R.string.download_failure_net_deny);
                    } else if (e.toString().contains("Permission denied")) {
                        toastError(com.lib_src.R.string.download_failure_storage_permission_deny);
                    } else {
                        toastError(com.lib_src.R.string.apk_update_download_failed);
                    }
                }
                Looper.loop();
            }).start();
        }

        private void downLoad(long total) {
            // 创建要下载的文件对象
            String path = this.context.getExternalCacheDir() + "/" +
                    AppUtils.getAppName(context) +
                    newVersion + ".apk";
            mApkFile = new File(path);
            final long length = mApkFile.length();
            if (mApkFile.exists() && length == total) {
                // 已下载APK,直接去安装
                progressLoading.dismissDialog();
                installApk();
                return;
            }
            OkHttpDownloadUtil downloadUtil = new OkHttpDownloadUtil();
            downloadUtil.getDownloadRequest(mDownloadUrl, mApkFile, length, total, new HttpDownloadCallBack() {
                @Override
                public void onFailure(Call call, IOException e, long totalLength, long downloadLength) {
                    //下载失败
                    Message message = new Message();
                    message.what = DownLoadState.DOWN_LOAD_FAILED;
                    mHandler.sendMessage(message);
                }

                @Override
                public void inProgress(Call call, Response response, long totalLength, long downloadLength) {
                    // 下载进度回调,0-100，仅在进度有更新时才会回调
                    Message message = new Message();
                    message.what = DownLoadState.DOWN_LOADING;
                    int progress = (int) (downloadLength * 1.0 / totalLength * 100);
                    message.obj = progress;
                    mHandler.sendMessage(message);
                }

                @Override
                public void onResponse(Call call, Response response, long totalLength, long downloadLength) {
                    if (totalLength == downloadLength) {
                        // 标记成下载完成
                        Message message = new Message();
                        message.what = DownLoadState.DOWN_LOAD_COMPLETE;
                        mHandler.sendMessage(message);
                    }
                }
            });
        }

        /**
         * Toast error message
         *
         * @param id res id
         */
        private void toastError(@StringRes int id) {
            Looper.prepare();
            Toast.makeText(context, context.getResources().getString(id), Toast.LENGTH_LONG).show();
            Looper.loop();
        }

        /**
         * 安装 Apk
         */
        private void installApk() {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(getContext(), Constants.PROVIDER_AUTHORITIES, mApkFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(mApkFile);
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
//            BaseApplication.getApplication().exitApp();
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(0);
        }

        public interface updateClick {
            void onClickUpDate();

            void onDismiss();
        }

    }

}
