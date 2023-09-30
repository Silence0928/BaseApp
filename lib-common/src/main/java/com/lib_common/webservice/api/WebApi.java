package com.lib_common.webservice.api;

public class WebApi {

    public static String webBaseUrl = "http://tempuri.org/";
//    public static String serviceAddressUrl = "http://192.168.6.180:8084/Interface.asmx";
    public static String serviceAddressUrl = "http://192.168.0.105:8084/Interface.asmx";
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
}
