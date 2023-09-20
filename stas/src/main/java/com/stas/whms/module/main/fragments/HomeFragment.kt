package com.stas.whms.module.main.fragments

import com.lib_common.base.fragment.BaseMvvmFragment
import com.lib_common.base.mvvm.BaseViewModel
import com.stas.whms.R
import com.stas.whms.databinding.FragmentHomeBinding

class HomeFragment: BaseMvvmFragment<FragmentHomeBinding, BaseViewModel>() {
    override fun onViewEvent() {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun getVariableId(): Int {
        return 0
    }
}