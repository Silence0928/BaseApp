package com.stas.whms.bean

import java.io.Serializable

class SaveInBoundAuditReqInfo : Serializable{
    var Remark: String? = null // 备注
    var ReasonID: String? = null // 原因
    var ListData: List<GoodsInfo>? = null
}