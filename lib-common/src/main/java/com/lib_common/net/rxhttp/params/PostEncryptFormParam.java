package com.lib_common.net.rxhttp.params;

import java.util.List;

import okhttp3.RequestBody;
import rxhttp.wrapper.annotation.Param;
import rxhttp.wrapper.entity.KeyValuePair;
import rxhttp.wrapper.param.FormParam;
import rxhttp.wrapper.param.Method;

@Param(methodName = "postEncryptForm")
public class PostEncryptFormParam extends FormParam {
    private String mUrl;
    /**
     * @param url    request url
     */
    public PostEncryptFormParam(String url) {
        super(url, Method.POST);
        mUrl = url;
    }

    @Override
    public RequestBody getRequestBody() {
        //所有body参数
        List<KeyValuePair> bodyParam = getBodyParam();
        return super.getRequestBody();
    }
}
