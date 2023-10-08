package com.stas.whms.module.main.fragments

import android.text.TextUtils
import android.view.View
import com.alibaba.fastjson.JSON
import com.lib_common.base.BaseActivity
import com.lib_common.base.BaseActivity.isFastClick
import com.lib_common.base.fragment.BaseMvvmFragment
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.constants.MmkvConstants
import com.lib_common.utils.AndroidUtil
import com.lib_common.view.layout.dialog.CommonAlertDialog
import com.lib_common.view.layout.dialog.update.download.UpdateBean
import com.stas.whms.R
import com.stas.whms.bean.LoginInfo
import com.stas.whms.databinding.FragmentMineBinding
import com.stas.whms.utils.RouteJumpUtil

class MineFragment: BaseMvvmFragment<FragmentMineBinding, BaseViewModel>() {

    override fun initView(rootView: View?) {
        initHeadView()
        initVersionView()
    }

    override fun onViewEvent() {
        // 退出登录
        mDataBinding.stvLogout.setOnClickListener {
            logoutConfirm()
        }
        // 系统设置
        mDataBinding.sbSysSetting.setOnClickListener {
            RouteJumpUtil.jumpToSysSetting()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_mine
    }

    override fun getVariableId(): Int {
        return 0
    }

    private fun logoutConfirm() {
        CommonAlertDialog(context).builder().setTitle("提示")
            .setMsg("确认退出当前账号吗？")
            .setNegativeButton("取消", null)
            .setPositiveButton("退出") { logout() }.show()
    }

    private fun logout() {
        mMMKV.encode(MmkvConstants.MMKV_LOGIN_INFO, "")
        RouteJumpUtil.jumpToLogin()
        activity?.finish()
    }

    private fun initHeadView() {
        val loginInfoStr = mMMKV.decodeString(MmkvConstants.MMKV_LOGIN_INFO)
        if (loginInfoStr != null) {
            val loginInfo = JSON.parseObject(loginInfoStr, LoginInfo::class.java)
            if (loginInfo != null) {
                mDataBinding.tvName.text = loginInfo.UserName
                mDataBinding.tvLevel.text = if (loginInfo.Level == "1") "岗位：班长" else "岗位：操作员"
            }
        }
    }
    private fun initVersionView() {
        "版本号：V${AndroidUtil.getAppVersionName(context)}".also { mDataBinding.tvVersion.text = it }
        val updateBean = mMMKV.decodeParcelable(MmkvConstants.MMKV_UPDATE_INFO, UpdateBean::class.java)
        if (updateBean != null && !TextUtils.isEmpty(updateBean.updateVersion)) {
            // 比较新旧版本，若大于等于新版本，不做处理
            val newVersion = updateBean.updateVersion.replace(".", "").toInt()
            val currentVersion = AndroidUtil.getVersionCode(context)
            if (currentVersion < newVersion) {
                mDataBinding.tvVersionStatus.text = "更新版本"
                mDataBinding.tvVersionStatusFlag.visibility = View.VISIBLE
                mDataBinding.clVersionUpdate.setOnClickListener {
                    if (!isFastClick()) {
                        (activity as BaseActivity).showVersionUpdateDialog(updateBean, true)
                    }
                }
            } else {
                mDataBinding.tvVersionStatus.text = "已是最新版本"
                mDataBinding.tvVersionStatusFlag.visibility = View.GONE
            }
        } else {
            mDataBinding.tvVersionStatus.text = "已是最新版本"
            mDataBinding.tvVersionStatusFlag.visibility = View.GONE
        }
    }
}