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
    /**
     * 入库审核
     */
    public static void jumpToStorageAudit() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_STORAGE_AUDIT).navigation();
    }
    /**
     * 在库查询
     */
    public static void jumpToQueryLibrary() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_QUERY_LIBRARY).navigation();
    }
    /**
     * 在库调整
     */
    public static void jumpToAdjustmentLibrary() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_ADJUSTMENT_LIBRARY).navigation();
    }
    /**
     * 退库采集
     */
    public static void jumpToRefundCollection() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_REFUND_COLLECTION).navigation();
    }
    /**
     * 退库审核
     */
    public static void jumpToRefundAudit() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_REFUND_AUDIT).navigation();
    }
    /**
     * 出货准备
     */
    public static void jumpToShipmentPrepare() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_SHIPMENT_PREPARE).navigation();
    }
    /**
     * 捆包合照
     */
    public static void jumpToBaleGroupPhoto() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_BALE_GROUP_PHOTO).navigation();
    }
    /**
     * 出货
     */
    public static void jumpToShipment() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_SHIPMENT).navigation();
    }
    /**
     * 照和解除
     */
    public static void jumpToLightRelease() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_LIGHT_RELEASE).navigation();
    }
    /**
     * 出货取消
     */
    public static void jumpToShipmentCancel() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_SHIPMENT_CANCEL).navigation();
    }
    /**
     * 移库采集
     */
    public static void jumpToMoveCollection() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_MOVE_COLLECTION).navigation();
    }
    /**
     * 移库审核
     */
    public static void jumpToMoveAudit() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_MOVE_AUDIT).navigation();
    }
    /**
     * 系统设置
     */
    public static void jumpToSysSetting() {
        ARouter.getInstance().build(RoutePathConfig.ROUTE_SYS_SETTING).navigation();
    }
}
