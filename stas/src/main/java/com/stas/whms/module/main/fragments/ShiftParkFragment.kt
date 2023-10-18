package com.stas.whms.module.main.fragments

import com.alibaba.fastjson.JSON
import com.hjq.toast.ToastUtils
import com.lib_common.base.fragment.BaseMvvmFragment
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.constants.MmkvConstants
import com.stas.whms.R
import com.stas.whms.bean.LoginInfo
import com.stas.whms.databinding.FragmentShiftingParkingBinding
import com.stas.whms.utils.RouteJumpUtil

class ShiftParkFragment: BaseMvvmFragment<FragmentShiftingParkingBinding, BaseViewModel>() {
    override fun onViewEvent() {
        val loginInfoStr = mMMKV.decodeString(MmkvConstants.MMKV_LOGIN_INFO)
        var level: String? = null
        if (loginInfoStr != null) {
            val loginInfo = JSON.parseObject(loginInfoStr, LoginInfo::class.java)
            if (loginInfo != null) {
                level = loginInfo.Level
            }
        }
        // 移库采集
        mDataBinding.llMoveCollection.setOnClickListener {
            RouteJumpUtil.jumpToMoveCollection()
        }
        // 移库审核
        mDataBinding.llMoveAudit.setOnClickListener {
            if (level == "1") {
                RouteJumpUtil.jumpToMoveAudit()
            } else {
                ToastUtils.show("您暂无此操作权限，请联系班长处理！")
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_shifting_parking
    }

    override fun getVariableId(): Int {
        return 0
    }
}