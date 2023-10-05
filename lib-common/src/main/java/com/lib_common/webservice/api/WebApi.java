package com.lib_common.webservice.api;

public class WebApi {

    public static String webBaseUrl = "http://tempuri.org/";
//    public static String serviceAddressUrl = "http://192.168.6.180:8084/Interface.asmx";
    public static String serviceAddressUrl = "http://192.168.1.8:8084/Interface.asmx";
    // 登录
    public static final String loginUrl = webBaseUrl + WebMethodApi.loginMethod;
    // 扫描采集
    public static final String scannerUrl = webBaseUrl + WebMethodApi.scannerMethod;
    // 入库采集保存
    public static final String inboundSaveUrl = webBaseUrl + WebMethodApi.inboundSaveMethod;
    // 入库审核数据获取
    public static final String inboundAuditDataUrl = webBaseUrl + WebMethodApi.inboundAuditGetMethod;
    // 入库审核保存
    public static final String inboundAuditSaveUrl = webBaseUrl + WebMethodApi.inboundAuditSaveMethod;
    // 退库采集
    public static final String returnUrl = webBaseUrl + WebMethodApi.returnMethod;
    // 退库采集保存
    public static final String returnSaveUrl = webBaseUrl + WebMethodApi.returnSaveMethod;
    // 退库审核数据获取
    public static final String returnAuditDataUrl = webBaseUrl + WebMethodApi.returnAuditGetMethod;
    // 退库审核保存
    public static final String returnAuditSaveUrl = webBaseUrl + WebMethodApi.returnAuditSaveMethod;
    // 在库数据获取
    public static final String queryLibraryDataUrl = webBaseUrl + WebMethodApi.queryLibraryMethod;
    // 在库数据获取
    public static final String queryAdjustmentLibraryDataUrl = webBaseUrl + WebMethodApi.queryAdjustmentLibraryMethod;
    // 在库数据保存
    public static final String saveAdjustmentLibraryDataUrl = webBaseUrl + WebMethodApi.saveAdjustmentLibraryMethod;
    // 出货准备数据获取
    public static final String queryShipmentPrepareDataUrl = webBaseUrl + WebMethodApi.queryShipmentPrepareMethod;
    // 出货准备数据保存
    public static final String saveShipmentPrepareDataUrl = webBaseUrl + WebMethodApi.saveShipmentPrepareMethod;
    // 移库采集
    public static final String queryMoveDataUrl = webBaseUrl + WebMethodApi.queryMoveDataMethod;
    // 移库采集保存
    public static final String saveMoveUrl = webBaseUrl + WebMethodApi.saveMoveDataMethod;
    // 移库审核采集
    public static final String queryMoveAuditDataUrl = webBaseUrl + WebMethodApi.queryMoveAuditDataMethod;
    // 移库审核采集保存
    public static final String saveMoveAuditUrl = webBaseUrl + WebMethodApi.saveMoveAuditDataMethod;
}
