package com.stas.whms.module.main.fragments

import com.lib_common.base.fragment.BaseMvvmFragment
import com.lib_common.base.mvvm.BaseViewModel
import com.stas.whms.R
import com.stas.whms.databinding.FragmentBaleBinding
import com.stas.whms.utils.RouteJumpUtil

class BaleFragment: BaseMvvmFragment<FragmentBaleBinding, BaseViewModel>() {
    override fun onViewEvent() {
        // 出货准备
        mDataBinding.tvShipmentPrepare.setOnClickListener {
            RouteJumpUtil.jumpToShipmentPrepare()
        }
        // 捆包照合
        mDataBinding.tvBale.setOnClickListener {
            RouteJumpUtil.jumpToBaleGroupPhoto()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_bale
    }

    override fun getVariableId(): Int {
        return 0
    }
}