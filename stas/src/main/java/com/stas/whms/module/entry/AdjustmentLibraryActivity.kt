package com.stas.whms.module.entry

import com.alibaba.android.arouter.facade.annotation.Route
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.stas.whms.R
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivityAdjustmentLibraryBinding

@Route(path = RoutePathConfig.ROUTE_ADJUSTMENT_LIBRARY)
class AdjustmentLibraryActivity : BaseMvvmActivity<ActivityAdjustmentLibraryBinding, BaseViewModel>() {

    override fun initView() {
        title = "在库调整"
    }

    override fun onViewEvent() {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_adjustment_library
    }

    override fun getVariableId(): Int {
        return 0
    }

}