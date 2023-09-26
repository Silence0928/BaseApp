package com.stas.whms.module.login

import android.annotation.SuppressLint
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.hjq.toast.ToastUtils
import com.lib_common.app.BaseApplication
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.utils.AndroidUtil
import com.lib_common.utils.InputTextHelper
import com.stas.whms.R
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityLoginBinding
import com.stas.whms.utils.StasHttpRequestUtil
import rxhttp.wrapper.utils.LogUtil

@Route(path = RoutePathConfig.ROUTE_LOGIN)
class SystemLoginActivity: BaseMvvmActivity<ActivityLoginBinding, BaseViewModel>() {
    private var clickSystemSetting = false
    /**
     * 判断点击退出程序标识
     */
    private var mBackKeyPressedTimes: Long = 0

    override fun initView() {
        title = "登录"
        mActionBar.leftView.visibility = View.INVISIBLE
        mDataBinding.tvVersion.text = "版本：V${AndroidUtil.getAppVersionName(this)}"
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
                mDataBinding.tvSystemSetting.text = "系统设置"
                mDataBinding.clSysSetting.visibility = View.GONE
            } else {
                clickSystemSetting = true
                mDataBinding.tvSystemSetting.text = "关闭系统设置"
                mDataBinding.clSysSetting.visibility = View.VISIBLE
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
        Thread {
            val result = StasHttpRequestUtil.login(JSON.toJSONString(req), null)
            LogUtil.log(JSON.toJSONString(result))
        }.start()
//        val result = SoapClientUtil.execute("Login", req as Map<String, Any>?)
//        LogUtil.log("login result: $result")
//        RouteJumpUtil.jumpToMain()
//        finish()
//        StasHttpRequestUtil.login(this, JSON.toJSONString(req), object: RxHttpCallBack<String>{
//            override fun onStart() {
//            }
//
//            override fun onError(error: ErrorInfo?) {
//                ToastUtils.show(error?.errorMsg)
//            }
//
//            override fun onFinish() {
//            }
//
//            override fun onSuccess(response: String?) {
//                LogUtil.log("login result: $response")
//            }
//        })
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