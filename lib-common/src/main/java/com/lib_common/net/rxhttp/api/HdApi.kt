package com.lib_common.net.rxhttp.api

import rxhttp.wrapper.annotation.DefaultDomain
import rxhttp.wrapper.annotation.Domain

/**
 * <P>Created by vincent on 2023/8/15.</P>
 */
object HdApi {

  /**
   * 默认服务地址
   */
  @JvmField
  @DefaultDomain
  var commonUrl = "https://gateway.yunxiaobao.com"

  /**
   * 文件服务对应服务地址
   */
  @JvmField
  @Domain
  var baseFileServerUrl = "https://file-server.yunxiaobao.com"

}