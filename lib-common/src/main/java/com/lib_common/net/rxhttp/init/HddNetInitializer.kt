package com.lib_common.net.rxhttp.init

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.startup.Initializer
import com.lib_common.app.BaseApplication
import com.lib_common.constants.MmkvConstants
import com.lib_common.utils.AndroidUtil
import com.tencent.mmkv.MMKV
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.param.Param
import java.util.concurrent.TimeUnit

/**
 * <P>Created by vincent on 2023/8/15.</P>
 * RxHttp 初始化
 */
class HddNetInitializer : Initializer<Unit> {

  private val TIME_OUT = 30L


  override fun create(context: Context) {
    MMKV.initialize(context)
    /*
         * 设置debug模式，默认为false，设置为true后，发请求，过滤"RxHttp"能看到请求日志
         * 根据不同请求添加不同参数，子线程执行，每次发送请求前都会被回调
         * 如果希望部分请求不回调这里，发请求前调用Param.setAssemblyEnabled(false)即可
         * 设置公共参数，非必须
         */
    RxHttpPlugins.init(getHddOkHttpClient())
            .setDebug(com.lib_common.BuildConfig.DEBUG, true, 2)
            .setOnParamAssembly { p: Param<*> ->
              val method = p.method
              if (method.isGet) { //Get请求
              } else if (method.isPost) { //Post请求
              }
              val mmkv = MMKV.defaultMMKV()
              val token = mmkv.decodeString(MmkvConstants.MMKV_TOKEN, "")
              p.addHeader("Authorization", token)
                      .addHeader("deviceId", Build.BRAND)
                      .addHeader("systemType", "android")
                      .addHeader("systemVersion", Build.VERSION.RELEASE)
                      .addHeader("modelName", Build.MODEL)
                      .addHeader("Connection", "close")
                      .addHeader("versionCode", AndroidUtil.getAppVersionName(BaseApplication.getApplication().applicationContext))
                      .addHeader("auth_channel", "hw") //添加公共请求头
            } //替换 data:"" 为"",防止对象转换失败
            .setResultDecoder { result: String -> result.replace(",\"data\":\"\"", "") }
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()

  private fun getHddOkHttpClient(): OkHttpClient {
    val builder = OkHttpClient.Builder()
    builder.connectTimeout(TIME_OUT, TimeUnit.SECONDS)
    builder.readTimeout(TIME_OUT, TimeUnit.SECONDS)
    builder.writeTimeout(TIME_OUT, TimeUnit.SECONDS)
    //错误重连
    builder.retryOnConnectionFailure(true)
    //新建log拦截器
    val loggingInterceptor = HttpLoggingInterceptor { message: String -> Log.w("rxhttp", "retrofitBack = $message") }
    val level = HttpLoggingInterceptor.Level.BASIC
    loggingInterceptor.setLevel(level)
    builder.addInterceptor(loggingInterceptor) //添加retrofit日志打印

    //添加响应拦截器，用于部分接口响应参数需要解密的场景，统一在此添加
    builder.addInterceptor(Interceptor addInterceptor@{ chain: Interceptor.Chain ->
      val originalResponse = chain.proceed(chain.request())
      try {
        if (originalResponse.isSuccessful) {
          val responseBody = originalResponse.body
                  ?: return@addInterceptor originalResponse
          val url = chain.request().url.toUri().path
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
      originalResponse
    })
    //        builder.addInterceptor(new BaseUrlInterceptor());
//        builder.addInterceptor(new HeaderInterceptor());
    return builder.build()
  }
}