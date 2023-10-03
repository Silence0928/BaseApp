package com.stas.whms.bean

import java.io.Serializable

class SaveInBoundAuditReqInfo : Serializable{
    var Remark: String? = null // 备注
    var Reason: String? = null // 原因
    var Data: List<GoodsInfo>? = null
}