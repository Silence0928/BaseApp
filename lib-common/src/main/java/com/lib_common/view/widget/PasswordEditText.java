package com.lib_common.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.lib_common.R;


/**
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/08/25
 *    desc   : 密码隐藏显示 EditText
 */
public final class PasswordEditText extends RegexEditText
        implements View.OnTouchListener,
        View.OnFocusChangeListener, TextWatcher {

    private int TYPE_VISIBLE = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
    private int TYPE_INVISIBLE = InputType.TYPE_TEXT_VARIATION_PASSWORD;

    private Drawable mCurrentDrawable;
    private Drawable drawable;
    private final Drawable mVisibleDrawable;
    private final Drawable mInvisibleDrawable;

    private OnTouchListener mOnTouchListener;
    private OnFocusChangeListener mOnFocusChangeListener;

    public PasswordEditText(Context context) {
        this(context, null);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    @SuppressWarnings("all")
    @SuppressLint("ClickableViewAccessibility")
    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //自定义属性获取
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordEditText);
        boolean isShowPwd = typedArray.getBoolean(R.styleable.PasswordEditText_isShowPwd, false);
        drawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, com.lib_src.R.drawable.popup_icon_close));
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        // Wrap the drawable so that it can be tinted pre Lollipop
        mVisibleDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, com.lib_src.R.drawable.icon_password_gone));
        mVisibleDrawable.setBounds(0, 0, mVisibleDrawable.getIntrinsicWidth(), mVisibleDrawable.getIntrinsicHeight());

        mInvisibleDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, com.lib_src.R.drawable.icon_password_visibility));
        mInvisibleDrawable.setBounds(0, 0, mInvisibleDrawable.getIntrinsicWidth(), mInvisibleDrawable.getIntrinsicHeight());

        // 密码是否可见
        mCurrentDrawable = isShowPwd ? mInvisibleDrawable : mVisibleDrawable;
        addInputType(isShowPwd ? TYPE_VISIBLE : TYPE_INVISIBLE);
        if (getInputRegex() == null) {
            // 密码输入规则
            setInputRegex(REGEX_PWD);
        }

        setDrawableVisible(true);
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        super.addTextChangedListener(this);
    }

    public void setTypeVisible(int typeVisible) {
        TYPE_VISIBLE = typeVisible;
    }
    public void setTypeInvisible(int typeInvisible) {
        TYPE_INVISIBLE = typeInvisible;
    }

    public int getTypeVisible() {
        return TYPE_VISIBLE;
    }

    public int getTypeInvisible() {
        return TYPE_INVISIBLE;
    }

    private void setDrawableVisible(boolean visible) {
//        if (mCurrentDrawable.isVisible() == visible) {
//            return;
//        }

        mCurrentDrawable.setVisible(visible, false);
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawablesRelative(
                drawables[0],
                drawables[1],
                visible ? mCurrentDrawable : null,
                drawables[3]);
    }

    private void refreshDrawableStatus() {
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawablesRelative(
                drawables[0],
                drawables[1],
                mCurrentDrawable,
                drawables[3]);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        mOnFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    /**
     * {@link OnFocusChangeListener}
     */

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
//        if (hasFocus && getText() != null) {
//            setDrawableVisible(getText().length() > 0);
//        } else {
//            setDrawableVisible(false);
//        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(view, hasFocus);
        }
    }

    /**
     * {@link OnTouchListener}
     */

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        if (mCurrentDrawable.isVisible() && x > getWidth() - getPaddingRight() - mCurrentDrawable.getIntrinsicWidth()) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (mCurrentDrawable == mVisibleDrawable) {
                    mCurrentDrawable = mInvisibleDrawable;
                    // 密码可见
                    removeInputType(TYPE_INVISIBLE);
                    addInputType(TYPE_VISIBLE);
                    refreshDrawableStatus();
                } else if (mCurrentDrawable == mInvisibleDrawable) {
                    mCurrentDrawable = mVisibleDrawable;
                    // 密码不可见
                    removeInputType(TYPE_VISIBLE);
                    addInputType(TYPE_INVISIBLE);
                    refreshDrawableStatus();
                }
                Editable editable = getText();
                if (editable != null) {
                    setSelection(editable.toString().length());
                }
            }
            return true;
        }
        return mOnTouchListener != null && mOnTouchListener.onTouch(view, motionEvent);
    }

    /**
     * {@link TextWatcher}
     */

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isFocused()) {
            setDrawableVisible(true);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}
}