package com.lib_common.utils;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.hjq.toast.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by DELL on 2016/11/16.
 */
public class AndroidUtil {
    public static final String BAIDU_MAP_NAME = "com.baidu.BaiduMap";
    public static final String GAODE_MAP_NAME = "com.autonavi.minimap";
    public static final String TENCENT_MAP_NAME = "com.tencent.map";

    /**
     * 隐藏键盘
     *
     * @date 2016年3月24日 下午4:54:46
     */
    public static void hideSoftInputView(Activity activity) {
        InputMethodManager manager = ((InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE));
        if (activity.getCurrentFocus() != null)
            manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static boolean isHiddenSoftInput(Activity activity, View view) {
        InputMethodManager manager = ((InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE));
        return !manager.isActive(view);
    }

    /**
     * 显示键盘
     *
     * @date 2016年3月24日 下午4:55:07
     */
    public static void showSoftInputView(Activity activity, View view) {
        if (activity.getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null)
                ((InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(view, 0);
        }
    }

    public static boolean isSoftShowing(Activity activity) {
        //获取当屏幕内容的高度
        int screenHeight = activity.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        //DecorView即为activity的顶级view
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        //选取screenHeight*2/3进行判断
        return screenHeight * 2 / 3 > rect.bottom + getSoftButtonsBarHeight(activity);
    }

    /**
     * 判断指定的intent能否被当前手机解析
     *
     * @param intent
     * @param context
     * @return
     */
    public static boolean isIntentAvailable(Intent intent, Context context) {
        if (intent == null) {
            return false;
        }
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static float getStatusBarHeight(Context context) {
        int result = 0;
        try {
            int resource = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            result = context.getResources().getDimensionPixelSize(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * 判断虚拟导航栏是否显示
     *
     * @param context 上下文对象
     * @param window  当前窗口
     * @return true(显示虚拟导航栏)，false(不显示或不支持虚拟导航栏)
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean checkNavigationBarShow(@NonNull Context context, @NonNull Window window) {
        boolean show;
        Display display = window.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);

        View decorView = window.getDecorView();
        Configuration conf = context.getResources().getConfiguration();
        if (Configuration.ORIENTATION_LANDSCAPE == conf.orientation) {
            View contentView = decorView.findViewById(android.R.id.content);
            show = (point.x != contentView.getWidth());
        } else {
            Rect rect = new Rect();
            decorView.getWindowVisibleDisplayFrame(rect);
            show = (rect.bottom != point.y);
        }
        return show;
    }

    public static boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取所有网络连接NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<String> getMapList(Context context) {
        List<String> data = new ArrayList<>();
        if (AndroidUtil.checkMapAppsIsExist(context, BAIDU_MAP_NAME)) {
            data.add("百度地图");
        }
        if (AndroidUtil.checkMapAppsIsExist(context, TENCENT_MAP_NAME)) {
            data.add("腾讯地图");
        }
        if (AndroidUtil.checkMapAppsIsExist(context, GAODE_MAP_NAME)) {
            data.add("高德地图");
        }
        return data;
    }

    /**
     * 根据包名判断某个app是否安装
     *
     * @param packageName
     * @return
     */
    public static boolean checkMapAppsIsExist(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 打开百度地图（公交出行，起点位置使用地图当前位置）
     * <p>
     * mode = transit（公交）、driving（驾车）、walking（步行）和riding（骑行）. 默认:driving
     * 当 mode=transit 时 ： sy = 0：推荐路线 、 2：少换乘 、 3：少步行 、 4：不坐地铁 、 5：时间短 、 6：地铁优先
     *
     * @param dlat  终点纬度
     * @param dlon  终点经度
     * @param dname 终点名称
     */
    public static void openBaiduMap(Context context, double dlat, double dlon, String dname) {
        if (checkMapAppsIsExist(context, BAIDU_MAP_NAME)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("baidumap://map/direction?origin=我的位置&destination=name:"
                    + dname
                    + "|latlng:" + dlat + "," + dlon
                    + "&mode=driving&sy=3&index=0&target=1"));
            context.startActivity(intent);
        } else {
            AndroidUtil.showMessage(context, "百度地图未安装");
        }
    }

    /**
     * Show message
     *
     * @param activity Activity
     * @param msg      message
     */
    public static void showMessage(Activity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showMessage(Application application, String msg) {
        Toast.makeText(application, msg, Toast.LENGTH_SHORT).show();
    }


    public static ProgressDialog showProgress(Context context) {
        ProgressDialog window = showProgress(context,
                "正在加载");
        return window;
    }

    public static ProgressDialog showProgress(final Context context, String hintText) {
        ProgressDialog window = ProgressDialog.show(context, "", hintText);
        window.getWindow().setGravity(Gravity.CENTER);
        window.setCancelable(false);
        return window;
    }

    /***
     * 获取设备ID
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceId = tm.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            String time = Long.toString((System.currentTimeMillis() / (1000 * 60 * 60)));
            deviceId = time + time;
        }
        return deviceId;
    }


    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param tel 电话号码
     */

    public static void callDialPhone(Context context, String tel) {
        if (TextUtils.isEmpty(tel)) {
            return;
        }
        // 拨打电话
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri data = Uri.parse("tel:" + tel);
            intent.setData(data);
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtils.showShort("没有发现拨号软件");
        }
    }

    public static void callServicePhone(Context context) {
        callDialPhone(context, "");
    }

    public static int getAppVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getAppVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void installApk(Context context, String downloadApk) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(downloadApk);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, "com.yunxiaobao.tms.driver" + "provider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        context.startActivity(intent);

    }

    public static void callBrowser(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setAction("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 复制文本
     */
    public static void copy(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", text);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * 设置屏幕高亮
     * @param activity
     * @param bright 亮度 -1-1 -1f是不设置亮度，最大是1f
     */
    public static void setScreenBrightness(Activity activity, float bright) {
        if (activity == null || activity.isFinishing()) return;
        Window window = activity.getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = bright;
        window.setAttributes(lp);
    }
}
