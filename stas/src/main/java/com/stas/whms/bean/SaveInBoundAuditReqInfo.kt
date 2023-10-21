package com.stas.whms.bean

import java.io.Serializable

class SaveInBoundAuditReqInfo : Serializable{
    var Remark: String? = null // 备注
    var ReasonID: String? = null // 原因
    var ListData: List<GoodsInfo>? = null
    var PdaID: String? = null // 设备ID
    var CreateBy: String? = null // 操作人
    var TimeStamp: String? = null // 时间戳
}