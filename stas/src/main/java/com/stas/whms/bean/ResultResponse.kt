package com.stas.whms.bean

import java.io.Serializable

class ResultResponse<T> : Serializable{
    val ErrorCode = 0
    val Reason: String? = null
    val data: List<T>? = null
}