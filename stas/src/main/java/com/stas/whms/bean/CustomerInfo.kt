package com.stas.whms.bean

import java.io.Serializable

class CustomerInfo : Serializable {
    var idNum: Int = 0 // 序号
    var SortID: String? = null // 序列号
    var CustemerReceipt: String? = null // 客户受领书
    var CustomLabel: String? = null // 客户看板编号
    var QtySum: String? = null // 总数量
    var BoxSum: String? = null // 总箱数
    var del = "删除"
}