package com.stas.whms.module.setting

import com.alibaba.android.arouter.facade.annotation.Route
import com.lib_common.app.BaseApplication
import com.lib_common.base.mvvm.BaseMvvmActivity
import com.lib_common.base.mvvm.BaseViewModel
import com.lib_common.constants.MmkvConstants
import com.stas.whms.R
import com.stas.whms.constants.RoutePathConfig
import com.stas.whms.databinding.ActivitySysSettingBinding

@Route(path = RoutePathConfig.ROUTE_SYS_SETTING)
class SystemSettingActivity: BaseMvvmActivity<ActivitySysSettingBinding, BaseViewModel>() {

    override fun initView() {
        title = "系统设置"
        mDataBinding.swJmcg.isChecked = mMMKV.decodeBool(MmkvConstants.MMKV_OPEN_DECODER_SUCCESS_BEEP, true)
        mDataBinding.swJmsb.isChecked = mMMKV.decodeBool(MmkvConstants.MMKV_OPEN_DECODER_FAILED_BEEP, false)
        mDataBinding.swJmcgzd.isChecked = mMMKV.decodeBool(MmkvConstants.MMKV_OPEN_DECODER_SUCCESS_VIBRATE, false)
        mDataBinding.swSlms.isChecked = mMMKV.decodeBool(MmkvConstants.MMKV_OPEN_EFFORT_SCAN, true)
        mDataBinding.swSmzsd.isChecked = mMMKV.decodeBool(MmkvConstants.MMKV_OPEN_LIGHT_SET, false)
    }

    override fun onViewEvent() {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_sys_setting
    }

    override fun getVariableId(): Int {
        return 0
    }

    override fun onPause() {
        super.onPause()
        mMMKV.encode(MmkvConstants.MMKV_OPEN_DECODER_SUCCESS_BEEP, mDataBinding.swJmcg.isChecked)
        mMMKV.encode(MmkvConstants.MMKV_OPEN_DECODER_FAILED_BEEP, mDataBinding.swJmsb.isChecked)
        mMMKV.encode(MmkvConstants.MMKV_OPEN_DECODER_SUCCESS_VIBRATE, mDataBinding.swJmcgzd.isChecked)
        mMMKV.encode(MmkvConstants.MMKV_OPEN_EFFORT_SCAN, mDataBinding.swSlms.isChecked)
        mMMKV.encode(MmkvConstants.MMKV_OPEN_LIGHT_SET, mDataBinding.swSmzsd.isChecked)
        BaseApplication.getApplication().initScan()
    }
}