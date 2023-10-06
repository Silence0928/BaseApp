package com.stas.whms.bean

import java.io.Serializable

open class ScannerRequestInfo: Serializable {
    var PdaID: String? = null // 请求接口的设备号或者IP
    var TimeStamp: String? = null // 时间戳，精确到毫秒 2023-09-09 12:12:12:1111
    var TextID: String = "1" // 第几个文本框调用，区分扫码场景 默认1
    var QrCode: String? = null // 采集的条码原始信息
    var DocNo: String? = null // 单据号
    var Date: String? = null // 时间戳，精确到毫秒 2023-09-09 12:12:12:1111
    var FromProCode: String? = null // 前工程号
    var TagSerialNo: String? = null // 回转番号
    var PartsNo: String? = null // 电装品番
}