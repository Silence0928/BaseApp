package com.lib_common.view.layout.dialog.vehicleno;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lib_common.R;
import com.lib_common.view.layout.SettingBar;


/**
 * author: zhaohongtao
 * time  :2021/6/1 14:02
 * desc : The constants of memory.
 */
public class VechicleNoKeyBoardView extends LinearLayout {

    private static final int MIN_DELAY_TIME = 200;  // 两次点击间隔不能少于200ms
    private static long lastClickTime;
    private LicensePlateNumKeyBoardView mVehicleNoKeyBoard; // 键盘
    private View vehicleNoView; // 车牌号展示View
    private Dialog vehicleNoDialog; // 车牌号弹窗
    private int plateNumIndex = 0; // 车牌号索引
    private int plateNumIndexMax = 6; // 新能源8位，非新能源7位
    private final String CLEAR = "clear";
    private boolean isShowGua; // 是否显示挂
    private String num1;
    private String num2;
    private String num3;
    private String num4;
    private String num5;
    private String num6;
    private String num7;
    private String num8;

    public VechicleNoKeyBoardView(Context context) {
        this(context, null);
    }

    public VechicleNoKeyBoardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VechicleNoKeyBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VechicleNoKeyBoardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.layout_vehicle_no_key_board_view, this);
        mVehicleNoKeyBoard = findViewById(R.id.lpbv_vehicle_no_keyboard);
        mVehicleNoKeyBoard.setKeyEventListener(key -> {
            if (!TextUtils.isEmpty(key) && !isFastClick()) {
                setView(key);
                showContent();
            }
        });
    }

    private static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    public void setVehicleNoView(View mActv, boolean isShowGua, Dialog vehicleNoDialog) {
        this.vehicleNoView = mActv;
        this.isShowGua = isShowGua;
        this.vehicleNoDialog = vehicleNoDialog;
        mVehicleNoKeyBoard.showGua(isShowGua);
        String vehicleNoStr = "";
        if (vehicleNoView != null) {
            if (vehicleNoView instanceof TextView) {
                vehicleNoStr = ((TextView) vehicleNoView).getText().toString();
            } else if (vehicleNoView instanceof SettingBar) {
                vehicleNoStr = ((SettingBar) vehicleNoView).getRightText().toString();
            }
        }
        if (TextUtils.isEmpty(vehicleNoStr)) {
            num1 = "";
            num2 = "";
            num3 = "";
            num4 = "";
            num5 = "";
            num6 = "";
            num7 = "";
            num8 = "";
            plateNumIndex = 0;
        } else {
            plateNumIndex = vehicleNoStr.length();
            switch (plateNumIndex) {
                case 1:
                    num1 = vehicleNoStr.substring(0, 1);
                    num2 = "";
                    num3 = "";
                    num4 = "";
                    num5 = "";
                    num6 = "";
                    num7 = "";
                    num8 = "";
                    break;
                case 2:
                    num1 = vehicleNoStr.substring(0, 1);
                    num2 = vehicleNoStr.substring(1, 2);
                    num3 = "";
                    num4 = "";
                    num5 = "";
                    num6 = "";
                    num7 = "";
                    num8 = "";
                    break;
                case 3:
                    num1 = vehicleNoStr.substring(0, 1);
                    num2 = vehicleNoStr.substring(1, 2);
                    num3 = vehicleNoStr.substring(2, 3);
                    num4 = "";
                    num5 = "";
                    num6 = "";
                    num7 = "";
                    num8 = "";
                    break;
                case 4:
                    num1 = vehicleNoStr.substring(0, 1);
                    num2 = vehicleNoStr.substring(1, 2);
                    num3 = vehicleNoStr.substring(2, 3);
                    num4 = vehicleNoStr.substring(3, 4);
                    num5 = "";
                    num6 = "";
                    num7 = "";
                    num8 = "";
                    break;
                case 5:
                    num1 = vehicleNoStr.substring(0, 1);
                    num2 = vehicleNoStr.substring(1, 2);
                    num3 = vehicleNoStr.substring(2, 3);
                    num4 = vehicleNoStr.substring(3, 4);
                    num5 = vehicleNoStr.substring(4, 5);
                    num6 = "";
                    num7 = "";
                    num8 = "";
                    break;
                case 6:
                    num1 = vehicleNoStr.substring(0, 1);
                    num2 = vehicleNoStr.substring(1, 2);
                    num3 = vehicleNoStr.substring(2, 3);
                    num4 = vehicleNoStr.substring(3, 4);
                    num5 = vehicleNoStr.substring(4, 5);
                    num6 = vehicleNoStr.substring(5, 6);
                    num7 = "";
                    num8 = "";
                    break;
                case 7:
                    num1 = vehicleNoStr.substring(0, 1);
                    num2 = vehicleNoStr.substring(1, 2);
                    num3 = vehicleNoStr.substring(2, 3);
                    num4 = vehicleNoStr.substring(3, 4);
                    num5 = vehicleNoStr.substring(4, 5);
                    num6 = vehicleNoStr.substring(5, 6);
                    num7 = vehicleNoStr.substring(6, 7);
                    num8 = "";
                    break;
                case 8:
                    num1 = vehicleNoStr.substring(0, 1);
                    num2 = vehicleNoStr.substring(1, 2);
                    num3 = vehicleNoStr.substring(2, 3);
                    num4 = vehicleNoStr.substring(3, 4);
                    num5 = vehicleNoStr.substring(4, 5);
                    num6 = vehicleNoStr.substring(5, 6);
                    num7 = vehicleNoStr.substring(6, 7);
                    num8 = vehicleNoStr.substring(7, 8);
                    break;
            }
        }
        setKeyBoardView(isShowGua);
    }

    /**
     * 根据键盘操作判断当前输入的内容
     *
     * @param key
     */
    private void setView(String key) {
        if (TextUtils.equals("del", key)) {
            --plateNumIndex;
            if (plateNumIndex < 0) {
                plateNumIndex = 0;
            }
            setPlateNum("");
        } else if (TextUtils.equals(CLEAR, key)) {
            plateNumIndex = 0;
            setPlateNum("");
        } else {
//            if (plateNumIndex > 0 && key.matches("[\u4E00-\u9FA5]+") && !"挂".equals(key)) {
//                // 除了第一位后不能有中文(除了“挂”)
//                return;
//            }
            setPlateNum(key);
            ++plateNumIndex;
            if (plateNumIndex > plateNumIndexMax) {
                plateNumIndex = plateNumIndexMax + 1;
            }
        }
        setKeyBoardView(isShowGua);
    }

    /**
     * 设置当前输入的键盘格式
     */
    private void setKeyBoardView(boolean isShowGua) {
        if (plateNumIndex == 0) {
            mVehicleNoKeyBoard.setKeyBoardView(LicensePlateNumKeyBoardView.PROVINCE);
        } else if (plateNumIndex == 1) {
            mVehicleNoKeyBoard.setKeyBoardView(LicensePlateNumKeyBoardView.LETTER);
        } else if (plateNumIndex < 6) {
            mVehicleNoKeyBoard.setKeyBoardView(LicensePlateNumKeyBoardView.LETTER_NUM);
        } else if (isShowGua) {
            mVehicleNoKeyBoard.setKeyBoardView(LicensePlateNumKeyBoardView.LETTER_GUA);
        } else {
            mVehicleNoKeyBoard.setKeyBoardView(LicensePlateNumKeyBoardView.LETTER_NUM);
        }
    }

    /**
     * 计算车牌号
     *
     * @param account
     */
    private void setPlateNum(String account) {
        switch (plateNumIndex) {
            case 0:
                num1 = account;
                num2 = "";
                num3 = "";
                num4 = "";
                num5 = "";
                num6 = "";
                num7 = "";
                num8 = "";
                break;
            case 1:
                num2 = account;
                num3 = "";
                num4 = "";
                num5 = "";
                num6 = "";
                num7 = "";
                num8 = "";
                break;
            case 2:
                num3 = account;
                num4 = "";
                num5 = "";
                num6 = "";
                num7 = "";
                num8 = "";
                break;
            case 3:
                num4 = account;
                num5 = "";
                num6 = "";
                num7 = "";
                num8 = "";
                break;
            case 4:
                num5 = account;
                num6 = "";
                num7 = "";
                num8 = "";
                break;
            case 5:
                num6 = account;
                num7 = "";
                num8 = "";
                break;
            case 6:
                num7 = account;
                num8 = "";
                break;
            case 7:
                num8 = account;
                break;
            case 8:
                break;
        }
    }

    /**
     * 将输入的内容展示到TextView
     */
    private void showContent() {
        StringBuilder vehicleNoSb = new StringBuilder();
        vehicleNoSb.append(num1).append(num2).append(num3).append(num4).append(num5).append(num6).append(num7).append(num8);
        if (vehicleNoView != null) {
            if (vehicleNoView instanceof TextView) {
                ((TextView) vehicleNoView).setText(vehicleNoSb.toString());
            } else if (vehicleNoView instanceof SettingBar) {
                ((SettingBar) vehicleNoView).setRightText(vehicleNoSb.toString());
            }
        }
        if (((isShowGua && vehicleNoSb.toString().trim().length() == 7) || vehicleNoSb.toString().trim().length() == 8) && vehicleNoDialog != null) {
            // 挂车7位关闭键盘，非挂车8位关闭
            vehicleNoDialog.dismiss();
        }
    }

}
