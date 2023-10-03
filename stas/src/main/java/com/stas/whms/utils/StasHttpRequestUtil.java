package com.stas.whms.utils;

import com.lib_common.net.rxhttp.callback.RxHttpCallBack;
import com.lib_common.webservice.SoapClientUtil;
import com.lib_common.webservice.api.WebApi;
import com.lib_common.webservice.api.WebMethodApi;
import com.lib_common.webservice.response.WebServiceResponse;

public class StasHttpRequestUtil {
    /**
     * 登录
     * @param req
     * @return
     */
    public static WebServiceResponse login(String req) {
        return SoapClientUtil.execute(req, WebApi.loginUrl, WebMethodApi.loginMethod);
    }
    /**
     * 扫描结果查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryScannerResult(String req) {
        return SoapClientUtil.execute(req, WebApi.scannerUrl, WebMethodApi.scannerMethod);
    }
    /**
     * 保存入库采集结果
     * @param req
     * @return
     */
    public static WebServiceResponse saveInBound(String req) {
        return SoapClientUtil.execute(req, WebApi.inboundSaveUrl, WebMethodApi.inboundSaveMethod);
    }
    /**
     * 入库审核数据查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryInBoundAuditData(String req) {
        return SoapClientUtil.execute(req, WebApi.inboundAuditDataUrl, WebMethodApi.inboundAuditGetMethod);
    }
    /**
     * 入库审核数据保存
     * @param req
     * @return
     */
    public static WebServiceResponse saveInBoundAuditData(String req) {
        return SoapClientUtil.execute(req, WebApi.inboundAuditSaveUrl, WebMethodApi.inboundAuditSaveMethod);
    }
    /**
     * 退库结果查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryReturnScannerResult(String req) {
        return SoapClientUtil.execute(req, WebApi.returnUrl, WebMethodApi.returnMethod);
    }
    /**
     * 保存退库采集结果
     * @param req
     * @return
     */
    public static WebServiceResponse saveReturn(String req) {
        return SoapClientUtil.execute(req, WebApi.returnSaveUrl, WebMethodApi.returnSaveMethod);
    }
    /**
     * 退库审核数据查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryReturnAuditData(String req) {
        return SoapClientUtil.execute(req, WebApi.returnAuditDataUrl, WebMethodApi.returnAuditGetMethod);
    }
    /**
     * 退库审核数据保存
     * @param req
     * @return
     */
    public static WebServiceResponse saveReturnAuditData(String req) {
        return SoapClientUtil.execute(req, WebApi.returnAuditSaveUrl, WebMethodApi.returnAuditSaveMethod);
    }
    /**
     * 在库数据查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryLibrariesData(String req) {
        return SoapClientUtil.execute(req, WebApi.queryLibraryDataUrl, WebMethodApi.queryLibraryMethod);
    }
}
