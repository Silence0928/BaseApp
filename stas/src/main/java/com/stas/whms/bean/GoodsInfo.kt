package com.stas.whms.bean

import java.io.Serializable

class GoodsInfo : Serializable {
    var idNum: Int = 0 // 序号
    var DocNo: String? = null // 入库单号
    var SortID: String? = null // 序列号
    var BoxSum: String? = null // 总箱数
    var FromProCode: String? = null // 前工程号
    var PartsNo: String? = null // 电装品番
    var SupplierCode: String? = null // 生产结束标签对应的客户编号
    var TagSerialNo: String? = null // 回转番号
    var InBoundTime: String? = null // 入库时间
    var CreateDT: String? = null // 入库时间
    var CreateBy: String? = null // 入库人
    var Status: String? = null // 状态
    var Remark: String? = null // 备注
    var CustemerReceipt: String? = null // 客户受领书
    var CustomLabel: String? = null // 客户看板编号
    var QtySum: String? = null // 总数量
    var checked = false // 是否选中
    var SingleBoxSum: String? = null // 单箱数量
    var Num: String? = null // 数量
    var Qty: String? = null // 箱内数量
    var ActualNum: String? = null // 已采集的实际数量
    var del = "删除"
}