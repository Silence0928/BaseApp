package com.stas.whms.module.main.fragments

import com.lib_common.base.fragment.BaseMvvmFragment
import com.lib_common.base.mvvm.BaseViewModel
import com.stas.whms.R
import com.stas.whms.databinding.FragmentShiftingParkingBinding
import com.stas.whms.utils.RouteJumpUtil

class ShiftParkFragment: BaseMvvmFragment<FragmentShiftingParkingBinding, BaseViewModel>() {
    override fun onViewEvent() {
        // 移库采集
        mDataBinding.llMoveCollection.setOnClickListener {
            RouteJumpUtil.jumpToMoveCollection()
        }
        // 移库审核
        mDataBinding.llMoveAudit.setOnClickListener {
            RouteJumpUtil.jumpToMoveAudit()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_shifting_parking
    }

    override fun getVariableId(): Int {
        return 0
    }
}