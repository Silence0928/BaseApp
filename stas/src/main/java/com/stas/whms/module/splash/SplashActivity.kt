package com.stas.whms.module.splash

import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.constants.MmkvConstants
import com.lib_common.utils.AndroidUtil
import com.stas.whms.R
import com.stas.whms.databinding.ActivitySplashBinding
import com.stas.whms.utils.RouteJumpUtil

class SplashActivity: BaseMvvmActivity<ActivitySplashBinding, BaseViewModel>() {

    override fun initView() {
        mDataBinding.clSplashParent.setBackgroundResource(com.lib_src.R.drawable.bg_splash)
        mDataBinding.tvVersion.text = AndroidUtil.getAppVersionName(this)
        if (TextUtils.isEmpty(mMMKV.decodeString(MmkvConstants.MMKV_LOGIN_INFO))) {
            RouteJumpUtil.jumpToLogin()
        } else {
            RouteJumpUtil.jumpToMain()
        }
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
}