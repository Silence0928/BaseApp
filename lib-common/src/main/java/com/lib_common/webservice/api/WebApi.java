package com.lib_common.webservice.api;

public class WebApi {

    public static final String webBaseUrl = "http://tempuri.org/";
    public static final String serviceAddressUrl = "http://192.168.6.180:8084/Interface.asmx";
    // 登录
    public static final String loginUrl = webBaseUrl + WebMethodApi.loginMethod;
    // 扫描采集
    public static final String scannerUrl = webBaseUrl + WebMethodApi.scannerMethod;
    // 入库采集保存
    public static final String inboundSaveUrl = webBaseUrl + WebMethodApi.inboundSaveMethod;
}
