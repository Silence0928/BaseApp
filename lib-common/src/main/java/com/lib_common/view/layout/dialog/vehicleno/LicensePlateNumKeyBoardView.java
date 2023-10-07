package com.lib_common.view.layout.dialog.vehicleno;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.coorchice.library.SuperTextView;
import com.lib_common.R;

public class LicensePlateNumKeyBoardView extends LinearLayout implements View.OnClickListener {
    private LinearLayout ll_province;
    private SuperTextView key_jing;
    private SuperTextView key_jin_tianjin;
    private SuperTextView key_ji_hebei;
    private SuperTextView key_jin_shanxi;
    private SuperTextView key_meng;
    private SuperTextView key_liao;
    private SuperTextView key_ji_jilin;
    private SuperTextView key_hei;
    private SuperTextView key_hu;
    private SuperTextView key_su;
    private SuperTextView key_zhe;
    private SuperTextView key_wan;
    private SuperTextView key_min;
    private SuperTextView key_gan_jiangxi;
    private SuperTextView key_lu;
    private SuperTextView key_yu_henan;
    private SuperTextView key_e_hubei;
    private SuperTextView key_xiang;
    private SuperTextView key_yue;
    private SuperTextView key_gui_guangxi;
    private SuperTextView key_qiong;
    private SuperTextView key_yu_chongqing;
    private SuperTextView key_chuan;
    private SuperTextView key_gui_guizhou;
    private SuperTextView key_yun;
    private SuperTextView key_zang;
    private SuperTextView key_shan;
    private SuperTextView key_gan_gansu;
    private SuperTextView key_qing;
    private SuperTextView key_ning;
    private SuperTextView key_xin;
    private ImageView iv_delete_province;

    private LinearLayout ll_letter_num;
    private SuperTextView key_0;
    private SuperTextView key_1;
    private SuperTextView key_2;
    private SuperTextView key_3;
    private SuperTextView key_4;
    private SuperTextView key_5;
    private SuperTextView key_6;
    private SuperTextView key_7;
    private SuperTextView key_8;
    private SuperTextView key_9;
    private SuperTextView key_a;
    private SuperTextView key_b;
    private SuperTextView key_c;
    private SuperTextView key_d;
    private SuperTextView key_e;
    private SuperTextView key_f;
    private SuperTextView key_g;
    private SuperTextView key_h;
    private SuperTextView key_j;
    private SuperTextView key_k;
    private SuperTextView key_l;
    private SuperTextView key_m;
    private SuperTextView key_n;
    private SuperTextView key_p;
    private SuperTextView key_q;
    private SuperTextView key_r;
    private SuperTextView key_s;
    private SuperTextView key_t;
    private SuperTextView key_u;
    private SuperTextView key_v;
    private SuperTextView key_w;
    private SuperTextView key_x;
    private SuperTextView key_y;
    private SuperTextView key_z;
    private SuperTextView key_gua; // 挂
    private ImageView iv_delete_letter_num;

    private Context mContext;
    private OnKeyEventListener mKeyEventListener;

    public static final int PROVINCE = 0;
    public static final int LETTER = 1;
    public static final int LETTER_NUM = 2;
    public static final int LETTER_GUA = 3;
    public LicensePlateNumKeyBoardView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public LicensePlateNumKeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public LicensePlateNumKeyBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        inflate(mContext, R.layout.view_license_plate_num, this);
        initView();
    }

    private void initView() {

        ll_province = findViewById(R.id.ll_province);
        key_jing = findViewById(R.id.key_jing);
        key_jin_tianjin = findViewById(R.id.key_jin_tianjin);
        key_ji_hebei = findViewById(R.id.key_ji_hebei);
        key_jin_shanxi = findViewById(R.id.key_jin_shanxi);
        key_meng = findViewById(R.id.key_meng);
        key_liao = findViewById(R.id.key_liao);
        key_ji_jilin = findViewById(R.id.key_ji_jilin);
        key_hei = findViewById(R.id.key_hei);
        key_hu = findViewById(R.id.key_hu);
        key_su = findViewById(R.id.key_su);
        key_zhe = findViewById(R.id.key_zhe);
        key_wan = findViewById(R.id.key_wan);
        key_min = findViewById(R.id.key_min);
        key_gan_jiangxi = findViewById(R.id.key_gan_jiangxi);
        key_lu = findViewById(R.id.key_lu);
        key_yu_henan = findViewById(R.id.key_yu_henan);
        key_e_hubei = findViewById(R.id.key_e_hubei);
        key_xiang = findViewById(R.id.key_xiang);
        key_yue = findViewById(R.id.key_yue);
        key_gui_guangxi = findViewById(R.id.key_gui_guangxi);
        key_qiong = findViewById(R.id.key_qiong);
        key_yu_chongqing = findViewById(R.id.key_yu_chongqing);
        key_chuan = findViewById(R.id.key_chuan);
        key_gui_guizhou = findViewById(R.id.key_gui_guizhou);
        key_yun = findViewById(R.id.key_yun);
        key_zang = findViewById(R.id.key_zang);
        key_shan = findViewById(R.id.key_shan);
        key_gan_gansu = findViewById(R.id.key_gan_gansu);
        key_qing = findViewById(R.id.key_qing);
        key_ning = findViewById(R.id.key_ning);
        key_xin = findViewById(R.id.key_xin);
        iv_delete_province = findViewById(R.id.iv_delete_province);

        ll_letter_num = findViewById(R.id.ll_letter_num);
        key_0 = findViewById(R.id.key_0);
        key_1 = findViewById(R.id.key_1);
        key_2 = findViewById(R.id.key_2);
        key_3 = findViewById(R.id.key_3);
        key_4 = findViewById(R.id.key_4);
        key_5 = findViewById(R.id.key_5);
        key_6 = findViewById(R.id.key_6);
        key_7 = findViewById(R.id.key_7);
        key_8 = findViewById(R.id.key_8);
        key_9 = findViewById(R.id.key_9);
        key_a = findViewById(R.id.key_a);
        key_b = findViewById(R.id.key_b);
        key_c = findViewById(R.id.key_c);
        key_d = findViewById(R.id.key_d);
        key_e = findViewById(R.id.key_e);
        key_f = findViewById(R.id.key_f);
        key_g = findViewById(R.id.key_g);
        key_h = findViewById(R.id.key_h);
        key_j = findViewById(R.id.key_j);
        key_k = findViewById(R.id.key_k);
        key_l = findViewById(R.id.key_l);
        key_m = findViewById(R.id.key_m);
        key_n = findViewById(R.id.key_n);
        key_p = findViewById(R.id.key_p);
        key_q = findViewById(R.id.key_q);
        key_r = findViewById(R.id.key_r);
        key_s = findViewById(R.id.key_s);
        key_t = findViewById(R.id.key_t);
        key_u = findViewById(R.id.key_u);
        key_v = findViewById(R.id.key_v);
        key_w = findViewById(R.id.key_w);
        key_x = findViewById(R.id.key_x);
        key_y = findViewById(R.id.key_y);
        key_z = findViewById(R.id.key_z);
        key_gua = findViewById(R.id.key_gua);
        iv_delete_letter_num = findViewById(R.id.iv_delete_letter_num);


        key_jing.setOnClickListener(this);
        key_jin_tianjin.setOnClickListener(this);
        key_ji_hebei.setOnClickListener(this);
        key_jin_shanxi.setOnClickListener(this);
        key_meng.setOnClickListener(this);
        key_liao.setOnClickListener(this);
        key_ji_jilin.setOnClickListener(this);
        key_hei.setOnClickListener(this);
        key_hu.setOnClickListener(this);
        key_su.setOnClickListener(this);
        key_zhe.setOnClickListener(this);
        key_wan.setOnClickListener(this);
        key_min.setOnClickListener(this);
        key_gan_jiangxi.setOnClickListener(this);
        key_lu.setOnClickListener(this);
        key_yu_henan.setOnClickListener(this);
        key_e_hubei.setOnClickListener(this);
        key_xiang.setOnClickListener(this);
        key_yue.setOnClickListener(this);
        key_gui_guangxi.setOnClickListener(this);
        key_qiong.setOnClickListener(this);
        key_yu_chongqing.setOnClickListener(this);
        key_chuan.setOnClickListener(this);
        key_gui_guizhou.setOnClickListener(this);
        key_yun.setOnClickListener(this);
        key_zang.setOnClickListener(this);
        key_shan.setOnClickListener(this);
        key_gan_gansu.setOnClickListener(this);
        key_qing.setOnClickListener(this);
        key_ning.setOnClickListener(this);
        key_xin.setOnClickListener(this);
        iv_delete_province.setOnClickListener(this);

        key_0.setOnClickListener(this);
        key_1.setOnClickListener(this);
        key_2.setOnClickListener(this);
        key_3.setOnClickListener(this);
        key_4.setOnClickListener(this);
        key_5.setOnClickListener(this);
        key_6.setOnClickListener(this);
        key_7.setOnClickListener(this);
        key_8.setOnClickListener(this);
        key_9.setOnClickListener(this);
        key_a.setOnClickListener(this);
        key_b.setOnClickListener(this);
        key_c.setOnClickListener(this);
        key_d.setOnClickListener(this);
        key_e.setOnClickListener(this);
        key_f.setOnClickListener(this);
        key_g.setOnClickListener(this);
        key_h.setOnClickListener(this);
        key_j.setOnClickListener(this);
        key_k.setOnClickListener(this);
        key_l.setOnClickListener(this);
        key_m.setOnClickListener(this);
        key_n.setOnClickListener(this);
        key_p.setOnClickListener(this);
        key_q.setOnClickListener(this);
        key_r.setOnClickListener(this);
        key_s.setOnClickListener(this);
        key_t.setOnClickListener(this);
        key_u.setOnClickListener(this);
        key_v.setOnClickListener(this);
        key_w.setOnClickListener(this);
        key_x.setOnClickListener(this);
        key_y.setOnClickListener(this);
        key_z.setOnClickListener(this);
        key_gua.setOnClickListener(this);
        iv_delete_letter_num.setOnClickListener(this);


    }

    public void setKeyBoardView(int type) {
        switch (type) {
            case PROVINCE:
                // 省
                ll_province.setVisibility(VISIBLE);
                ll_letter_num.setVisibility(GONE);
                break;
            case LETTER:
                // 字母
                ll_province.setVisibility(GONE);
                ll_letter_num.setVisibility(VISIBLE);
                setLetterEnable(true);
                setNumEnable(false);
                setGuaEnable(false);
                break;
            case LETTER_NUM:
                // 数字、字母
                ll_province.setVisibility(GONE);
                ll_letter_num.setVisibility(VISIBLE);
                setNumEnable(true);
                setLetterEnable(true);
                setGuaEnable(false);
                break;
            case LETTER_GUA:
                // 挂
                ll_province.setVisibility(GONE);
                ll_letter_num.setVisibility(VISIBLE);
                setNumEnable(false);
                setLetterEnable(false);
                setGuaEnable(true);
                break;
        }
    }

    /**
     * 数字
     * @param isEnable
     */
    private void setNumEnable(boolean isEnable) {
        key_0.setClickable(isEnable);
        key_1.setClickable(isEnable);
        key_2.setClickable(isEnable);
        key_3.setClickable(isEnable);
        key_4.setClickable(isEnable);
        key_5.setClickable(isEnable);
        key_6.setClickable(isEnable);
        key_7.setClickable(isEnable);
        key_8.setClickable(isEnable);
        key_9.setClickable(isEnable);

        int enableColor = mContext.getResources().getColor(isEnable ? com.lib_src.R.color.black04 : com.lib_src.R.color.black10);
        key_0.setTextColor(enableColor);
        key_1.setTextColor(enableColor);
        key_2.setTextColor(enableColor);
        key_3.setTextColor(enableColor);
        key_4.setTextColor(enableColor);
        key_5.setTextColor(enableColor);
        key_6.setTextColor(enableColor);
        key_7.setTextColor(enableColor);
        key_8.setTextColor(enableColor);
        key_9.setTextColor(enableColor);

        int solidColor = mContext.getResources().getColor(isEnable ? com.lib_src.R.color.white : com.lib_src.R.color.bg_grey_f5);
        key_0.setSolid(solidColor);
        key_1.setSolid(solidColor);
        key_2.setSolid(solidColor);
        key_3.setSolid(solidColor);
        key_4.setSolid(solidColor);
        key_5.setSolid(solidColor);
        key_6.setSolid(solidColor);
        key_7.setSolid(solidColor);
        key_8.setSolid(solidColor);
        key_9.setSolid(solidColor);
    }

    /**
     * 字母
     * @param isEnable
     */
    private void setLetterEnable(boolean isEnable) {
        key_a.setClickable(isEnable);
        key_b.setClickable(isEnable);
        key_c.setClickable(isEnable);
        key_d.setClickable(isEnable);
        key_e.setClickable(isEnable);
        key_f.setClickable(isEnable);
        key_g.setClickable(isEnable);
        key_h.setClickable(isEnable);
        key_j.setClickable(isEnable);
        key_k.setClickable(isEnable);
        key_l.setClickable(isEnable);
        key_m.setClickable(isEnable);
        key_n.setClickable(isEnable);
        key_p.setClickable(isEnable);
        key_q.setClickable(isEnable);
        key_r.setClickable(isEnable);
        key_s.setClickable(isEnable);
        key_t.setClickable(isEnable);
        key_u.setClickable(isEnable);
        key_v.setClickable(isEnable);
        key_w.setClickable(isEnable);
        key_x.setClickable(isEnable);
        key_y.setClickable(isEnable);
        key_z.setClickable(isEnable);

        int enableColor = mContext.getResources().getColor(isEnable ? com.lib_src.R.color.black04 : com.lib_src.R.color.black10);
        key_a.setTextColor(enableColor);
        key_b.setTextColor(enableColor);
        key_c.setTextColor(enableColor);
        key_d.setTextColor(enableColor);
        key_e.setTextColor(enableColor);
        key_f.setTextColor(enableColor);
        key_g.setTextColor(enableColor);
        key_h.setTextColor(enableColor);
        key_j.setTextColor(enableColor);
        key_k.setTextColor(enableColor);
        key_l.setTextColor(enableColor);
        key_m.setTextColor(enableColor);
        key_n.setTextColor(enableColor);
        key_p.setTextColor(enableColor);
        key_q.setTextColor(enableColor);
        key_r.setTextColor(enableColor);
        key_s.setTextColor(enableColor);
        key_t.setTextColor(enableColor);
        key_u.setTextColor(enableColor);
        key_v.setTextColor(enableColor);
        key_w.setTextColor(enableColor);
        key_x.setTextColor(enableColor);
        key_y.setTextColor(enableColor);
        key_z.setTextColor(enableColor);


        int solidColor = mContext.getResources().getColor(isEnable ? com.lib_src.R.color.white : com.lib_src.R.color.bg_grey_f5);
        key_a.setSolid(solidColor);
        key_b.setSolid(solidColor);
        key_c.setSolid(solidColor);
        key_d.setSolid(solidColor);
        key_e.setSolid(solidColor);
        key_f.setSolid(solidColor);
        key_g.setSolid(solidColor);
        key_h.setSolid(solidColor);
        key_j.setSolid(solidColor);
        key_k.setSolid(solidColor);
        key_l.setSolid(solidColor);
        key_m.setSolid(solidColor);
        key_n.setSolid(solidColor);
        key_p.setSolid(solidColor);
        key_q.setSolid(solidColor);
        key_r.setSolid(solidColor);
        key_s.setSolid(solidColor);
        key_t.setSolid(solidColor);
        key_u.setSolid(solidColor);
        key_v.setSolid(solidColor);
        key_w.setSolid(solidColor);
        key_x.setSolid(solidColor);
        key_y.setSolid(solidColor);
        key_z.setSolid(solidColor);
    }

    /**
     * 挂
     * @param isEnable
     */
    private void setGuaEnable(boolean isEnable) {
        key_gua.setClickable(isEnable);
        key_gua.setTextColor(mContext.getResources().getColor(isEnable ? com.lib_src.R.color.black04 : com.lib_src.R.color.black10));
        key_gua.setSolid(mContext.getResources().getColor(isEnable ? com.lib_src.R.color.white : com.lib_src.R.color.bg_grey_f5));
    }

    /**
     * 是否显示“挂”
     * @param isShow
     */
    public void showGua(boolean isShow) {
        key_gua.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        String key = "";
        if (v.getId() == R.id.key_jing) {
            key = "京";
        } else if (v.getId() == R.id.key_jin_tianjin) {
            key = "津";
        } else if (v.getId() == R.id.key_ji_hebei) {
            key = "冀";
        } else if (v.getId() == R.id.key_jin_shanxi) {
            key = "晋";
        } else if (v.getId() == R.id.key_meng) {
            key = "蒙";
        } else if (v.getId() == R.id.key_liao) {
            key = "辽";
        } else if (v.getId() == R.id.key_ji_jilin) {
            key = "吉";
        } else if (v.getId() == R.id.key_hei) {
            key = "黑";
        } else if (v.getId() == R.id.key_hu) {
            key = "沪";
        } else if (v.getId() == R.id.key_su) {
            key = "苏";
        } else if (v.getId() == R.id.key_zhe) {
            key = "浙";
        } else if (v.getId() == R.id.key_wan) {
            key = "皖";
        } else if (v.getId() == R.id.key_min) {
            key = "闽";
        } else if (v.getId() == R.id.key_gan_jiangxi) {
            key = "赣";
        } else if (v.getId() == R.id.key_lu) {
            key = "鲁";
        } else if (v.getId() == R.id.key_yu_henan) {
            key = "豫";
        } else if (v.getId() == R.id.key_e_hubei) {
            key = "鄂";
        } else if (v.getId() == R.id.key_xiang) {
            key = "湘";
        } else if (v.getId() == R.id.key_yue) {
            key = "粤";
        } else if (v.getId() == R.id.key_gui_guangxi) {
            key = "桂";
        } else if (v.getId() == R.id.key_qiong) {
            key = "琼";
        } else if (v.getId() == R.id.key_yu_chongqing) {
            key = "渝";
        } else if (v.getId() == R.id.key_chuan) {
            key = "川";
        } else if (v.getId() == R.id.key_gui_guizhou) {
            key = "贵";
        } else if (v.getId() == R.id.key_yun) {
            key = "云";
        } else if (v.getId() == R.id.key_zang) {
            key = "藏";
        } else if (v.getId() == R.id.key_shan) {
            key = "陕";
        } else if (v.getId() == R.id.key_gan_gansu) {
            key = "甘";
        } else if (v.getId() == R.id.key_qing) {
            key = "青";
        } else if (v.getId() == R.id.key_ning) {
            key = "宁";
        } else if (v.getId() == R.id.key_xin) {
            key = "新";
        } else if (v.getId() == R.id.key_0) {
            key = "0";
        } else if (v.getId() == R.id.key_1) {
            key = "1";
        } else if (v.getId() == R.id.key_2) {
            key = "2";
        } else if (v.getId() == R.id.key_3) {
            key = "3";
        } else if (v.getId() == R.id.key_4) {
            key = "4";
        } else if (v.getId() == R.id.key_5) {
            key = "5";
        } else if (v.getId() == R.id.key_6) {
            key = "6";
        } else if (v.getId() == R.id.key_7) {
            key = "7";
        } else if (v.getId() == R.id.key_8) {
            key = "8";
        } else if (v.getId() == R.id.key_9) {
            key = "9";
        } else if (v.getId() == R.id.key_a) {
            key = "A";
        } else if (v.getId() == R.id.key_b) {
            key = "B";
        } else if (v.getId() == R.id.key_c) {
            key = "C";
        } else if (v.getId() == R.id.key_d) {
            key = "D";
        } else if (v.getId() == R.id.key_e) {
            key = "E";
        } else if (v.getId() == R.id.key_f) {
            key = "F";
        } else if (v.getId() == R.id.key_g) {
            key = "G";
        } else if (v.getId() == R.id.key_h) {
            key = "H";
        } else if (v.getId() == R.id.key_j) {
            key = "J";
        } else if (v.getId() == R.id.key_k) {
            key = "K";
        } else if (v.getId() == R.id.key_l) {
            key = "L";
        } else if (v.getId() == R.id.key_m) {
            key = "M";
        } else if (v.getId() == R.id.key_n) {
            key = "N";
        } else if (v.getId() == R.id.key_p) {
            key = "P";
        } else if (v.getId() == R.id.key_q) {
            key = "Q";
        } else if (v.getId() == R.id.key_r) {
            key = "R";
        } else if (v.getId() == R.id.key_s) {
            key = "S";
        } else if (v.getId() == R.id.key_t) {
            key = "T";
        } else if (v.getId() == R.id.key_u) {
            key = "U";
        } else if (v.getId() == R.id.key_v) {
            key = "V";
        } else if (v.getId() == R.id.key_w) {
            key = "W";
        } else if (v.getId() == R.id.key_x) {
            key = "X";
        } else if (v.getId() == R.id.key_y) {
            key = "Y";
        } else if (v.getId() == R.id.key_z) {
            key = "Z";
        } else if (v.getId() == R.id.key_gua) {
            key = "挂";
        } else if (v.getId() == R.id.iv_delete_province ||
                v.getId() == R.id.iv_delete_letter_num) {
            key = "del";
        }


        if (mKeyEventListener != null) {
            mKeyEventListener.onEvent(key);
        }
    }

    public OnKeyEventListener getKeyEventListener() {
        return mKeyEventListener;
    }

    public void setKeyEventListener(OnKeyEventListener mKeyEventListener) {
        this.mKeyEventListener = mKeyEventListener;
    }

    public interface OnKeyEventListener {
        void onEvent(String key);
    }
}
