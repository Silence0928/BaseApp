package com.lib_common.app;

import android.content.Context;
import android.os.Handler;
import android.os.IScanListener;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.example.iscandemo.iScanInterface;
import com.hjq.toast.ToastUtils;
import com.lib_common.BuildConfig;
import com.lib_common.entity.ScanResult;
import com.lib_common.utils.ActivityStackManager;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.mmkv.MMKV;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import rxhttp.RxHttpPlugins;

/**
 * Application
 * created by yhw
 * date 2022/11/8
 */
public class BaseApplication extends MultiDexApplication {

    // 设置下拉刷新控件全局的Header构建器
    // static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
//            layout.setPrimaryColorsId(R.color.main_color, R.color.white);//全局设置主题颜色
            return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }
    private static BaseApplication application; // 全局唯一的context
    private static Context mContext;//上下文
    private static Thread mMainThread;//主线程
    private static long mMainThreadId;//主线程id
    private static Looper mMainLooper;//循环队列
    private static Handler mHandler;//主线程Handler
    private final IScanListener mIScanListener = (data, type, decodeTime, keyKnowTime, imagePath) -> {
        ScanResult scanResult = new ScanResult();
        scanResult.setData(data);
        scanResult.setType(type);
        scanResult.setDecodeTime(decodeTime);
        scanResult.setKeyKnowTime(keyKnowTime);
        scanResult.setImagePath(imagePath);
        Log.e("Application===", JSON.toJSONString(scanResult));
//        scanResultCallBack(scanResult);
    };

    @Override
    public void onCreate() {
        super.onCreate();
        initSDK();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        application = this;
        MultiDex.install(this);
    }

    private void initSDK() {
        initARouter();
        initToast();
        initRxHttp();
        init();
        MMKV.initialize(this);
        initScan();
    }

    private void initScan() {
        iScanInterface scanInterface = new iScanInterface(this);
        // 扫描成功是否播放声音
        scanInterface.enablePlayBeep(true);
        // 是否启用扫描按键
        scanInterface.lockScanKey(true);
        /*配置扫描结果输出方式
         * mode  0：焦点输出   （没有焦点的时候会误触发UI）
         *       1：广播输出    action：android.intent.action.SCANRESULT
         *       2：模拟按键输出   （没有焦点的时候会误触发UI）
         *       3：复制到粘贴板
         */
        scanInterface.setOutputMode(1);
        scanInterface.registerScan(mIScanListener);
    }

    private void init() {
        mContext = getApplicationContext();
        mMainThread = Thread.currentThread();
        mMainThreadId = android.os.Process.myTid();
        mHandler = new Handler();
        // Activity 栈管理初始化
        ActivityStackManager.getInstance().init(this);
    }

    /**
     * 初始化 Toast 框架
     */
    private void initToast() {
        ToastUtils.init(this);
        ToastUtils.setGravity(Gravity.CENTER);
    }

    /**
     * 初始化RxHttp
     */
    private void initRxHttp() {
        RxHttpPlugins.init(genericClient())
                .setDebug(BuildConfig.DEBUG)
                .setOnParamAssembly(p -> {
//                    Method method = p.getMethod();
//                    if (method.isGet()) { //Get请求
//
//                    } else if (method.isPost()) { //Post请求
//
//                    }
                    p.addHeader("systemType", "android"); //添加公共请求头
                })
                //替换 data:"" 为"",防止对象转换失败
                .setResultDecoder(result -> result.replace(",\"data\":\"\"", ""));
    }

    private OkHttpClient genericClient() {
        final int timeout = 60;
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(timeout, TimeUnit.SECONDS);
        builder.readTimeout(timeout, TimeUnit.SECONDS);
        builder.writeTimeout(timeout, TimeUnit.SECONDS);
        //错误重连
//        builder.retryOnConnectionFailure(false);
        //新建log拦截器
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message ->
                Log.w("rxhttp", "retrofitBack = " + message));
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BASIC;
        loggingInterceptor.setLevel(level);
        builder.addInterceptor(loggingInterceptor);//添加retrofit日志打印 }
//        builder.addInterceptor(new BaseUrlInterceptor());
//        builder.addInterceptor(new HeaderInterceptor());
        return builder.build();
    }

    /**
     * 初始化ARouter框架
     */
    private void initARouter() {
        // 这两行必须写在init之前，否则这些配置在init过程中将无效
        if (BuildConfig.DEBUG) {
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化
    }

    public static BaseApplication getApplication() {
        return application;
    }
    public static Context getContext() {
        return mContext;
    }
    public static void setContext(Context context) {
        BaseApplication.mContext = context;
    }
    public static long getMainThreadId() {
        return mMainThreadId;
    }
    public static Handler getMainHandler() {
        return mHandler;
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        ActivityStackManager.getInstance().finishAllActivities();
    }


}
