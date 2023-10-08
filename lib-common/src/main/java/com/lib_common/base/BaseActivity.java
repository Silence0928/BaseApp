package com.lib_common.base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.lib_common.R;
import com.lib_common.base.mvvm.BaseMvvmActivity;
import com.lib_common.constants.Constants;
import com.lib_common.constants.MmkvConstants;
import com.lib_common.dialog.CommonAlertDialog;
import com.lib_common.dialog.LoadingDialog;
import com.lib_common.entity.ScanResult;
import com.lib_common.utils.AndroidUtil;
import com.lib_common.view.layout.ActionBar;
import com.lib_common.view.layout.dialog.ErrorDialog;
import com.lib_common.view.layout.dialog.update.BaseDialog;
import com.lib_common.view.layout.dialog.update.UpdateDialog;
import com.lib_common.view.layout.dialog.update.download.AppUtils;
import com.lib_common.view.layout.dialog.update.download.DownloadInstaller;
import com.lib_common.view.layout.dialog.update.download.DownloadProgressCallBack;
import com.lib_common.view.layout.dialog.update.download.UpdateBean;
import com.lib_common.webservice.SoapClientUtil;
import com.lib_common.webservice.api.WebApi;
import com.lib_common.webservice.api.WebMethodApi;
import com.lib_common.webservice.response.WebServiceResponse;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 基类Activity 其它Activity继承此Activity
 * 推荐继承 {@link BaseMvvmActivity}
 * created by yhw
 * date 2022/11/9
 */
public abstract class BaseActivity extends AppCompatActivity {
    //iscan 默认扫描结果广播
    private static final String RES_ACTION = "android.intent.action.SCANRESULT";
    private static final String RES_LABEL = "value";
    protected ActionBar mActionBar;
    protected LoadingDialog mLoadingDialog;
    protected MMKV mMMKV;
    protected ImmersionBar mImmersionBar;
    BroadcastReceiver scanReceiver;
    private static final int MIN_DELAY_TIME = 1000;  // 两次点击间隔不能少于500ms
    private static long lastClickTime;
    private static long lastCheckAPPUpdateTime; // 记录上次请求升级接口的时间
    private static final int APP_CHECK_UPDATE_DELAY_TIME = 3000;  // 请求升级接口间隔时间
    private BaseDialog updateDialog; // 升级弹窗

    /**
     * 避免快速点击
     *
     * @return
     */
    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        initImmersionBar(com.lib_src.R.color.main_color);
        if (!isDataBinding()) {
            //每个界面添加actionbar
            if (isShowActionBar()) {
                setContentView(R.layout.activity_base_layout);
                ((ViewGroup) findViewById(R.id.fl_content)).addView(getLayoutInflater().inflate(getLayoutId(), null));
                mActionBar = findViewById(R.id.actionbar);
            } else {
                setContentView(getLayoutId());
            }
            initView();
            onViewEvent();
            initSoftKeyboard();
        }
        if (isRegisterEventBus()) {
            EventBus.getDefault().register(this);
        }
        if (isRegisterScan()) {
            registerBroadcast();
        }
        mMMKV = MMKV.defaultMMKV();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCheckAppUpdate()) {
            initAPPCheckUpdate();
        }
    }

    /**
     * 设置沉浸式状态栏颜色
     *
     * @param color
     */
    protected void initImmersionBar(int color) {
        mImmersionBar = ImmersionBar.with(this);
        if (color != 0) {
            mImmersionBar.statusBarColor(color);
        }
        mImmersionBar.init();
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerBroadcast() {
        //扫描结果广播监听注册
        if ("IData".equalsIgnoreCase(Build.BRAND)) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(RES_ACTION);
            //注册广播接受者
            scanReceiver = new ScannerResultReceiver();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(scanReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(scanReceiver, intentFilter);
            }
        }
    }

    /**
     * 初始化控件
     */
    protected void initView() {

    }

    /**
     * View 事件处理方法，如点击，滑动，放大等
     */
    protected abstract void onViewEvent();

    /**
     * 布局文件
     */
    protected abstract int getLayoutId();

    /**
     * 是否为 DataBinding
     */
    protected boolean isDataBinding() {
        return false;
    }

    /**
     * 是否注册EventBus
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

    /**
     * 是否显示actionbar
     */
    protected boolean isShowActionBar() {
        return true;
    }

    /**
     * 是否注册扫描仪
     */
    protected boolean isRegisterScan() {
        return false;
    }

    protected void scanResultCallBack(ScanResult result) {
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        if (mActionBar != null) {
            mActionBar.setCenterText(getString(titleId));
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (mActionBar != null) {
            mActionBar.setCenterText(title);
        }
    }

    /**
     * 隐藏ActionBar返回按钮
     */
    public void hideActionBarBack() {
        mActionBar.getLeftView().setVisibility(View.INVISIBLE);
    }

    /**
     * 显示加载框
     */
    public void showLoading() {
        showLoading(null);
    }

    /**
     * 显示加载框
     */
    public void showLoading(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        mLoadingDialog.showDialog(msg);
    }

    /**
     * 隐藏加载框
     */
    public void dismissLoading() {
        if (mLoadingDialog == null) {
            return;
        }
        mLoadingDialog.dismissDialog();
    }

    /**
     * 初始化软键盘
     */
    protected void initSoftKeyboard() {
        // 点击外部隐藏软键盘，提升用户体验
        getContentView().setOnClickListener(v -> {
            // 隐藏软键，避免内存泄漏
            hideKeyboard(getCurrentFocus());
        });
    }

    @Override
    public void finish() {
        super.finish();
        // 隐藏软键，避免内存泄漏
        hideKeyboard(getCurrentFocus());
    }

    /**
     * 隐藏软键盘
     */
    protected void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager == null) {
            return;
        }
        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 和 setContentView 对应的方法
     */
    public ViewGroup getContentView() {
        return findViewById(Window.ID_ANDROID_CONTENT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegisterEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        if (scanReceiver != null) {
            unregisterReceiver(scanReceiver);
        }
        dismissLoading();
    }

    /**
     * 扫描结果广播接收
     */
    //*********重要
    private class ScannerResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("idata", "intent.getAction()-->" + intent.getAction());//

            if (Objects.equals(intent.getAction(), RES_ACTION)) {
                //获取扫描结果
                String scan_data = intent.getStringExtra(RES_LABEL);
                if (scan_data != null) {
                    Log.e("idata", "recv = " + scan_data);
                    ScanResult result = new ScanResult();
                    result.setData(scan_data);
                    runOnUiThread(() -> scanResultCallBack(result));
                }
            }
        }
    }

    EditText editText = null;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                // 点击了EditText控件外部，使EditText失去焦点
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    hideSoftKeyboard();
                    if (editText != null) {
                        editText.clearFocus();
                    }
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 此处必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    /**
     * 判断是否点击了EditText外部
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            editText = (EditText) v;
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int right = left + v.getWidth();
            int bottom = top + v.getHeight();
            return !(event.getX() > left && event.getX() < right
                    && event.getRawY() > top && event.getRawY() < bottom);
        }
        return false;
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeyboard() {
        // 隐藏软键盘，避免软键盘引发的内存泄露
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * WebService结果处理，含异常错误处理
     *
     * @param response
     * @param fromSource
     */
    public void handleWebServiceResult(WebServiceResponse response, int fromSource) {
        runOnUiThread(() -> {
            dismissLoading();
            if (response != null) {
                if (response.getErrorCode() == 200) {
                    handleWebServiceSuccess(response, fromSource);
                } else if (response.getErrorCode() == 300) {
                    // 发生错误，需班长解锁
                    new ErrorDialog(this, new ErrorDialog.ErrorHandleCallBack() {
                        @Override
                        public void commitModify(Dialog dialog, String workNo, String pwd, String remark) {
                            dialog.dismiss();
                        }

                        @Override
                        public void cancel() {
                            finish();
                        }
                    }).builder().show(response.getReason());
                } else {
                    ToastUtils.show(response.getReason());
                }
            }
        });
    }

    /**
     * 处理WebService成功回调
     *
     * @param response
     * @param fromSource
     */
    protected void handleWebServiceSuccess(WebServiceResponse response, int fromSource) {
    }

    /**
     * 是否检测APP升级，默认检测
     *
     * @return
     */
    protected boolean isCheckAppUpdate() {
        return false;
    }

    /**
     * 升级弹窗关闭
     */
    protected void dismissUpdateDialog() {
    }

    /**
     * APP升级
     */
    public void initAPPCheckUpdate() {
        if (Constants.isNotificationBack && !TextUtils.isEmpty(Constants.appUpdateUrl)) {
            Constants.isNotificationBack = false;
            showInstall(Constants.appUpdateUrl);
            return;
        }
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastCheckAPPUpdateTime) >= APP_CHECK_UPDATE_DELAY_TIME) {
            // 满足时间，获取APP版本升级信息
            lastCheckAPPUpdateTime = currentTime;
            if (updateDialog != null && updateDialog.isShowing()) {
                return;
            }
            getAPPVersionInfo();
        }
    }

    private void getAPPVersionInfo() {
//        Map<String, String> req = new HashMap<>();
//        req.put("versionId", AndroidUtil.getAppVersionName(this));
//        WebServiceResponse response = SoapClientUtil.execute(JSON.toJSONString(req), WebApi.scannerUrl, WebMethodApi.scannerMethod);
//        if (response != null && response.getErrorCode() == 200 && response.getObj() != null) {
//            UpdateBean res = JSONObject.parseObject(response.getObj(), UpdateBean.class);
//            mMMKV.encode(MmkvConstants.MMKV_UPDATE_INFO, res);
//            checkVersion(res);
//        } else {
//            dismissUpdateDialog();
//        }
        UpdateBean res = new UpdateBean();
        res.setAppName("仓储管理");
        res.setForceUpdate("1");
        res.setNewVersion("1.1.1");
        res.setUpdateVersion("1.1.2");
        res.setLastForceUpdateVer("1.0.0");
        res.setUpdateLink("https://huoda-tms-public.oss-cn-beijing.aliyuncs.com/shipper-app/shipper.apk");
        mMMKV.encode(MmkvConstants.MMKV_UPDATE_INFO, res);
        checkVersion(res);
    }

    /**
     * 新版本检测
     *
     * @param updateBean
     */
    private void checkVersion(UpdateBean updateBean) {
        showVersionUpdateDialog(updateBean, false);
    }

    public void showVersionUpdateDialog(UpdateBean updateBean, boolean isUpdateVersion) {
        if (updateBean == null || TextUtils.isEmpty(updateBean.getForceUpdate())) {
            dismissUpdateDialog();
            return;
        }
        String updateVersion = "";
        if (isUpdateVersion) {
            // 系统管理-检测新版本用
            updateVersion = updateBean.getUpdateVersion();
            updateBean.setForceUpdate("1");
        } else {
            updateVersion = updateBean.getNewVersion();
        }
        if (!TextUtils.isEmpty(updateVersion)) {
            // 比较新旧版本，若大于等于新版本，不做处理
            int newVersion = Integer.parseInt(updateVersion.replace(".", ""));
            int currentVersion = AppUtils.getVersionCode(this);
            if (currentVersion >= newVersion) {
                dismissUpdateDialog();
                return;
            }
        }
        switch (updateBean.getForceUpdate()) {
            // 1：普通更新 2：强制更新
            case "1":
                if (!Constants.isShowUpdateDialog && !isUpdateVersion) {
                    dismissUpdateDialog();
                    return;
                }
                updateDialog = new UpdateDialog.Builder(this)
                        // 版本名
                        .setVersionName("仓储管理APP已升级至" + updateVersion + ":")
                        .setNewVersion(updateVersion)
                        // 是否强制更新
                        .setForceUpdate(false)
                        // 更新日志
                        .setUpdateLog(updateBean.getUpdateDesc())
                        // 下载 url
                        .setDownloadUrl(updateBean.getUpdateLink())
                        .setClickUpDate(new UpdateDialog.Builder.updateClick() {
                            @Override
                            public void onClickUpDate() {
                                dismissUpdateDialog();
                                Constants.appUpdateUrl = updateBean.getUpdateLink();
                                showInstall(Constants.appUpdateUrl);
                            }

                            @Override
                            public void onDismiss() {
                                dismissUpdateDialog();
                            }
                        })
                        .show();
                break;
            case "2":
                updateDialog = new UpdateDialog.Builder(this)
                        // 版本名
                        .setVersionName("仓储管理APP已升级至" + updateVersion + ":")
                        .setNewVersion(updateVersion)
                        // 是否强制更新
                        .setForceUpdate(true)
                        // 更新日志
                        .setUpdateLog(updateBean.getUpdateDesc())
                        // 下载 url
                        .setDownloadUrl(updateBean.getUpdateLink())
                        .show();
                break;
            default:
                dismissUpdateDialog();
                break;
        }

    }

    private void showInstall(String url) {
        boolean Jurisdiction = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if (!Jurisdiction) {
            new CommonAlertDialog(this).setTitle("通知设置")
                    .setMessage("是否打开通知栏提示获得更好的下载体验?")
                    .setCancelable(false)
                    .setCancelText("暂不开启")
                    .setOnCancelClickListener(s -> {
                        downLoad(url);
                    })
                    .setConfirmText("立即设置")
                    .setOnConfirmClickListener(s -> {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                            Constants.isNotificationBack = true;
                            Intent intent = new Intent();
                            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                            intent.putExtra("app_package", this.getPackageName());
                            intent.putExtra("app_uid", this.getApplicationInfo().uid);
//                            startActivityForResult(intent, ACTIVITY_RESULT_NOTI);
                            startActivity(intent);
                        } else {
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", this.getPackageName(), null));
//                            startActivityForResult(localIntent, ACTIVITY_RESULT_NOTI);
                            startActivity(localIntent);
                            Constants.isNotificationBack = true;
                        }
                    }).show();
        } else {
            downLoad(url);
        }
    }

    private void downLoad(String url) {
        //一般的弹出对话框提示升级
        //如果是企业内部应用升级，肯定是要这个权限; 其他情况不要太流氓，TOAST 提示
        new DownloadInstaller(this, url, true, new DownloadProgressCallBack() {
            @Override
            public void downloadProgress(int progress) {
            }

            @Override
            public void downloadException(Exception e) {
            }

            @Override
            public void downloading() {
                ToastUtils.showShort("正在下载App");
            }


            @Override
            public void onInstallStart() {
            }
        }).start(this);
    }
}
