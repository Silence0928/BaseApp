package com.stas.whms.bean

import java.io.Serializable

class InBoundAuditRequestInfo: ScannerRequestInfo() {
    var DocNo: String? = null // 请求接口的设备号或者IP
    var Date: String? = null // 时间戳，精确到毫秒 2023-09-09 12:12:12:1111
}