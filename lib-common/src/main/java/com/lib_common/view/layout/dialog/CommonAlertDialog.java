package com.lib_common.view.layout.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lib_common.R;
import com.lib_common.utils.StringUtils;


/**
 * author: zhaohongtao
 * blog  :
 * time  :2023/09/20 10:29
 * desc  : 公共弹窗
 */
public class CommonAlertDialog {

    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private LinearLayout bank_title;
    private LinearLayout ll_wallet_title;
    private TextView txt_title;
    private TextView txt_msg;
    private ImageView iv_msg_icon;
    private TextView txt_msg_content;
    private TextView txt_id_card;
    private TextView txt_id_card_num;
    private TextView txt_wallet_num;
    private TextView txt_wallet_content;
    private TextView txt_id_card_content;
    private LinearLayout imgMsgLayout;
    private Button btn_neg;
    private Button btn_pos;
    private ImageView img_line;
    private Display display;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    AgreementCallBack agreementCallBack;
    private String idCard, idCardName, idCardNum, walletName, walletNum;

    public interface AgreementCallBack {
        void peopAgreement();

        void privacyPolicy();
    }

    public CommonAlertDialog(Context context, AgreementCallBack callBack) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        agreementCallBack = callBack;
    }

    public CommonAlertDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public CommonAlertDialog(Context context,
                             String idCard, String idCardName, String idCardNum,
                             String walletName, String walletNum) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        this.idCard = idCard;
        this.idCardName = idCardName;
        this.idCardNum = idCardNum;
        this.walletName = walletName;
        this.walletNum = walletNum;
    }


    public CommonAlertDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                com.lib_common.R.layout.layout_common_alert_dialog, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        imgMsgLayout = view.findViewById(R.id.ll_img_msg);
//        txt_title.setVisibility(View.GONE);
        txt_msg = (TextView) view.findViewById(R.id.txt_msg);
        iv_msg_icon = (ImageView) view.findViewById(R.id.iv_msg_icon);
        txt_msg.setVisibility(View.GONE);
        iv_msg_icon.setVisibility(View.GONE);
        txt_msg_content = (TextView) view.findViewById(R.id.txt_msg_content);
        txt_msg_content.setVisibility(View.GONE);
        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.GONE);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        btn_pos.setVisibility(View.GONE);
        img_line = (ImageView) view.findViewById(R.id.img_line);
        img_line.setVisibility(View.GONE);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.85), LinearLayout.LayoutParams.WRAP_CONTENT));
        //最大高度限制
        txt_msg.setMaxHeight((int) (display.getHeight() * 0.5));
        txt_msg.setMovementMethod(ScrollingMovementMethod.getInstance());
        txt_msg_content.setMaxHeight((int) (display.getHeight() * 0.5));
        txt_msg_content.setMovementMethod(ScrollingMovementMethod.getInstance());

        return this;
    }

    /**
     * 设置底部的提示内容-参考样式-道运经营许可证示例弹窗
     * @param content
     * @return
     */
    public CommonAlertDialog setBottomContent(String content) {
        if (txt_msg != null) {
            showMsg = true;
            txt_msg.setText(content);
        }
        return this;
    }

    private void setAgrement() {
        String str = "请您务必仔细阅读、充分理解“用户协议”和“隐私政策”各条款包括但不限于为了向您提供服务而收集、使用、存储您的个人信息等，您可阅读《用户协议》和《隐私政策》了解详细信息。如果您同意，请点击“同意”开始接受我们的服务。";

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(str);

        final int start = str.indexOf("《");//第一个出现的位置
        ssb.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                agreementCallBack.peopAgreement();

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(com.lib_src.R.color.main_color));       //设置文件颜色
                // 去掉下划线
                ds.setUnderlineText(false);
            }

        }, start, start + 6, 0);

        final int end = str.lastIndexOf("《");//最后一个出现的位置
        ssb.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                agreementCallBack.privacyPolicy();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(com.lib_src.R.color.main_color));       //设置文件颜色
                // 去掉下划线
                ds.setUnderlineText(false);
            }

        }, end, end + 6, 0);

        txt_msg.setMovementMethod(LinkMovementMethod.getInstance());
        txt_msg.setText(ssb, TextView.BufferType.SPANNABLE);
        // 解决强制换行问题
//        txt_msg.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener(txt_msg));
    }

    public CommonAlertDialog setTitle(int titleId) {
        return setTitle(context.getString(titleId));
    }

    public CommonAlertDialog setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            txt_title.setVisibility(View.GONE);
        } else {
            txt_title.setText(title);
        }
        return this;
    }

    public CommonAlertDialog setMsg(CharSequence msg) {
        showMsg = true;
        txt_msg.setText(msg);
        txt_msg.setGravity(Gravity.CENTER);
        // 解决强制换行问题
        txt_msg.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener(txt_msg));
        return this;
    }

    public CommonAlertDialog setSpannedMsg(CharSequence msg) {
        showMsg = true;
        txt_msg.setText(msg);
        txt_msg.setMovementMethod(LinkMovementMethod.getInstance());
        txt_msg.setGravity(Gravity.CENTER);
        // 解决强制换行问题
        txt_msg.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener(txt_msg));
        return this;
    }

    public CommonAlertDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public CommonAlertDialog setAgrementMsg() {
        showMsg = true;
        setAgrement();
        return this;
    }

    public CommonAlertDialog setMsgContent(String msg) {
        return setMsgContent(msg, context.getResources().getColor(com.lib_src.R.color.black_333333), Gravity.CENTER);
    }

    public CommonAlertDialog setMsgLeftContent(String msg) {
        return setMsgContent(msg, context.getResources().getColor(com.lib_src.R.color.black_333333), Gravity.LEFT);
    }

    public CommonAlertDialog setMsgContent(String msg, int color) {
        return setMsgContent(msg, color, Gravity.CENTER);
    }

    public CommonAlertDialog setMsgContent(String msg, int color, int gravity) {
        return setMsgContent(msg, null, color, gravity);
    }

    public CommonAlertDialog setMsgContent(CharSequence msg, TextView.BufferType type, int color, int gravity) {
        if (imgMsgLayout != null) {
            imgMsgLayout.setVisibility(View.GONE);
        }
        txt_msg_content.setVisibility(View.VISIBLE);
        if(type == null){
            txt_msg_content.setText(msg);
        }else {
            txt_msg_content.setText(msg,type);
        }
        txt_msg_content.setTextColor(color);
        txt_msg_content.setGravity(gravity);
        // 解决强制换行问题
        txt_msg_content.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener(txt_msg_content));
        return this;
    }

    /**
     * 设置副内容
     */
    public CommonAlertDialog setSubMsgContent(CharSequence msg) {
        setSubMsgContent(msg, Gravity.CENTER);
        return this;
    }

    public CommonAlertDialog setSubMsgContent(CharSequence msg, int gravity) {
        txt_msg_content.setText(msg);
        txt_msg_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        txt_msg_content.setVisibility(View.VISIBLE);
        txt_msg_content.setTextColor(context.getResources().getColor(com.lib_src.R.color.black_666666));
        txt_msg_content.setGravity(gravity);
        // 解决强制换行问题
        txt_msg_content.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener(txt_msg_content));
        return this;
    }

    public CommonAlertDialog setTipContent(String msg) {
        iv_msg_icon.setVisibility(View.VISIBLE);
        showMsg = true;
        txt_msg.setText(msg);
//        txt_msg.setGravity(Gravity.CENTER);
        // 解决强制换行问题
        txt_msg.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener(txt_msg));
        return this;
    }

    public CommonAlertDialog setMsg(String msg) {
        showMsg = true;
        txt_msg.setText(msg);
        txt_msg.setGravity(Gravity.CENTER);
        // 解决强制换行问题
        txt_msg.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener(txt_msg));
        return this;
    }

    public CommonAlertDialog setGreenTel(String msg) {
        showMsg = true;
        txt_msg.setGravity(Gravity.CENTER);
        StringUtils.setTelGreen(context, msg, txt_msg);
        return this;
    }

    public CommonAlertDialog setGravity(int gravity) {
        txt_msg.setGravity(gravity);
        return this;
    }

    public CommonAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public CommonAlertDialog setPositiveButton(String text,
                                           final View.OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("确定");
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public CommonAlertDialog setPositiveButton(String text, int color,
                                           final View.OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("确定");
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setTextColor(color);
        btn_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public CommonAlertDialog setNegativeButton(int textId,
                                           final View.OnClickListener listener) {
        return setNegativeButton(context.getString(textId), listener);
    }

    public CommonAlertDialog setNegativeButton(String text,
                                           final View.OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText("取消");
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });
        return this;
    }

    public CommonAlertDialog setNegativeButtonGone() {
        btn_neg.setVisibility(View.GONE);
        return this;
    }

    public CommonAlertDialog setPositiveButtonGone() {
        btn_pos.setVisibility(View.GONE);
        img_line.setVisibility(View.GONE);
        return this;
    }

    public CommonAlertDialog setNegativeButton(String text,
                                           final View.OnClickListener listener, int color) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText("取消");
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setTextColor(color);
        btn_neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v);
                }
                dialog.dismiss();
            }
        });
        return this;
    }

    private void setLayout() {
        if (!showTitle && !showMsg) {
            txt_title.setText("提示");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn && btn_pos != null) {
            btn_pos.setText("确定");
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(com.lib_src.R.drawable.alertdialog_single_selector);
            btn_pos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(com.lib_src.R.drawable.alertdialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(com.lib_src.R.drawable.alertdialog_left_selector);
            img_line.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(com.lib_src.R.drawable.alertdialog_single_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(com.lib_src.R.drawable.alertdialog_single_selector);
        }
    }

    public void show() {
        setLayout();
        dialog.show();
    }

    public void showVehicleExplain() {
        dialog.show();
    }

    public boolean isShow() {
        return dialog.isShowing();
    }

    private static String autoSplitText(final TextView tv) {
        final String rawText = tv.getText().toString(); //原始文本
        final Paint tvPaint = tv.getPaint(); //paint，包含字体等信息
        final float tvWidth = tv.getWidth() - tv.getPaddingLeft() - tv.getPaddingRight(); //控件可用宽度

        //将原始文本按行拆分
        String [] rawTextLines = rawText.replaceAll("\r", "").split("\n");
        StringBuilder sbNewText = new StringBuilder();
        for (String rawTextLine : rawTextLines) {
            if (tvPaint.measureText(rawTextLine) <= tvWidth) {
                //如果整行宽度在控件可用宽度之内，就不处理了
                sbNewText.append(rawTextLine);
            } else {
                //如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
                float lineWidth = 0;
                for (int cnt = 0; cnt != rawTextLine.length(); ++cnt) {
                    char ch = rawTextLine.charAt(cnt);
                    lineWidth += tvPaint.measureText(String.valueOf(ch));
                    if (lineWidth <= tvWidth) {
                        sbNewText.append(ch);
                    } else {
                        sbNewText.append("\n");
                        lineWidth = 0;
                        --cnt;
                    }
                }
            }
            sbNewText.append("\n");
        }

        //把结尾多余的\n去掉
        if (!rawText.endsWith("\n")) {
            sbNewText.deleteCharAt(sbNewText.length() - 1);
        }

        return sbNewText.toString();
    }

    private static class OnTvGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        private TextView mTv;
        public OnTvGlobalLayoutListener(TextView textView) {
            this.mTv = textView;
        }

        @Override
        public void onGlobalLayout() {
            mTv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            final String newText = autoSplitText(mTv);
            if (!TextUtils.isEmpty(newText)) {
                mTv.setText(newText);
            }
        }
    }
}
