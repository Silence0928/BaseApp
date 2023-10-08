package com.lib_common.constants;

public class Constants {
    /**
     * 默认日期格式
     */
    public static final String DATE_FORMAT_SLASH = "yyyy/MM/dd";
    public static final String DATE_FORMAT_LINE = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DEFAULT = DATE_FORMAT_SLASH + " HH:mm:ss";
    public static final String DATE_FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_MILE_TIME = "yyyy-MM-dd HH:mm:ss:SSS";
    public static final String PROVIDER_AUTHORITIES = "com.stas.whms";
    // 是否可以弹普通升级窗 默认显示
    public static boolean isShowUpdateDialog = true;
    // 是否通知栏返回，用于新版本安装
    public static boolean isNotificationBack = false;
    // APP包下载地址
    public static String appUpdateUrl = "";
}
