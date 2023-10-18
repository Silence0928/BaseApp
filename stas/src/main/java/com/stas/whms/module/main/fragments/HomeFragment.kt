package com.stas.whms.module.main.fragments

import com.alibaba.fastjson.JSON
import com.hjq.toast.ToastUtils
import com.lib_common.base.fragment.BaseMvvmFragment
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.constants.MmkvConstants
import com.stas.whms.R
import com.stas.whms.bean.LoginInfo
import com.stas.whms.databinding.FragmentHomeBinding
import com.stas.whms.utils.RouteJumpUtil

class HomeFragment: BaseMvvmFragment<FragmentHomeBinding, BaseViewModel>() {
    override fun onViewEvent() {
        val loginInfoStr = mMMKV.decodeString(MmkvConstants.MMKV_LOGIN_INFO)
        var level: String? = null
        if (loginInfoStr != null) {
            val loginInfo = JSON.parseObject(loginInfoStr, LoginInfo::class.java)
            if (loginInfo != null) {
                level = loginInfo.Level
            }
        }
        // 入库采集
        mDataBinding.tvStorageCollection.setOnClickListener {
            RouteJumpUtil.jumpToStorageCollection()
        }
        // 入库审核
        mDataBinding.tvStorageAudit.setOnClickListener {
            if (level == "1") {
                RouteJumpUtil.jumpToStorageAudit()
            } else {
                ToastUtils.show("您暂无此操作权限，请联系班长处理！")
            }
        }
        // 在库查询
        mDataBinding.tvStockQuery.setOnClickListener {
            RouteJumpUtil.jumpToQueryLibrary()
        }
        // 在库调整
        mDataBinding.tvStockModify.setOnClickListener {
            RouteJumpUtil.jumpToAdjustmentLibrary()
        }
        // 退库采集
        mDataBinding.tvRefundStock.setOnClickListener {
            RouteJumpUtil.jumpToRefundCollection()
        }
        // 退库审核
        mDataBinding.tvRefundStockAudit.setOnClickListener {
            if (level == "1") {
                RouteJumpUtil.jumpToRefundAudit()
            } else {
                ToastUtils.show("您暂无此操作权限，请联系班长处理！")
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun getVariableId(): Int {
        return 0
    }
}