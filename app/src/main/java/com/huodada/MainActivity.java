package com.huodada;

import android.content.Intent;

import androidx.annotation.Nullable;

import com.hjq.permissions.Permission;
import com.hjq.toast.ToastUtils;
import com.huodada.databinding.ActivityMainBinding;
import com.huodada.lib_common.base.BaseDataBindingActivity;
import com.huodada.lib_common.dialog.BottomActionDialog;
import com.huodada.lib_common.dialog.BottomListDialog;
import com.huodada.lib_common.dialog.CommonAlertDialog;
import com.huodada.lib_common.dialog.DateSelectDialog;
import com.huodada.lib_common.router.RouterUtils;
import com.huodada.lib_common.utils.ImagePickerUtils;
import com.huodada.lib_common.utils.PermissionUtil;
import com.zhihu.matisse.Matisse;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 */
public class MainActivity extends BaseDataBindingActivity<ActivityMainBinding> {

    @Override
    protected void initView() {
        super.initView();
        setTitle(R.string.app_name);
        hideActionBarBack();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onViewEvent() {
        //列表
        mDataBinding.btnDemo.setOnClickListener(view -> {
            RouterUtils.jumpDemo();
        });

        //网页
        mDataBinding.btnBrowser.setOnClickListener(view -> {
            RouterUtils.jumpWeb("https://jd.com");
        });

        //选择日期时间
        mDataBinding.btnSelectTime.setOnClickListener(view -> {
            new DateSelectDialog(this, (date, v) -> {

            }).build().show();
        });

        //选择图片
        mDataBinding.btnSelectPic.setOnClickListener(view -> {
            //申请权限
            PermissionUtil.requestPermission(this, new String[]{Permission.CAMERA, Permission.WRITE_EXTERNAL_STORAGE},
                    () -> {
                        ImagePickerUtils.openGallery(this, 100, 10);
                    });
        });

        //对话框
        mDataBinding.btnDialog.setOnClickListener(view -> {
            new CommonAlertDialog(this).setMessage("确定要删除吗？")
                    .setOnConfirmClickListener(v -> {
                        CommonAlertDialog.showDialog(this, "这条数据已经被删除！");
                    }).show();
        });

        //底部选择对话框1
        mDataBinding.btnSelectDialog.setOnClickListener(view -> {
            List<String> items = new ArrayList<>();
            items.add("红色");
            items.add("黄色");
            items.add("绿色");
            items.add("橙色");
            items.add("紫色");
            new BottomListDialog(this).setItems(items)
                    .setOnConfirmSelectListener((position, name) -> {
                        ToastUtils.show(name);
                    })
                    .show();
        });

        //底部选择对话框2
        mDataBinding.btnSelectDialog2.setOnClickListener(view -> {
            List<String> items = new ArrayList<>();
            items.add("拍照");
            items.add("相册");
            new BottomActionDialog(this).setItems(items)
                    .setOnConfirmSelectListener((position, name) -> {
                        ToastUtils.show(name);
                    }).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (data == null) {
                return;
            }
            //图片选择地址
            List<String> list = Matisse.obtainPathResult(data);
            ToastUtils.showLong(list.toString());
        }
    }
}