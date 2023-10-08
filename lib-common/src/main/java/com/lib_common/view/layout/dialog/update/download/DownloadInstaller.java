package com.lib_common.view.layout.dialog.update.download;

import static com.yunxiaobao.tms.lib_common.util.download.DownLoadState.DOWN_LOADING;
import static com.yunxiaobao.tms.lib_common.util.download.DownLoadState.DOWN_LOAD_COMPLETE;
import static com.yunxiaobao.tms.lib_common.util.download.DownLoadState.DOWN_LOAD_FAILED;
import static com.yunxiaobao.tms.lib_common.util.download.DownLoadState.START_DOWN_LOAD;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.ArrayMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.huoda.shipper.common.R;
import com.yunxiaobao.tms.lib_common.constants.Comments;
import com.yunxiaobao.tms.lib_common.util.activitylauncher.ActivityLauncher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * author: zhaoke
 * blog  :
 * time  :2020/4/16 10:57
 * desc  : The constants of memory.
 */
public class DownloadInstaller {

    private String authority;
    private static final String intentType = "application/vnd.android.package-archive";

    private NotificationManager notificationManager;
    private Notification notification;
    private NotificationCompat.Builder builder;

    private Context mContext;
    private int progress;
    private int oldProgress;

    private boolean isForceGrantUnKnowSource;

    //新包的下载地址
    private String downloadApkUrl;
    private String downloadApkUrlMd5;
    private int downloadApkNotifyId;

    //local saveFilePath
    private String storageApkPath;

    //事件监听器
    private DownloadProgressCallBack downloadProgressCallBack;

    //保存下载状态信息，临时过度的方案。
    public static ArrayMap<String, Integer> downLoadStatusMap = new ArrayMap<>();

    private String storagePrefix;

    private File apkFile;

    /**
     * 不需要下载进度回调的
     *
     * @param context        上下文
     * @param downloadApkUrl apk 下载地址
     */
    public DownloadInstaller(Context context, String downloadApkUrl) {
        this(context, downloadApkUrl, false, null);
    }


    /**
     * 需要下载进度回调的
     *
     * @param context        上下文
     * @param downloadApkUrl apk下载地址
     * @param callBack       进度状态回调
     */
    public DownloadInstaller(Context context, String downloadApkUrl, DownloadProgressCallBack callBack) {
        this(context, downloadApkUrl, false, callBack);
    }


    /**
     * 下载安装App
     *
     * @param context                  上下文
     * @param downloadApkUrl           下载URL
     * @param isForceGrantUnKnowSource 是否是强制的要授权未知来源
     * @param callBack                 回调
     */
    public DownloadInstaller(Context context, String downloadApkUrl, boolean isForceGrantUnKnowSource, DownloadProgressCallBack callBack) {
        this.mContext = context;
        this.downloadApkUrl = downloadApkUrl;
        this.isForceGrantUnKnowSource = isForceGrantUnKnowSource;
        this.downloadProgressCallBack = callBack;
    }


    /**
     * 获取16位的MD5 值，大写
     *
     * @param str
     * @return
     */
    private String getUpperMD5Str16(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString().toUpperCase().substring(8, 24);
    }


    /**
     * app下载升级管理
     */
    public void start(Context context) {

        String applicationID = mContext.getPackageName();
        //防止不同的app 下载同一个链接的App 失败
        downloadApkUrlMd5 = getUpperMD5Str16(downloadApkUrl + applicationID);
        downloadApkNotifyId = downloadApkUrlMd5.hashCode();

        //https://developer.android.com/studio/build/application-id?hl=zh-cn
//        authority = applicationID + ".provider";
        authority = Comments.PROVIDER_AUTHORITIES;

        //前缀要统一 一下 + AppUtils.getAppName(mContext)+"/Download/"
        storagePrefix = context.getExternalCacheDir() + "/";
//        storagePrefix = Environment.getExternalStorageDirectory().getPath() + "/";
        storageApkPath = storagePrefix + AppUtils.getAppName(mContext) + downloadApkUrlMd5 + ".apk";

        Integer downloadStatus = downLoadStatusMap.get(downloadApkUrlMd5);

        if (downloadStatus == null || downloadStatus == UpdateStatus.UN_DOWNLOAD || downloadStatus == UpdateStatus.DOWNLOAD_ERROR) {
            initNotification();
            //如果没有正在下载&&没有下载好了还没有升级
            new Thread(mDownApkRunnable).start();
        } else if (downloadStatus == UpdateStatus.DOWNLOADING) {
            // 正在下载
            if (downloadProgressCallBack != null) {
                downloadProgressCallBack.downloading();
            }
        }

    }


    /**
     * 下载线程,使用最原始的HttpURLConnection，减少依赖
     * 大的APK下载还是比较慢的，后面改为多线程下载
     */
    private Runnable mDownApkRunnable = new Runnable() {
        @Override
        public void run() {
            downLoadStatusMap.put(downloadApkUrlMd5, UpdateStatus.DOWNLOADING);
            try {
                URL url = new URL(downloadApkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                long total = conn.getContentLength();

                File file = new File(storagePrefix);
                if (!file.exists()) {
                    file.mkdir();
                }

                apkFile = new File(storageApkPath);

                if (apkFile.exists() && apkFile.length() == total) {
                    //已经下载过了，直接的progress ==100,然后去安装
                    progress = 100;
                    updateNotify(progress);
                    if (downloadProgressCallBack != null) {
                        downloadProgressCallBack.downloadProgress(progress);
                    }
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downLoadStatusMap.put(downloadApkUrlMd5, UpdateStatus.UNINSTALL);
                            installProcess();
                        }
                    });

                    return;
                }
                Message message = new Message();
                message.what = START_DOWN_LOAD;
                message.obj = total;
                mHandler.sendMessage(message);
            } catch (Exception e) {
                downLoadStatusMap.put(downloadApkUrlMd5, UpdateStatus.DOWNLOAD_ERROR);
                //后面有时间再完善异常的处理
                String errMsg;
                if (e instanceof FileNotFoundException) {
                    errMsg = getStringFrom(R.string.download_failure_file_not_found);
                } else if (e instanceof ConnectException) {
                    errMsg = getStringFrom(R.string.download_failure_net_deny);
                } else if (e instanceof UnknownHostException) {
                    errMsg = getStringFrom(R.string.download_failure_net_deny);
                } else if (e instanceof UnknownServiceException) {
                    errMsg = getStringFrom(R.string.download_failure_net_deny);
                } else if (e.toString().contains("Permission denied")) {
                    errMsg = getStringFrom(R.string.download_failure_storage_permission_deny);
                } else {
                    errMsg = getStringFrom(R.string.apk_update_download_failed);
                }
                notifyError(errMsg);
                toastError(errMsg);
                if (downloadProgressCallBack != null) {
                    downloadProgressCallBack.downloadException(new Exception(errMsg));
                }
            } finally {
                //finally do something
            }
        }
    };

    private void downLoad(File apkFile, long total) {
        OkHttpDownloadUtil downloadUtil = new OkHttpDownloadUtil();
        downloadUtil.getDownloadRequest(downloadApkUrl, apkFile, apkFile.length(), total, new HttpDownloadCallBack() {

            @Override
            public void onFailure(Call call, IOException e, long totalLength, long downloadLength) {
                Message message = new Message();
                message.what = DOWN_LOAD_FAILED;
                message.obj = getStringFrom(R.string.apk_update_download_failed);
                mHandler.sendMessage(message);
            }

            @Override
            public void inProgress(Call call, Response response, long totalLength, long downloadLength) {
                Message message = new Message();
                message.what = DOWN_LOADING;
                int progress = (int) (downloadLength * 1.0 / totalLength * 100);
                message.obj = progress;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response, long totalLength, long downloadLength) {
                Message message = new Message();
                message.what = DOWN_LOAD_COMPLETE;
                mHandler.sendMessage(message);
            }
        });
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
                case START_DOWN_LOAD: // 开始下载
                    if (calBack != null && calBack.get() != null) {
                        calBack.get().downLoadApk((Long) msg.obj);
                    }
                    break;
                case DOWN_LOAD_COMPLETE: // 下载完成
                    if (calBack != null && calBack.get() != null) {
                        calBack.get().downLoadComplete();
                    }
                    break;
                case DOWN_LOAD_FAILED: // 下载失败
                    if (calBack != null && calBack.get() != null) {
                        calBack.get().downLoadFailed((String) msg.obj);
                    }
                    break;
                case DOWN_LOADING: // 下载中
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

    private final Handler mHandler = new UIHandler(Looper.getMainLooper(), new DownLoadCallBack() {
        @Override
        public void downLoadApk(long total) {
            downLoad(apkFile, total);
        }

        @Override
        public void downLoadComplete() {
            progress = 100;
            downLoadStatusMap.put(downloadApkUrlMd5, UpdateStatus.UNINSTALL);
            installProcess();
        }

        @Override
        public void downLoading(int progress) {
            updateNotify(progress);
            if (downloadProgressCallBack != null) {
                downloadProgressCallBack.downloadProgress(progress);
            }
        }

        @Override
        public void downLoadFailed(String errMsg) {
            downLoadStatusMap.put(downloadApkUrlMd5, UpdateStatus.DOWNLOAD_ERROR);
            if (downloadProgressCallBack != null) {
                downloadProgressCallBack.downloadException(new Exception(getStringFrom(R.string.apk_update_download_failed)));
            }
        }
    });

    /**
     * get String from id
     *
     * @param id res id
     * @return string
     */
    @NonNull
    public String getStringFrom(@StringRes int id) {
        return mContext.getResources().getString(id);
    }

    /**
     * Toast error message
     *
     * @param id res id
     */
    private void toastError(String id) {
        Looper.prepare();
        Toast.makeText(mContext, id, Toast.LENGTH_LONG).show();
        Looper.loop();
    }


    /**
     * 安装过程处理
     */
    public void installProcess() {
        if (progress < 100) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean canInstallPackage = mContext.getPackageManager().canRequestPackageInstalls();
            final Integer downloadStatus = downLoadStatusMap.get(downloadApkUrlMd5); //unboxing

            if (canInstallPackage) {
                if (downloadStatus == UpdateStatus.UNINSTALL) {
                    installApk();
                }
            } else {
                Uri packageURI = Uri.parse("package:" + AppUtils.getPackageName(mContext));
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);

                //检查是否可以安装未知来源的应用，没有权限就一直去尝试，我感觉这样子是很流氓的...
                //在这里拦截OnActivityResult,不要代码割裂
                ActivityLauncher.init((Activity) mContext).startActivityForResult(intent, new ActivityLauncher.Callback() {
                    @Override
                    public void onActivityResult(int resultCode, Intent data) {
                        //授权了就去安装
                        if (resultCode == Activity.RESULT_OK) {
                            if (downloadStatus == UpdateStatus.UNINSTALL) {
                                installProcess();
                            }
                        } else {
                            //如果是企业内部应用升级，肯定是要这个权限，其他情况不要太流氓，TOAST 提示
                            if (isForceGrantUnKnowSource) {
                                installProcess();
                            } else {
                                Toast.makeText(mContext, "你没有授权安装App", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        } else {
            installApk();
        }

    }


    /**
     * 跳转到安装apk的页面
     */
    private void installApk() {
        File apkFile = new File(storageApkPath);
        if (!apkFile.exists()) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(mContext, authority, apkFile);
            intent.setDataAndType(contentUri, intentType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), intentType);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        downLoadStatusMap.put(downloadApkUrlMd5, UpdateStatus.UN_DOWNLOAD);
        /**
         * 开始安装了
         */
        if (downloadProgressCallBack != null) {
            downloadProgressCallBack.onInstallStart();
        }
    }


    /**
     * 初始化通知 initNotification
     */
    private void initNotification() {
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(downloadApkUrlMd5, downloadApkUrlMd5, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
        }

        builder = new NotificationCompat.Builder(mContext, downloadApkUrl);
        builder.setContentTitle(mContext.getResources().getString(R.string.apk_update_tips_title)) //设置通知标题
                .setSmallIcon(R.drawable.download)
                .setDefaults(Notification.DEFAULT_LIGHTS) //设置通知的提醒方式： 呼吸灯
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
                .setAutoCancel(true)  //
                .setOngoing(true)     // 不可以删除
                .setContentText(mContext.getResources().getString(R.string.apk_update_downloading_progress))
                .setChannelId(downloadApkUrlMd5)
                .setProgress(100, 0, false);
        notification = builder.build();//构建通知对象
    }


    /**
     * 通知下载更新过程中的错误信息
     *
     * @param errorMsg 错误信息
     */
    private void notifyError(String errorMsg) {
        builder.setContentTitle(mContext.getResources().getString(R.string.apk_update_tips_error_title));
        builder.setContentText(errorMsg);
        notification = builder.build();
        notificationManager.notify(downloadApkNotifyId, notification);
    }


    /**
     * 更新下载的进度
     *
     * @param progress
     */
    private void updateNotify(int progress) {
        builder.setProgress(100, progress, false);
        builder.setContentText(mContext.getResources().getString(R.string.apk_update_downloading_progress) + " 「" + progress + "%」");
        notification = builder.build();

        //点击通知栏到安装界面，可能下载好了，用户没有安装
        if (progress == 100) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(mContext, authority, new File(storageApkPath));
                intent.setDataAndType(contentUri, intentType);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.setDataAndType(Uri.parse("file://" + new File(storageApkPath).toString()), intentType);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notification.contentIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        }
        notificationManager.notify(downloadApkNotifyId, notification);
    }
}
