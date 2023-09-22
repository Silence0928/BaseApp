package com.lib_common.net.rxhttp.parser

import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.Response
import java.io.IOException

class NetParseException: IOException {

    var errorCode: String? = null
    var requestMethod: String? = null //请求方法，Get/Post等
    var httpUrl: HttpUrl? = null //请求Url及查询参数
    var responseHeaders: Headers? = null //响应头
    var mData: String? = null // 返回data结果

    constructor(code: String, message: String?, response: Response, data: String?): super(message) {
//        val types = TypeUtil.getActualTypeParameters(ResponseNet::class.java)
//        val data: ResponseNet<String> = response.convertTo(ResponseNet::class, *types)
        mData = data //获取data字段
        errorCode = code
        val request = response.request
        requestMethod = request.method
        httpUrl = request.url
        responseHeaders = response.headers
    }

    fun getRequestUrl(): String? {
        return httpUrl.toString()
    }

    fun getLocalizedMessage(): String? {
        return errorCode
    }

    override fun toString(): String {
        return javaClass.name + ":" +
                "\n" + requestMethod + " " + httpUrl +
                "\n\nCode=" + errorCode + " message=" + message +
                "\n" + responseHeaders
    }
}