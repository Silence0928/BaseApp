package com.stas.whms.module.login

import android.view.View
import com.hjq.toast.ToastUtils
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.utils.InputTextHelper
import com.stas.whms.R
import com.stas.whms.databinding.ActivityLoginBinding

class SystemLoginActivity: BaseMvvmActivity<ActivityLoginBinding, BaseViewModel>() {
    private var clickSystemSetting = false


    override fun initView() {
        title = "登录"
        mActionBar.leftView.visibility = View.INVISIBLE
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

    }
}