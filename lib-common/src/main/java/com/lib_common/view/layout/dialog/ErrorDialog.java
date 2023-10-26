package com.lib_common.view.layout.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;
import com.lib_common.R;
import com.lib_common.view.widget.ClearEditText;
import com.lib_common.view.widget.PasswordEditText;

import java.util.Locale;
import java.util.Objects;

/**
 * 补充身份证号码弹窗
 */
public class ErrorDialog {
    private Context mContext;
    private Dialog mDialog;
    private TextView mTvReason;
    private ClearEditText mWorkNoEt; // 工号
    private PasswordEditText mPwdEt; // 密码
    private ClearEditText mRemarkEt; // 备注
    private Button mRegBtn; // 取消
    private Button mPosBtn; // 确定
    private Display mDisplay;
    private ErrorHandleCallBack mCallBack;

    public ErrorDialog(Context context, ErrorHandleCallBack callBack) {
        this.mContext = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();
        this.mCallBack = callBack;
    }

    public ErrorDialog builder() {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_error, null);
        LinearLayout lLayout_bg = view.findViewById(R.id.lLayout_bg);
        mTvReason = view.findViewById(R.id.tv_reason);
        mRegBtn = view.findViewById(R.id.btn_neg);
        mPosBtn = view.findViewById(R.id.btn_pos);
        mPwdEt = view.findViewById(R.id.et_pwd);
        mWorkNoEt = view.findViewById(R.id.et_work_no);
        mRemarkEt = view.findViewById(R.id.et_remark);
        // 定义Dialog布局和参数
        mDialog = new Dialog(mContext, com.lib_common.R.style.AlertDialogStyle);
        mDialog.setContentView(view);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (mDisplay
                .getWidth() * 0.85), LinearLayout.LayoutParams.WRAP_CONTENT));

        mRegBtn.setOnClickListener(v -> {
            dismissDialog();
            if (mCallBack != null) {
                mCallBack.cancel();
            }
        });
        mPosBtn.setOnClickListener(v -> {
            checkCommitData();
        });
        return this;
    }

    public void show() {
        mDialog.show();
    }

    /**
     * 显示
     *
     * @param reason 错误原因
     */
    public void show(String reason) {
        mTvReason.setText(reason);
        show();
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public void destroyView() {
        if (mDialog != null) {
            dismissDialog();
            mDialog = null;
        }
    }

    public boolean isShowing() {
        if (mDialog != null) {
            return mDialog.isShowing();
        }
        return false;
    }

    public void setWorkNo(String workNo) {
        if (mWorkNoEt != null) {
            mWorkNoEt.setText(workNo);
        }
    }

    private void checkCommitData() {
        String workNo = mWorkNoEt.getText().toString().trim().toUpperCase(Locale.getDefault());
        if (TextUtils.isEmpty(workNo)) {
            ToastUtils.showShort("请输入工号");
            return;
        }
        String pwd = mPwdEt.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showShort("请输入密码");
            return;
        }
        if (mCallBack != null) {
            mCallBack.commitModify(mDialog, workNo, pwd, Objects.requireNonNull(mRemarkEt.getText()).toString());
        }
    }

    public interface ErrorHandleCallBack {

        /**
         * 解锁
         * @param dialog
         * @param workNo
         * @param pwd
         * @param remark
         */
        void commitModify(Dialog dialog, String workNo, String pwd, String remark);

        /**
         * 取消
         */
        void cancel();
    }
}
