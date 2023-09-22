package com.lib_common.net.rxhttp.params;

import java.util.Map;

import okhttp3.RequestBody;
import rxhttp.wrapper.annotation.Param;
import rxhttp.wrapper.param.JsonParam;
import rxhttp.wrapper.param.Method;
@Param(methodName = "postEncryptJson")
public class PostEncryptJsonParam extends JsonParam {
    private String mUrl;
    /**
     * @param url    request url
     */
    public PostEncryptJsonParam(String url) {
        super(url, Method.POST);
        mUrl = url;
    }

    @Override
    public RequestBody getRequestBody() {
        //所有body参数
        Map<String, Object> bodyParam = getBodyParam();

        return super.getRequestBody();
    }
}
