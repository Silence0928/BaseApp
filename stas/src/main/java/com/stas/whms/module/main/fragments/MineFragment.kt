package com.stas.whms.module.main.fragments

import com.lib_common.base.fragment.BaseMvvmFragment
import com.lib_common.base.mvvm.BaseViewModel
import com.stas.whms.R
import com.stas.whms.databinding.FragmentMineBinding
import com.stas.whms.utils.RouteJumpUtil

class MineFragment: BaseMvvmFragment<FragmentMineBinding, BaseViewModel>() {
    override fun onViewEvent() {
        mDataBinding.stvLogout.setOnClickListener {
            RouteJumpUtil.jumpToLogin()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_mine
    }

    override fun getVariableId(): Int {
        return 0
    }
}