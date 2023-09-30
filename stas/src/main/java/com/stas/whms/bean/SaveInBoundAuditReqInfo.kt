package com.stas.whms.bean

import java.io.Serializable

class SaveInBoundAuditReqInfo : Serializable{
    var Remark: String? = null
    var Data: List<GoodsInfo>? = null
}