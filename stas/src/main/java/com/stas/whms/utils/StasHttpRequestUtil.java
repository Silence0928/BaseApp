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
}
