package com.lib_common.view.layout.dialog.vehicleno;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lib_common.R;


/**
 * Created by zhaohongtao
 * on 2023/9/22
 * <p>
 * 车牌号键盘
 */
public class VehicleNoKeyBoardDialog extends Dialog {

    private ImageView mIvCancel;
    private VechicleNoKeyBoardView kbv;
    private TextView titleTv;

    public VehicleNoKeyBoardDialog(@NonNull Activity context) {
        super(context, R.style.vehicle_no_dialog);
        setOwnerActivity(context);
        setContentView(R.layout.dialog_vehicle_no_key_board);
        setCanceledOnTouchOutside(true);
        mIvCancel = findViewById(R.id.iv_dialog_license_plate_num_cancel);
        kbv = findViewById(R.id.kbv_dialog_license_plate_num);
        titleTv = findViewById(R.id.tv_vehicle_no_dialog_title);
        initClick();
    }

    private void initClick() {
        mIvCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    public void setVehicleNoView(View tv, boolean isShowGua) {
        if (tv != null) {
            kbv.setVehicleNoView(tv, isShowGua, this);
        }
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            titleTv.setText(title);
        }
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        getWindow().setAttributes(layoutParams);
    }

}
