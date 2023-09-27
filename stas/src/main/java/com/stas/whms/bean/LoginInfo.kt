package com.stas.whms.bean

import java.io.Serializable

class LoginInfo: Serializable {
    var UserID: String? = null // 用户ID
    var UserName: String? = null // 用户名
    var Level: String? = null // 级别 0-操作员  1-班长
}