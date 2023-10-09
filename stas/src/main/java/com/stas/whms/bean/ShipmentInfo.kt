package com.stas.whms.bean

import java.io.Serializable

class ShipmentInfo : Serializable {
    var idNum: Int = 0 // 序号
    var SortID: String? = null // 序列号
    var Customer: String? = null // 生产指示书-客户编号
    var TruckNo: String? = null // 车次号
    var PartsNo: String? = null // 电装品番
    var Num: String? = null // 数量
    var ActualNum: String? = null // 已采集的实际数量
    var checked = false // 是否选择
    var del = "删除"
}