package com.stas.whms.module.login

import android.annotation.SuppressLint
import android.text.Editable
import android.view.View
import android.webkit.JsResult
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.hjq.toast.ToastUtils
import com.lib_common.app.BaseApplication
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.constants.MmkvConstants
import com.lib_common.listener.SimpleTextWatcher
import com.lib_common.utils.AndroidUtil
import com.lib_common.utils.InputTextHelper
import com.lib_common.webservice.api.WebApi
import com.stas.whms.R
import com.stas.whms.bean.LoginInfo
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityLoginBinding
import com.stas.whms.utils.RouteJumpUtil
import com.stas.whms.utils.StasHttpRequestUtil
import rxhttp.wrapper.utils.LogUtil
import java.lang.Exception

@Route(path = RoutePathConfig.ROUTE_LOGIN)
class SystemLoginActivity : BaseMvvmActivity<ActivityLoginBinding, BaseViewModel>() {
    private var clickSystemSetting = false

    /**
     * 判断点击退出程序标识
     */
    private var mBackKeyPressedTimes: Long = 0

    override fun initView() {
        title = "登录"
        mActionBar.leftView.visibility = View.INVISIBLE
        mDataBinding.tvVersion.text = "版本：V${AndroidUtil.getAppVersionName(this)}"
        mDataBinding.etUrl.setText(WebApi.serviceAddressUrl)
        mDataBinding.etUrlNs.setText(WebApi.webBaseUrl)
    }

    override fun onViewEvent() {
        // 登录按钮控制
        InputTextHelper.with(this)
            .addView(mDataBinding.etJobNum)
            .addView(mDataBinding.etPwd)
            .setMain(mDataBinding.tvLogin)
            .setListener(object : InputTextHelper.OnInputTextListener {
                override fun onInputChange(helper: InputTextHelper): Boolean {
                    return mDataBinding.etJobNum.text.toString().trim().isNotEmpty()
                            && mDataBinding.etPwd.text.toString().trim().isNotEmpty()
                }

                override fun setTextColor() {
                    mDataBinding.tvLogin.alpha = 1F
                }

                override fun setUnTextColor() {
                    mDataBinding.tvLogin.alpha = 0.5F
                }
            })
            .build()

        // 系统设置
        mDataBinding.tvSystemSetting.setOnClickListener {
            if (clickSystemSetting) {
                clickSystemSetting = false
                mDataBinding.tvSystemSetting.text = "环境设置"
                mDataBinding.clSysSetting.visibility = View.GONE
            } else {
                clickSystemSetting = true
                mDataBinding.tvSystemSetting.text = "关闭环境设置"
                mDataBinding.clSysSetting.visibility = View.VISIBLE
            }
        }
        // 域名
        mDataBinding.etUrlNs.setOnFocusChangeListener{ v, b ->
            if (!b) {
                WebApi.webBaseUrl = mDataBinding.etUrlNs.text.toString()
                mMMKV.encode(MmkvConstants.MMKV_URL_NS, WebApi.webBaseUrl)
            }
        }
        // 代码地址
        mDataBinding.etUrl.setOnFocusChangeListener{ v, b ->
            if (!b) {
                WebApi.serviceAddressUrl = mDataBinding.etUrl.text.toString()
                mMMKV.encode(MmkvConstants.MMKV_SERVICE_URL, WebApi.serviceAddressUrl)
            }
        }

        // 登录
        mDataBinding.tvLogin.setOnClickListener {
            val jobNum = mDataBinding.etJobNum.text.toString().trim()
            val password = mDataBinding.etPwd.text.toString().trim()
            if (jobNum.isEmpty()) {
                ToastUtils.show("请输入工号")
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                ToastUtils.show("请输入密码")
                return@setOnClickListener
            }
            login(jobNum, password)
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun getVariableId(): Int {
        return 0
    }

    private fun login(jobNum: String, pwd: String) {
        val req = HashMap<String, String>()
        req["UserID"] = jobNum
        req["Password"] = pwd
        req["PdaID"] = AndroidUtil.getIpAddress()
        showLoading()
        Thread {
            try {
                val result = StasHttpRequestUtil.login(JSON.toJSONString(req))
                runOnUiThread {
                    dismissLoading()
                    if (result.errorCode == 200) {
                        val loginInfo =
                            JSON.parseObject(result.obj.toString(), LoginInfo::class.java)
                        mMMKV.encode(MmkvConstants.MMKV_LOGIN_INFO, JSON.toJSONString(loginInfo))
                        RouteJumpUtil.jumpToMain()
                    } else {
                        ToastUtils.show(result.reason)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dismissLoading()
            }
        }.start()
    }


    @SuppressLint("UnsafeOptInUsageError")
    override fun onBackPressed() {
//        if (BuildCompat.isAtLeastT()) {
//            onBackInvokedDispatcher.registerOnBackInvokedCallback(
//                OnBackInvokedDispatcher.PRIORITY_DEFAULT
//            ) {
//                // Back is pressed... Finishing the activity
//                if (System.currentTimeMillis() - mBackKeyPressedTimes > 2000) {
//                    mBackKeyPressedTimes = System.currentTimeMillis()
//                    ToastUtils.showShort(
//                        resources.getString(com.lib_src.R.string.home_exit_app)
//                    )
//                    return@registerOnBackInvokedCallback
//                }
//                finish()
//            }
//        } else {
//            onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    if (System.currentTimeMillis() - mBackKeyPressedTimes > 2000) {
//                        mBackKeyPressedTimes = System.currentTimeMillis()
//                        ToastUtils.showShort(
//                            resources.getString(com.lib_src.R.string.home_exit_app)
//                        )
//                        return
//                    }
//                    finish()
//                }
//            })
//        }
        if (System.currentTimeMillis() - mBackKeyPressedTimes > 2000) {
            mBackKeyPressedTimes = System.currentTimeMillis()
            ToastUtils.showShort(
                resources.getString(com.lib_src.R.string.home_exit_app)
            )
            return
        }
        BaseApplication.getApplication().exitApp()
    }

}