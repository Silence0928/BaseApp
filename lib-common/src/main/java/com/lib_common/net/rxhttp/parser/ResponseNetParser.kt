package com.lib_common.net.rxhttp.parser

import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.lib_common.net.rxhttp.response.PagerListBean
import com.lib_common.net.rxhttp.response.ResponseNet
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.parse.TypeParser
import rxhttp.wrapper.utils.convertTo
import java.io.IOException
import java.lang.reflect.Type

@Parser(name = "ResponseNet", wrappers = [PagerListBean::class])
open class ResponseNetParser<T> : TypeParser<T> {
    //注意，以下两个构造方法是必须的
    protected constructor() : super()
    constructor(type: Type) : super(type)

    @Throws(IOException::class)
    override fun onParse(response: okhttp3.Response): T {
        val data: ResponseNet<T> = response.convertTo(ResponseNet::class, *types)
        var t = data.dataTableSubset //获取data字段
        if (data.errorCode == 200 || data.errorCode == 1000) {
            if (t == null && types != null && types.isNotEmpty() && types[0] == String::class.java) {
                t = "" as T
            }
            return t
        }
        if (data.errorCode == 401) {
            // 认证失败
//            Utils.loginAgain()
            if (t == null && types != null && types.isNotEmpty() && types[0] == String::class.java) {
                t = "" as T
            }
            return t
        }
        throw NetParseException(
            data.errorCode.toString(),
            getErrorMessage(data.reason, data.message, data.error),
            response,
            if (t == null) "" else if (types != null && types.isNotEmpty() && types[0] == String::class.java) t as String else JSON.toJSONString(
                t
            )
        )
    }

    private fun getErrorMessage(msg: String?, message: String?, error: String?): String {
        var resultMsg = msg
        if (TextUtils.isEmpty(resultMsg)) {
            resultMsg = if (TextUtils.isEmpty(message)) {
                if (TextUtils.isEmpty(error)) {
                    "数据解析异常，请稍候重试"
                } else {
                    error!!
                }
            } else {
                message!!
            }
        }
        return resultMsg!!
    }
}
