package com.lib_common.entity

import java.io.Serializable

/**
 * 扫描结果
 */
class ScanResult: Serializable {
    var data: String? = null // 扫描结果
    var type: Int? = null // 条码类型
    var decodeTime: Long? = null // 扫描时间
    var keyKnowTime: Long? = null // 按键按下时间
    var imagePath: String? = null // 图片存储路径-用于OCR识别需求，需开启保存图片才有效
}