package com.lib_common.webservice;

import com.alibaba.fastjson.JSON;
import com.lib_common.webservice.api.WebApi;
import com.lib_common.webservice.response.WebServiceResponse;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class SoapClientUtil {
    public static WebServiceResponse execute(String req, String url, String methodName) {
        WebServiceResponse response = new WebServiceResponse();
        SoapObject soapObject = new SoapObject(WebApi.webBaseUrl, methodName);
        soapObject.addProperty("param", req);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = soapObject;
        envelope.dotNet = true;  // 这里如果设置为TRUE,那么在服务器端将获取不到参数值(如:将这些数据插入到数据库中的话)
        envelope.setOutputSoapObject(soapObject);
        HttpTransportSE httpTransportSE = new HttpTransportSE(WebApi.serviceAddressUrl, 60000);
        try {
            httpTransportSE.call(url, envelope);
            System.out.println("调用WebService服务成功");
            //获得服务返回的数据,并且开始解析
            if (envelope.bodyIn instanceof SoapObject) {
                SoapObject object = (SoapObject) envelope.bodyIn;//System.out.println("获得服务数据");
                String obj = object.getProperty(0).toString();
                response = JSON.parseObject(obj, WebServiceResponse.class);
//                if (obj instanceof WebServiceResponse) {
//                    response = (WebServiceResponse) obj;
//                } else {
//                    response.setErrorCode(-1);
//                    response.setReason("调用WebService服务失败");
//                }
            } else if (envelope.bodyIn instanceof SoapFault) {
                SoapFault objectFault = (SoapFault) envelope.bodyIn;
                response.setErrorCode(-1);
                response.setReason(objectFault.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setErrorCode(-1);
            response.setReason("调用WebService服务失败");
        }
        return response;
    }
}
