package com.stas.whms.bean

import java.io.Serializable

class GoodsInfo : Serializable {
    var idNum: Int = 0 // 序号
    var SortID: String? = null // 序列号
    var BoxSum: String? = null // 箱内数量
    var FromProCode: String? = null // 前工程号
    var PartsNo: String? = null // 电装品番
    var SupplierCode: String? = null // 生产结束标签对应的客户编号
    var TagSerialNo: String? = null // 回转番号
    var del = "删除"
}