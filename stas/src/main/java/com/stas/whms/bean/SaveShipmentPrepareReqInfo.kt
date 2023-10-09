package com.stas.whms.bean

import java.io.Serializable

class SaveShipmentPrepareReqInfo : Serializable{
    var Remark: String? = null // 备注
    var CustemerReceipt: String? = null // 客户受领书
    var OutPlanList: List<ShipmentInfo>? = null // 出货指示书
    var CustomLabelList: List<GoodsInfo>? = null // 客户看板编号列表
    var ProductEnd: GoodsInfo? = null // 制造完了标签实体类
    var ProductEndList: List<GoodsInfo>? = null // 制造完了标签数组
    var TruckNo: String? = null // 车牌号
}