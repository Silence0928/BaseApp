package com.huodada;

import com.huodada.databinding.ActivityMainBinding;
import com.huodada.lib_common.base.BaseDataBindingActivity;
import com.huodada.lib_common.dialog.DateSelectDialog;
import com.huodada.lib_common.router.RouterUtils;

/**
 * 主界面
 */
public class MainActivity extends BaseDataBindingActivity<ActivityMainBinding> {

    @Override
    protected void initView() {
        super.initView();
        setTitle(R.string.app_name);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onViewEvent() {
        mDataBinding.btnDemo.setOnClickListener(view -> {
            RouterUtils.jumpDemo();
        });

        mDataBinding.btnBrowser.setOnClickListener(view -> {
            RouterUtils.jumpWeb("https://jd.com");
        });

        mDataBinding.btnSelectTime.setOnClickListener(view -> {
            new DateSelectDialog(this, (date, v) -> {

            }).setType(2).build().show();
        });
    }
}