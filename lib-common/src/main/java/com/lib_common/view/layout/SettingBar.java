package com.lib_common.view.layout;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.lib_common.R;
import com.lib_common.utils.UIUtils;
import com.lib_common.view.widget.ClearEditText;
import com.lib_common.view.widget.LimitNumEditText;


/**
 * 设置控件，可选择，可输入
 */
public final class SettingBar extends FrameLayout {
    private final LinearLayout mLinearLayout;
    private final TextView mLeftView;
    private final TextView mRightView;
    private TextView mLeftSubText; //左侧副标题(小号字)
    private TextView mRequiredView;
    private ImageView mTipView;
    private final View mLineView;
    private String mTipText;
    private int mDecimalsNum; //限制输入小数位数
    private int mMaxNum; //限制最大输入的数字
    private boolean isRequired; //是否必填
    private boolean isEditor;

    public SettingBar(Context context) {
        this(context, null);
    }

    public SettingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SettingBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        final TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SettingBar);
        mLinearLayout = new LinearLayout(getContext());
        mLeftView = new TextView(getContext());
        //右侧控件设置
        isEditor = array.getBoolean(R.styleable.SettingBar_bar_editor, false);
        if (isEditor) {
            //输入类型
            int type = array.getInt(R.styleable.SettingBar_bar_inputType, 0);
            switch (type) {
                case 1:
                    mRightView = new ClearEditText(getContext());
                    mRightView.setInputType(InputType.TYPE_CLASS_PHONE);
                    break;
                case 2:
                    mRightView = new ClearEditText(getContext());
                    mRightView.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case 3:
                    mRightView = new LimitNumEditText(getContext());
                    mRightView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    //只有类型为numberDecimal可以设置小数位数
                    if (array.hasValue(R.styleable.SettingBar_bar_edit_decimalsNum)) {
                        mDecimalsNum = array.getInt(R.styleable.SettingBar_bar_edit_decimalsNum, 2); //默认可输入两位小数
                        ((LimitNumEditText) mRightView).setDecimalsNum(mDecimalsNum);
                    }
                    //只有类型为numberDecimal可以设置最大输入数
                    if (array.hasValue(R.styleable.SettingBar_bar_edit_maxNum)) {
                        mMaxNum = array.getInt(R.styleable.SettingBar_bar_edit_maxNum, -1);
                        ((LimitNumEditText) mRightView).setMaxNum(mMaxNum);
                    }
                    break;
                default:
                    mRightView = new ClearEditText(getContext());
                    break;
            }
            //最大长度
            if (array.hasValue(R.styleable.SettingBar_bar_length)) {
                mRightView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(array.getInt(R.styleable.SettingBar_bar_length, 100))});
            }
            //限制字符
            if (array.hasValue(R.styleable.SettingBar_bar_edit_digits)) {
                String digits = array.getString(R.styleable.SettingBar_bar_edit_digits);
                mRightView.setKeyListener(new DigitsKeyListener() {
                    @Override
                    protected char[] getAcceptedChars() {
                        return digits.toCharArray();
                    }

                    @Override
                    public int getInputType() {
                        return InputType.TYPE_CLASS_TEXT;
                    }
                });
            }
            //设置编辑框的padding 避免输入框可点击区域过小
            if (array.hasValue(R.styleable.SettingBar_bar_edit_padding)) {
                int padding = array.getDimensionPixelSize(R.styleable.SettingBar_bar_edit_padding, 0);
                mRightView.setPadding(padding, padding, padding, padding);
            } else {
                int paddingLeft = array.getDimensionPixelSize(R.styleable.SettingBar_bar_edit_paddingLeft, 0);
                int paddingRight = array.getDimensionPixelSize(R.styleable.SettingBar_bar_edit_paddingRight, 0);
                int paddingTop = array.getDimensionPixelSize(R.styleable.SettingBar_bar_edit_paddingTop, 0);
                int paddingBottom = array.getDimensionPixelSize(R.styleable.SettingBar_bar_edit_paddingBottom, 0);
                mRightView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            }
        } else {
            mRightView = new TextView(getContext());

            mRightView.setPaddingRelative((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
        }

        //最大行数
        if (array.hasValue(R.styleable.SettingBar_bar_maxLine) || isEditor) {
            final int maxLine = array.getInt(R.styleable.SettingBar_bar_maxLine, 1);
            mRightView.setMaxLines(maxLine);
            if (maxLine == 1) {
                mRightView.setEllipsize(TextUtils.TruncateAt.END);
            }
        }

        mRightView.setBackground(null);
        mLineView = new View(getContext());
        //左侧小号文本
        if (array.hasValue(R.styleable.SettingBar_bar_leftSubText)) {
            mLeftSubText = new TextView(getContext());
            mLeftSubText.setPadding(0, UIUtils.dip2Px(2), 0, 0);
            mLeftSubText.setText(array.getString(R.styleable.SettingBar_bar_leftSubText));
        }
        //是否为必填
        isRequired = array.getBoolean(R.styleable.SettingBar_bar_required, false);
        mRequiredView = new TextView(getContext());

        //是否有提示控件显示
        mTipText = array.getString(R.styleable.SettingBar_bar_tipText);
        if (!TextUtils.isEmpty(mTipText)) {
            mTipView = new ImageView(getContext());
        }
        //提示按钮图标
        final Drawable tipDrawable = array.getDrawable(R.styleable.SettingBar_bar_tipIcon);

        mLeftView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        mRightView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

        mLeftView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()), mLeftView.getLineSpacingMultiplier());
        mRightView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()), mRightView.getLineSpacingMultiplier());

        mLeftView.setPaddingRelative((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));

        mLeftView.setCompoundDrawablePadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        mRightView.setCompoundDrawablePadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));

        // 文本设置
        if (array.hasValue(R.styleable.SettingBar_bar_leftText)) {
            setLeftText(array.getString(R.styleable.SettingBar_bar_leftText));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_rightText)) {
            setRightText(array.getString(R.styleable.SettingBar_bar_rightText));
        }

        // 提示设置
        if (array.hasValue(R.styleable.SettingBar_bar_leftHint)) {
            setLeftHint(array.getString(R.styleable.SettingBar_bar_leftHint));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_rightHint)) {
            setRightHint(array.getString(R.styleable.SettingBar_bar_rightHint));
        }

        // 图标设置
        if (array.hasValue(R.styleable.SettingBar_bar_leftIcon)) {
            setLeftIcon(array.getDrawable(R.styleable.SettingBar_bar_leftIcon));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_rightIcon)) {
            setRightIcon(array.getDrawable(R.styleable.SettingBar_bar_rightIcon));
        }

        // 文字颜色设置
        setLeftColor(array.getColor(R.styleable.SettingBar_bar_leftColor, ContextCompat.getColor(getContext(), com.lib_src.R.color.black80)));
        setRightColor(array.getColor(R.styleable.SettingBar_bar_rightColor, ContextCompat.getColor(getContext(), com.lib_src.R.color.black60)));

        // 文字大小设置
        setLeftSize(TypedValue.COMPLEX_UNIT_PX, array.getDimensionPixelSize(R.styleable.SettingBar_bar_leftSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics())));
        setRightSize(TypedValue.COMPLEX_UNIT_PX, array.getDimensionPixelSize(R.styleable.SettingBar_bar_rightSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics())));

        // 分割线设置
        if (array.hasValue(R.styleable.SettingBar_bar_lineColor)) {
            setLineDrawable(array.getDrawable(R.styleable.SettingBar_bar_lineColor));
        } else {
            setLineDrawable(new ColorDrawable(0xFFECECEC));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_lineVisible)) {
            setLineVisible(array.getBoolean(R.styleable.SettingBar_bar_lineVisible, true));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_lineSize)) {
            setLineSize(array.getDimensionPixelSize(R.styleable.SettingBar_bar_lineSize, 0));
        }

        if (array.hasValue(R.styleable.SettingBar_bar_lineMargin)) {
            setLineMargin(array.getDimensionPixelSize(R.styleable.SettingBar_bar_lineMargin, 0));
        }

        if (getBackground() == null) {
            if (!isEnabled() || !isClickable()) {
                setBackgroundResource(com.lib_src.R.color.white);
            } else {
                StateListDrawable drawable = new StateListDrawable();
                drawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(ContextCompat.getColor(getContext(), com.lib_src.R.color.black5)));
                drawable.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(ContextCompat.getColor(getContext(), com.lib_src.R.color.black5)));
                drawable.addState(new int[]{android.R.attr.state_focused}, new ColorDrawable(ContextCompat.getColor(getContext(), com.lib_src.R.color.black5)));
                drawable.addState(new int[]{}, new ColorDrawable(ContextCompat.getColor(getContext(), com.lib_src.R.color.white)));
                setBackground(drawable);

                // 必须要设置可点击，否则点击屏幕任何角落都会触发按压事件
                setFocusable(true);
                setClickable(true);
            }
        }

        array.recycle();

        LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        leftParams.gravity = Gravity.CENTER_VERTICAL;
        mLinearLayout.addView(mLeftView, leftParams);
        if (mLeftSubText != null) {
            mLeftSubText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            mLinearLayout.addView(mLeftSubText, leftParams);
        }

        mRequiredView.setText("*");
        mRequiredView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLeftView.getTextSize());
        mRequiredView.setTextColor(getContext().getResources().getColor(com.lib_src.R.color.red01));
        mLinearLayout.addView(mRequiredView, leftParams);
        setRequired(isRequired);

        if (mTipView != null) {
            if (tipDrawable == null) {
                mTipView.setImageResource(com.lib_src.R.drawable.ic_goods_detail_tip);
            } else {
                mTipView.setImageDrawable(tipDrawable);
            }
            leftParams.leftMargin = UIUtils.dip2Px(3);
            mLinearLayout.addView(mTipView, leftParams);
            mTipView.setClickable(true);
            mTipView.setFocusable(true);
            mTipView.setOnClickListener(v -> showTipDialog());
        }

        LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
        rightParams.gravity = Gravity.CENTER_VERTICAL;
        rightParams.weight = 1;
        //文本控件时设置左右控件默认间距
        if (!isEditor) {
            rightParams.leftMargin = UIUtils.dip2Px(15);
        }
        mLinearLayout.addView(mRightView, rightParams);

        addView(mLinearLayout, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
        addView(mLineView, 1, new LayoutParams(LayoutParams.MATCH_PARENT, 1, Gravity.BOTTOM));
    }

    public boolean isEditor() {
        return isEditor;
    }

    /**
     * 设置左边的标题
     */
    public SettingBar setLeftText(@StringRes int id) {
        return setLeftText(getResources().getString(id));
    }

    public SettingBar setLeftText(CharSequence text) {
        mLeftView.setText(text);
        return this;
    }

    public CharSequence getLeftText() {
        return mLeftView.getText();
    }

    /**
     * 设置左边的提示
     */
    public SettingBar setLeftHint(@StringRes int id) {
        return setLeftHint(getResources().getString(id));
    }

    public SettingBar setLeftHint(CharSequence hint) {
        mLeftView.setHint(hint);
        return this;
    }

    /**
     * 设置右边的标题
     */
    public SettingBar setRightText(@StringRes int id) {
        setRightText(getResources().getString(id));
        return this;
    }

    public SettingBar setRightText(CharSequence text) {
        mRightView.setText(text);
        return this;
    }

    /**
     * 设置右侧标题，如果是空则隐藏控件
     */
    public SettingBar setRightTextEmptyHide(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            setVisibility(GONE);
            return this;
        }
        setVisibility(VISIBLE);
        return setRightText(text);
    }


    public CharSequence getRightText() {
        return mRightView.getText();
    }

    /**
     * 设置右边的提示
     */
    public SettingBar setRightHint(@StringRes int id) {
        return setRightHint(getResources().getString(id));
    }

    public SettingBar setRightHint(CharSequence hint) {
        mRightView.setHint(hint);
        return this;
    }

    /**
     * 设置左边的图标
     */
    public SettingBar setLeftIcon(@DrawableRes int id) {
        setLeftIcon(ContextCompat.getDrawable(getContext(), id));
        return this;
    }

    public SettingBar setLeftIcon(Drawable drawable) {
        mLeftView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        return this;
    }

    public Drawable getLeftIcon() {
        return mLeftView.getCompoundDrawables()[0];
    }

    /**
     * 设置右边的图标
     */
    public SettingBar setRightIcon(@DrawableRes int id) {
        setRightIcon(ContextCompat.getDrawable(getContext(), id));
        return this;
    }

    public SettingBar setRightIcon(Drawable drawable) {
        mRightView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        return this;
    }

    public Drawable getRightIcon() {
        return mRightView.getCompoundDrawables()[2];
    }

    /**
     * 设置左标题颜色
     */
    public SettingBar setLeftColor(@ColorInt int color) {
        mLeftView.setTextColor(color);
        if (mLeftSubText != null) {
            mLeftSubText.setTextColor(color);
        }
        return this;
    }

    /**
     * 设置右标题颜色
     */
    public SettingBar setRightColor(@ColorInt int color) {
        mRightView.setTextColor(color);
        return this;
    }

    /**
     * 设置左标题的文本大小
     */
    public SettingBar setLeftSize(int unit, float size) {
        mLeftView.setTextSize(unit, size);
        return this;
    }

    /**
     * 设置右标题的文本大小
     */
    public SettingBar setRightSize(int unit, float size) {
        mRightView.setTextSize(unit, size);
        return this;
    }

    /**
     * 设置分割线是否显示
     */
    public SettingBar setLineVisible(boolean visible) {
        mLineView.setVisibility(visible ? VISIBLE : GONE);
        return this;
    }

    /**
     * 设置分割线的颜色
     */
    public SettingBar setLineColor(@ColorInt int color) {
        return setLineDrawable(new ColorDrawable(color));
    }

    public SettingBar setLineDrawable(Drawable drawable) {
        mLineView.setBackground(drawable);
        return this;
    }

    /**
     * 设置分割线的大小
     */
    public SettingBar setLineSize(int size) {
        ViewGroup.LayoutParams layoutParams = mLineView.getLayoutParams();
        layoutParams.height = size;
        mLineView.setLayoutParams(layoutParams);
        return this;
    }

    /**
     * 设置分割线边界
     */
    public SettingBar setLineMargin(int margin) {
        LayoutParams params = (LayoutParams) mLineView.getLayoutParams();
        params.leftMargin = margin;
        params.rightMargin = margin;
        mLineView.setLayoutParams(params);
        return this;
    }

    public void setTipText(String text) {
        this.mTipText = text;
    }

    public void setLeftSubText(String text) {
        mLeftSubText.setText(text);
    }

    /**
     * 显示提示弹框
     */
    private void showTipDialog() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_tip_layout, null);
        final TextView msgView = view.findViewById(R.id.tv_msg);
        final Button posBtn = view.findViewById(R.id.btn_pos);
        msgView.setText(mTipText);
        final Dialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                .setView(view)
                .create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        posBtn.setOnClickListener(v -> dialog.dismiss());
    }

    public void showTip() {
        if (mTipView != null) {
            mTipView.setVisibility(VISIBLE);
        }
    }

    public void hideTip() {
        if (mTipView != null) {
            mTipView.setVisibility(GONE);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mRightView.setHint(null);
        mRightView.setEnabled(false);
        setClickable(false);
        setFocusable(false);
    }

    /**
     * 获取主布局
     */
    public LinearLayout getMainLayout() {
        return mLinearLayout;
    }

    /**
     * 获取左标题
     */
    public TextView getLeftView() {
        return mLeftView;
    }

    /**
     * 获取右标题
     */
    public TextView getRightView() {
        return mRightView;
    }

    /**
     * 获取分割线
     */
    public View getLineView() {
        return mLineView;
    }

    /**
     * 获取必填控件
     */
    public TextView getRequiredView() {
        return mRequiredView;
    }

    /**
     * 获取提示控件
     */
    public ImageView getTipView() {
        return mTipView;
    }

    public void setRequired(boolean required) {
        isRequired = required;
        if (isRequired) {
            mRequiredView.setVisibility(View.VISIBLE);
        } else {
            mRequiredView.setVisibility(View.GONE);
        }
    }
}