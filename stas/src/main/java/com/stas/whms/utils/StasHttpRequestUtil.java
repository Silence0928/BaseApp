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
        return SoapClientUtil.execute(req, WebMethodApi.loginMethod);
    }
    /**
     * 扫描结果查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryScannerResult(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.scannerMethod);
    }
    /**
     * 保存入库采集结果
     * @param req
     * @return
     */
    public static WebServiceResponse saveInBound(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.inboundSaveMethod);
    }
    /**
     * 入库审核数据查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryInBoundAuditData(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.inboundAuditGetMethod);
    }
    /**
     * 入库审核数据保存
     * @param req
     * @return
     */
    public static WebServiceResponse saveInBoundAuditData(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.inboundAuditSaveMethod);
    }
    /**
     * 退库结果查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryReturnScannerResult(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.returnMethod);
    }
    /**
     * 保存退库采集结果
     * @param req
     * @return
     */
    public static WebServiceResponse saveReturn(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.returnSaveMethod);
    }
    /**
     * 退库审核数据查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryReturnAuditData(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.returnAuditGetMethod);
    }
    /**
     * 退库审核数据保存
     * @param req
     * @return
     */
    public static WebServiceResponse saveReturnAuditData(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.returnAuditSaveMethod);
    }
    /**
     * 在库数据查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryLibrariesData(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.queryLibraryMethod);
    }
    /**
     * 在库调整查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryAdjustmentLibrariesData(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.queryAdjustmentLibraryMethod);
    }
    /**
     * 在库调整保存
     * @param req
     * @return
     */
    public static WebServiceResponse saveAdjustmentLibrariesData(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.saveAdjustmentLibraryMethod);
    }
    /**
     * 出货准备查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryShipmentPrepareResult(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.queryShipmentPrepareMethod);
    }
    /**
     * 出货准备保存
     * @param req
     * @return
     */
    public static WebServiceResponse saveShipmentPrepareData(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.saveShipmentPrepareMethod);
    }
    /**
     * 移库结果查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryMoveDataResult(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.queryMoveDataMethod);
    }
    /**
     * 保存移库采集结果
     * @param req
     * @return
     */
    public static WebServiceResponse saveMoveData(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.saveMoveDataMethod);
    }
    /**
     * 移库审核结果查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryMoveAuditDataResult(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.queryMoveAuditDataMethod);
    }
    /**
     * 保存移库审核采集结果
     * @param req
     * @return
     */
    public static WebServiceResponse saveMoveAuditData(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.saveMoveAuditDataMethod);
    }
    /**
     * 捆包照合结果查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryBaleDataResult(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.queryBaleDataMethod);
    }
    /**
     * 保存捆包照合采集结果
     * @param req
     * @return
     */
    public static WebServiceResponse saveBaleData(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.saveBaleDataMethod);
    }
    /**
     * 出货采集
     * @param req
     * @return
     */
    public static WebServiceResponse queryShipmentDataResult(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.queryShipmentDataMethod);
    }
    /**
     * 保存出货采集结果
     * @param req
     * @return
     */
    public static WebServiceResponse saveShipmentData(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.saveShipmentDataMethod);
    }
    /**
     * 照合解除查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryLightReleaseDataResult(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.queryLightReleaseDataMethod);
    }
    /**
     * 保存照合解除结果
     * @param req
     * @return
     */
    public static WebServiceResponse saveLightReleaseData(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.saveLightReleaseDataMethod);
    }
    /**
     * 出货取消查询
     * @param req
     * @return
     */
    public static WebServiceResponse queryShipmentCancelDataResult(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.queryShipmentCancelDataMethod);
    }
    /**
     * 保存出货取消结果
     * @param req
     * @return
     */
    public static WebServiceResponse saveShipmentCancelData(String req) {
        return SoapClientUtil.execute(req, WebMethodApi.saveShipmentCancelDataMethod);
    }
}
