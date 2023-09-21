package com.lib_common.view.layout.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.view.WheelView;
import com.lib_common.R;
import com.lib_common.view.layout.SettingBar;

import java.util.List;

/**
 * 从底部弹出的列表选项对话框
 */
public class BottomDialog extends Dialog {
    private final Display display;
    private final Context context;
    private WheelView wheelView;
    private Button buttonCancel;
    private Button btnSubmit;
    private TextView txt_title;
    private View bg_view;
    private Dialog dialog;
    private boolean showTitle = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    private List<String> list;

    public BottomDialog(@NonNull Context context, List<String> list) {
        super(context);
        this.context = context;
        this.list = list;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public BottomDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_bottom_view, null);

        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        // 获取自定义Dialog布局中的控件
        wheelView = view.findViewById(R.id.wheel_view);
        buttonCancel = view.findViewById(R.id.btnCancel);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        txt_title = view.findViewById(R.id.tvTitle);
        bg_view = view.findViewById(R.id.bg_view);
        wheelView.setAdapter(new ArrayWheelAdapter(list));
        wheelView.setLabel("");
        wheelView.setDividerColor(ContextCompat.getColor(context, com.lib_src.R.color.green_27B57D));
        wheelView.setCurrentItem(0);
        wheelView.setCyclic(false);
        wheelView.setTextSize(16);
        wheelView.setLineSpacingMultiplier(2f);

        buttonCancel.setOnClickListener(v -> dismiss());

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        setCancelable(true);
        return this;
    }

    public void setList(List<String> list) {
        this.list = list;
        wheelView.setAdapter(new ArrayWheelAdapter(list));
    }

    public List<String> getList() {
        return list;
    }

    public BottomDialog setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            txt_title.setText("标题");
        } else {
            txt_title.setText(title);
        }
        return this;
    }


    public BottomDialog setCurrentItem(int currentItem) {
        wheelView.setCurrentItem(currentItem);
        return this;
    }

    public int getCurrentItem() {
        return wheelView.getCurrentItem();
    }

    public int getCurrentItem(String value) {
        if (TextUtils.isEmpty(value)) {
            return wheelView.getCurrentItem();
        }
        if (list == null || list.size() == 0) {
            return wheelView.getCurrentItem();
        }
        for (int i = 0; i < list.size(); i++) {
            if (value.equals(list.get(i))) {
                return i;
            }
        }
        return 0;
    }

    public String getCurrentValue() {
        return list.get(wheelView.getCurrentItem());
    }

    public interface BtnSubmitCallback {
        void getName(String name, int position);
    }

    public BottomDialog setPositiveButton(String text,
                                          BtnSubmitCallback callback) {
        showPosBtn = true;
        if ("".equals(text)) {
            btnSubmit.setText("确定");
        } else {
            btnSubmit.setText(text);
        }
        btnSubmit.setOnClickListener(v -> {
//                listener.onClick();
            callback.getName(list.get(wheelView.getCurrentItem()), wheelView.getCurrentItem());
            dismiss();
        });
        return this;
    }

    public BottomDialog setNegativeButton(String text,
                                          final View.OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            buttonCancel.setText("取消");
        } else {
            buttonCancel.setText(text);
        }
        buttonCancel.setOnClickListener(v -> {
            listener.onClick(v);
            dismiss();
        });
        return this;
    }

    public BottomDialog setBgView(final View.OnClickListener listener) {
        bg_view.setOnClickListener(v -> {
            listener.onClick(v);
            dismiss();
        });
        return this;
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        if (flag) {
            bg_view.setOnClickListener(v -> {
                dismiss();
            });
        }
    }

    private void setLayout() {
        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        } else {
            txt_title.setText("提示");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn) {
            btnSubmit.setText("确定");
            btnSubmit.setVisibility(View.VISIBLE);
            btnSubmit.setBackgroundResource(com.lib_src.R.drawable.alertdialog_single_selector);
            btnSubmit.setOnClickListener(v -> dialog.dismiss());
        }

        if (showPosBtn && showNegBtn) {
            btnSubmit.setVisibility(View.VISIBLE);
            btnSubmit.setBackgroundResource(com.lib_src.R.drawable.alertdialog_right_selector);
            btnSubmit.setVisibility(View.VISIBLE);
            btnSubmit.setBackgroundResource(com.lib_src.R.drawable.alertdialog_left_selector);
        }

        if (showPosBtn && !showNegBtn) {
            btnSubmit.setVisibility(View.VISIBLE);
            btnSubmit.setBackgroundResource(com.lib_src.R.drawable.alertdialog_single_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btnSubmit.setVisibility(View.VISIBLE);
            btnSubmit.setBackgroundResource(com.lib_src.R.drawable.alertdialog_single_selector);
        }
    }

    /**
     * 静态调用方法
     */
    public static void showDialog(Context context, String title, List<String> itemList, BtnSubmitCallback callback) {
        showDialog(context, title, 0, itemList, callback);
    }

    /**
     * 静态调用方法
     * 支持直接传递 settingBar 控件
     */
    public static void showDialog(Context context, SettingBar settingBar, List<String> itemList, BtnSubmitCallback callback) {
        showDialog(context, settingBar.getLeftText().toString(), settingBar.getRightText().toString(), itemList, callback);
    }

    /**
     * 静态调用方法
     */
    public static void showDialog(Context context, String title, String currentText, List<String> itemList, BtnSubmitCallback callback) {
        showDialog(context, title, currentText, itemList, callback, null);
    }

    public static void showDialog(Context context, String title, String currentText, List<String> itemList, BtnSubmitCallback callback, OnDismissListener dismissListener) {
        int index = 0;
        for (int i = 0; i < itemList.size(); i++) {
            if (currentText.equals(itemList.get(i))) {
                index = i;
                break;
            }
        }
        showDialog(context, title, index, itemList, callback, dismissListener);
    }

    public static void showDialog(Context context, String title, int currentItem, List<String> itemList, BtnSubmitCallback callback) {
        showDialog(context, title, currentItem, itemList, callback, null);
    }

    /**
     * 静态调用方法
     *
     * @param context     上下文
     * @param title       标题
     * @param currentItem 当前选中索引
     * @param itemList    选项列表
     * @param callback    回调方法
     */
    public static void showDialog(Context context, String title, int currentItem, List<String> itemList, BtnSubmitCallback callback, OnDismissListener dismissListener) {
        final BottomDialog bottomDialog = new BottomDialog(context, itemList).builder()
                .setTitle(title)
                .setCurrentItem(currentItem)
                .setPositiveButton("确定", callback);
        if (dismissListener != null) {
            bottomDialog.setOnDismissListener(dismissListener);
        }
        bottomDialog.setNegativeButton("取消", v -> bottomDialog.dismiss());
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.setCancelable(true);
        bottomDialog.show();
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
        dialog.setOnDismissListener(listener);
    }

    public void show() {
        setLayout();
        dialog.show();
    }

    @Override
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.dismiss();
    }
}
