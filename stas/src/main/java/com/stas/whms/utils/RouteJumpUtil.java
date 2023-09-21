package com.stas.whms.utils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.stas.whms.constants.RoutePathConfig;

public class RouteJumpUtil {

    /**
     * 登录
     */
    public static void jumpToLogin() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_LOGIN).navigation();
    }
    /**
     * 主页
     */
    public static void jumpToMain() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_MAIN).navigation();
    }
    /**
     * 入库采集
     */
    public static void jumpToStorageCollection() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_STORAGE_COLLECTION).navigation();
    }
}
