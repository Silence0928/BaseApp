package com.lib_common.utils;


import com.lib_common.app.BaseApplication;

/**
 * author: zhaohongtao
 * time  :2021/8/17 10:19
 * desc : The constants of memory.
 */
public class BasicUtil {

    /**
     * 获取string资源
     * @param id
     * @return
     */
    public static String getStringById(int id) {
        return BaseApplication.getApplication().getString(id);
    }

    /**
     * 获取颜色值
     * @param colorId
     * @return
     */
    public static int getColorById(int colorId) {
        return BaseApplication.getApplication().getResources().getColor(colorId);
    }
}
