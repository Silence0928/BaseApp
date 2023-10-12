package com.stas.whms.module.splash

import android.text.TextUtils
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.constants.MmkvConstants
import com.lib_common.utils.AndroidUtil
import com.lib_common.webservice.api.WebApi
import com.stas.whms.R
import com.stas.whms.databinding.ActivitySplashBinding
import com.stas.whms.utils.RouteJumpUtil
import com.tencent.bugly.crashreport.CrashReport

class SplashActivity: BaseMvvmActivity<ActivitySplashBinding, BaseViewModel>() {

    override fun initView() {
        initBugly()
        mDataBinding.clSplashParent.setBackgroundResource(com.lib_src.R.drawable.bg_splash)
        mDataBinding.tvVersion.text = AndroidUtil.getAppVersionName(this)
        WebApi.serviceAddressUrl = mMMKV.decodeString(MmkvConstants.MMKV_SERVICE_URL, WebApi.serviceAddressUrl)
        WebApi.webBaseUrl = mMMKV.decodeString(MmkvConstants.MMKV_URL_NS, WebApi.webBaseUrl)
        mMMKV.encode(MmkvConstants.MMKV_LOGIN_INFO, "")
//        if (TextUtils.isEmpty(mMMKV.decodeString(MmkvConstants.MMKV_LOGIN_INFO))) {
            RouteJumpUtil.jumpToLogin()
//        } else {
//            RouteJumpUtil.jumpToMain()
//        }
        finish()
    }

    override fun onViewEvent() {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun getVariableId(): Int {
        return 0
    }

    /**
     * 初始化bugly
     */
    private fun initBugly() {
        CrashReport.initCrashReport(application, "59ad190f34", false)
    }
}