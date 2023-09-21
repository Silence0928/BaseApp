package com.lib_common.utils;

import static java.lang.StrictMath.abs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str) || str.trim().length() == 0;
    }

    public static String goodsName2(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        try {
            String[] split = name.split(",");
            return split[1] + split[2];
        } catch (Exception e) {
            return name.replaceAll(",", "");
        }
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String TruncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;

        strURL = strURL.trim().toLowerCase();

        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }

        return strAllParam;
    }

    /**
     * 解析出url参数中的键值队
     * 如 "www.xx.int?Action=int&id12345"，解析出Action:int,id:12345存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    public static Map<String, String> URLRequest(String URL) {
        Map<String, String> mapRequest = new HashMap<String, String>();

        String[] arrSplit = null;
        String[] arrSplit1 = null;

        //每个键值为一组 www.2cto.com
        arrSplit1 = URL.split("[?]");
        for (String strSplit1 : arrSplit1) {
            arrSplit = strSplit1.split("[&]");
            for (String strSplit : arrSplit) {
                String[] arrSplitEqual = null;
                arrSplitEqual = strSplit.split("[=]");
                //解析出键值
                if (arrSplitEqual.length > 1) {
                    //正确解析
                    mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
                } else {
                    if (arrSplitEqual[0] != "") {
                        //只有参数没有值，不加入
                        mapRequest.put(arrSplitEqual[0], "");
                    }
                }
            }
        }
        return mapRequest;
    }

    public static String getUrlParam(String url, String name) {
        return URLRequest(url).get(name);
    }


    /**
     * 判断是否为汉字
     *
     * @param string
     * @return
     */

    public static boolean isChinese(String string) {
        int n = 0;
        for (int i = 0; i < string.length(); i++) {
            n = (int) string.charAt(i);
            if (!(19968 <= n && n < 40869)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 1.判断字符串是否仅为数字:
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 设置string 最后一位的颜色  （猪猪*） 必填项指示
     *
     * @param textView
     * @param name
     */
    public static void setStringEndColor(TextView textView, String name) {
        SpannableString spannableString = new SpannableString(name);
        @SuppressLint("ResourceAsColor")
        BackgroundColorSpan colorSpan = new BackgroundColorSpan(com.lib_src.R.color.red);
        spannableString.setSpan(colorSpan, spannableString.length() - 1, spannableString.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }


    /**
     * 版本号比较
     *
     * @param version1 本地版本号
     * @param version2 服务器版本号
     * @return 0 本地版本小于服务器版本   1 版本相同    -1  本地版本大于服务器版本
     */
    public static int compareVersion(String version1, String version2) {
        if (version1.equals(version2)) {
            return 0;
        }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");

        int index = 0;
        // 获取最小长度值
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        // 循环判断每位的大小
        while (index < minLen
                && (diff = Integer.parseInt(version1Array[index])
                - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            // 如果位数不一致，比较多余位数
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    private static int StringToInt(String string) {
        return Integer.parseInt(string);
    }

    public static boolean isDoubleEmpty(double d) {
        if (abs(d) < 0.01) {
            return true;
//            System.out.println("等于0");
        } else {
            return false;
//            System.out.println("不等于0");
        }
    }

    public static boolean isStringEmpty(String s) {
        if (s.equals("0") || s.equals("0.00")) {
            return true;
//            System.out.println("等于0");
        } else {
            return false;
//            System.out.println("不等于0");
        }
    }

    public static void isDoubleToString(double d) {
        System.out.println(String.valueOf(d));
    }

    public static String isStringTo2(String a) {
        if (a == null) {
            return "0.00";
        }
        if (a.equals("")) {
            return "0.00";
        }
        String name = null;
        Double d = Double.parseDouble(a);
        DecimalFormat df = new DecimalFormat("0.00");
        name = df.format(d);
        return name;
    }

    public static String isMoneyDoubleToString(Double a) {
        if (a == null) {
            return "0.00";
        }
        if (a.equals("")) {
            return "0.00";
        }
        String name = null;
        DecimalFormat df = new DecimalFormat("0.00");
        name = df.format(a);
        return name;
    }

    private static void setLong(long l) {
        System.out.println(l);
    }

    /**
     * 截取指定字符之后的数据
     *
     * @param a
     * @return
     */
    public static String stringSub(String a, String sub) {
        return a.substring(a.indexOf(sub) + 1);
    }

    private static void toJsonString(String scanResult) {
        String orgCode = "{" +
                "orgCode:" + scanResult + '}';
        System.out.println(orgCode);
    }

    /**
     * 保留2位小数
     *
     * @param d
     * @return
     */
    public static String doubleto2(double d) {
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(d);
        } catch (Exception e) {
            e.printStackTrace();
            return "0.00";
        }
    }

    /**
     * 保留位小数
     *
     * @param d
     * @return
     */
    public static String doubleto2(double d, int quantityDecimal) {
        StringBuilder pattern = new StringBuilder();
        pattern.append("0");
        if (quantityDecimal > 0) {
            pattern.append(".");
        }
        for (int i = 0; i < quantityDecimal; i++) {
            pattern.append("0");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(d);
    }

    /**
     * @param string
     * @param length 限制多少位
     * @return
     */
    public static String strToLimitedLength(String string, int length) {
        StringBuffer stringBuffer = new StringBuffer();
        if (string.length() > length) {
            stringBuffer.append(string, 0, length);
            stringBuffer.append("...");
        } else {
            stringBuffer.append(string);
        }
        return stringBuffer.toString();
    }

    /**
     * 地址转换
     *
     * @param name
     * @param address
     * @return
     */
    public static String addressArea(String name, String address) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            if (name != null) {
                String[] split = name.split(",");
                stringBuffer.append(split[1] + split[2] + address);
            }
        } catch (Exception e) {
            stringBuffer.append(name);
        }
        return stringBuffer.toString();
    }

    private static String jsonToString(Object o) {
        return JSON.toJSONString(o);
    }

    /**
     * 地址转换
     *
     * @param name
     * @return
     */
    public static String addressCity(String name) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            if (name != null) {
                String[] split = name.split(",");
                stringBuffer.append(split[1]);
            }
        } catch (Exception e) {
            stringBuffer.append(name);
        }
        return stringBuffer.toString();
    }

    private static String bank(String a) {
        if (a != null) {
            return a.replaceAll("\\d{4}(?!$)", "$0 ");
        }
        return "0";
    }

    /**
     * Judge whether a string is whitespace, empty ("") or null.
     *
     * @param str
     * @return
     */
    public static boolean isEmpty1(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0 || str.equalsIgnoreCase("null")) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断str null,"","null" 均视为空.
     *
     * @param str 字符
     * @return 结果 boolean
     */
    public static boolean isNotEmpty(String str) {
        boolean bool = true;
        if (str == null || "null".equals(str) || "".equals(str)) {
            bool = false;
        } else {
            bool = true;
        }
        return bool;
    }

    /**
     * 检测String是否全是中文
     *
     * @param name
     * @return
     */

    public static boolean checkNameChese(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            if (!isChinese(cTemp[i])) {
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * 判定输入汉字
     *
     * @param c
     * @return
     */

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {

            return true;
        }
        return false;

    }

    /**
     * 编译后的正则表达式缓存
     */
    private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();

    /**
     * 编译一个正则表达式，并且进行缓存,如果缓存已存在则使用缓存
     *
     * @param regex 表达式
     * @return 编译后的Pattern
     */
    public static Pattern compileRegex(String regex) {
        Pattern pattern = PATTERN_CACHE.get(regex);
        if (pattern == null) {
            pattern = Pattern.compile(regex);
            PATTERN_CACHE.put(regex, pattern);
        }
        return pattern;
    }

    /**
     * 把字符串转成小写
     *
     * @param s
     * @return
     */
    public static String toLowerCase(String s) {
        return s.toLowerCase(Locale.getDefault());
    }

    /**
     * 将字符串的第一位转为小写
     *
     * @param str 需要转换的字符串
     * @return 转换后的字符串
     */
    public static String toLowerCaseFirstOne(String str) {
        if (Character.isLowerCase(str.charAt(0)))
            return str;
        else {
            char[] chars = str.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            return new String(chars);
        }
    }

    /**
     * 将字符串的第一位转为大写
     *
     * @param str 需要转换的字符串
     * @return 转换后的字符串
     */
    public static String toUpperCaseFirstOne(String str) {
        if (Character.isUpperCase(str.charAt(0)))
            return str;
        else {
            char[] chars = str.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        }
    }

    /**
     * 下划线命名转为驼峰命名
     *
     * @param str 下划线命名格式
     * @return 驼峰命名格式
     */
    public static String underScoreCase2CamelCase(String str) {
        if (!str.contains("_")) return str;
        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();
        boolean hitUnderScore = false;
        sb.append(chars[0]);
        for (int i = 1; i < chars.length; i++) {
            char c = chars[i];
            if (c == '_') {
                hitUnderScore = true;
            } else {
                if (hitUnderScore) {
                    sb.append(Character.toUpperCase(c));
                    hitUnderScore = false;
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰命名法转为下划线命名
     *
     * @param str 驼峰命名格式
     * @return 下划线命名格式
     */
    public static String camelCase2UnderScoreCase(String str) {
        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isUpperCase(c)) {
                sb.append("_").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 将异常栈信息转为字符串
     *
     * @param e 字符串
     * @return 异常栈
     */
    public static String throwable2String(Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    /**
     * 字符串连接，将参数列表拼接为一个字符串
     *
     * @param more 追加
     * @return 返回拼接后的字符串
     */
    public static String concat(Object... more) {
        return concatSpiltWith("", more);
    }

    /**
     * 字符串连接，将参数列表拼接为一个字符串
     *
     * @param split
     * @param more
     * @return 回拼接后的字符串
     */
    public static String concatSpiltWith(String split, Object... more) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < more.length; i++) {
            if (i != 0) buf.append(split);
            buf.append(more[i]);
        }
        return buf.toString();
    }

    /**
     * 将字符串转移为ASCII码
     *
     * @param str 字符串
     * @return 字符串ASCII码
     */
    public static String toASCII(String str) {
        StringBuffer strBuf = new StringBuffer();
        byte[] bGBK = str.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
        }
        return strBuf.toString();
    }

    /**
     * 将字符串转移为Unicode码
     *
     * @param str 字符串
     * @return
     */
    public static String toUnicode(String str) {
        StringBuffer strBuf = new StringBuffer();
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            strBuf.append("\\u").append(Integer.toHexString(chars[i]));
        }
        return strBuf.toString();
    }

    public static void setTelGreen(Context context, String content, TextView view) {
        SpannableString textSpanned21 = new SpannableString(content);
        textSpanned21.setSpan(new ClickableSpan() {

                                  @Override
                                  public void onClick(View widget) {
                                      AndroidUtil.callServicePhone(Objects.requireNonNull(context));
                                  }

                                  @Override
                                  public void updateDrawState(TextPaint ds) {
                                      super.updateDrawState(ds);
                                      ds.setColor(context.getResources().getColor(com.lib_src.R.color.main_color));
                                      //设置文件颜色
                                      // 去掉下划线
                                      ds.setUnderlineText(false);
                                  }

                              },
                textSpanned21.length() - 12, textSpanned21.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(textSpanned21);
    }

    /**
     * 将字符串转移为Unicode码
     *
     * @param chars 字符数组
     * @return
     */
    public static String toUnicodeString(char[] chars) {
        StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            strBuf.append("\\u").append(Integer.toHexString(chars[i]));
        }
        return strBuf.toString();
    }

    static final char CN_CHAR_START = '\u4e00';
    static final char CN_CHAR_END = '\u9fa5';

    /**
     * 是否包含中文字符
     *
     * @param str 要判断的字符串
     * @return 是否包含中文字符
     */
    public static boolean containsChineseChar(String str) {
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= CN_CHAR_START && chars[i] <= CN_CHAR_END) return true;
        }
        return false;
    }

    /**
     * 对象是否为无效值
     *
     * @param obj 要判断的对象
     * @return 是否为有效值（不为null 和 "" 字符串）
     */
    public static boolean isNullOrEmpty(Object obj) {
        return obj == null || "".equals(obj.toString());
    }

    /**
     * 参数是否是有效数字 （整数或者小数）
     *
     * @param obj 参数（对象将被调用string()转为字符串类型）
     * @return 是否是数字
     */
    public static boolean isNumber(Object obj) {
        if (obj instanceof Number) return true;
        return isInt(obj) || isDouble(obj);
    }

    /**
     * 匹配到第一个字符串
     *
     * @param patternStr 正则表达式
     * @param text       字符串
     * @return
     */
    public static String matcherFirst(String patternStr, String text) {
        Pattern pattern = compileRegex(patternStr);
        Matcher matcher = pattern.matcher(text);
        String group = null;
        if (matcher.find()) {
            group = matcher.group();
        }
        return group;
    }


    public static String toInt2(String name) {
        DecimalFormat format = new DecimalFormat("0.00");
        String a = format.format(new BigDecimal(name));
        return a;
    }

    /**
     * 参数是否是有效整数
     *
     * @param obj 参数（对象将被调用string()转为字符串类型）
     * @return 是否是整数
     */
    public static boolean isInt(Object obj) {
        if (isNullOrEmpty(obj))
            return false;
        if (obj instanceof Integer)
            return true;
        return obj.toString().matches("[-+]?\\d+");
    }

    /**
     * 字符串参数是否是double
     *
     * @param obj 参数（对象将被调用string()转为字符串类型）
     * @return 是否是double
     */
    public static boolean isDouble(Object obj) {
        if (isNullOrEmpty(obj))
            return false;
        if (obj instanceof Double || obj instanceof Float)
            return true;
        return compileRegex("[-+]?\\d+\\.\\d+").matcher(obj.toString()).matches();
    }

    /**
     * 判断一个对象是否为boolean类型,包括字符串中的true和false
     *
     * @param obj 要判断的对象
     * @return 是否是一个boolean类型
     */
    public static boolean isBoolean(Object obj) {
        if (obj instanceof Boolean) return true;
        String strVal = String.valueOf(obj);
        return "true".equalsIgnoreCase(strVal) || "false".equalsIgnoreCase(strVal);
    }

    /**
     * 对象是否为true
     *
     * @param obj
     * @return
     */
    public static boolean isTrue(Object obj) {
        return "true".equals(String.valueOf(obj));
    }

    /**
     * 判断一个数组里是否包含指定对象
     *
     * @param arr 对象数组
     * @param obj 要判断的对象
     * @return 是否包含
     */
    public static boolean contains(Object arr[], Object... obj) {
        if (arr == null || obj == null || arr.length == 0) return false;
        return Arrays.asList(arr).containsAll(Arrays.asList(obj));
    }

    /**
     * 将对象转为int值,如果对象无法进行转换,则使用默认值
     *
     * @param object       要转换的对象
     * @param defaultValue 默认值
     * @return 转换后的值
     */
    public static int toInt(Object object, int defaultValue) {
        if (object instanceof Number)
            return ((Number) object).intValue();
        if (isInt(object)) {
            return Integer.parseInt(object.toString());
        }
        if (isDouble(object)) {
            return (int) Double.parseDouble(object.toString());
        }
        return defaultValue;
    }

    /**
     * 将对象转为int值,如果对象不能转为,将返回0
     *
     * @param object 要转换的对象
     * @return 转换后的值
     */
    public static int toInt(Object object) {
        return toInt(object, 0);
    }

    /**
     * 将对象转为long类型,如果对象无法转换,将返回默认值
     *
     * @param object       要转换的对象
     * @param defaultValue 默认值
     * @return 转换后的值
     */
    public static long toLong(Object object, long defaultValue) {
        if (object instanceof Number)
            return ((Number) object).longValue();
        if (isInt(object)) {
            return Long.parseLong(object.toString());
        }
        if (isDouble(object)) {
            return (long) Double.parseDouble(object.toString());
        }
        return defaultValue;
    }

    /**
     * 将对象转为 long值,如果无法转换,则转为0
     *
     * @param object 要转换的对象
     * @return 转换后的值
     */
    public static long toLong(Object object) {
        return toLong(object, 0);
    }

    /**
     * 将对象转为Double,如果对象无法转换,将使用默认值
     *
     * @param object       要转换的对象
     * @param defaultValue 默认值
     * @return 转换后的值
     */
    public static double toDouble(Object object, double defaultValue) {
        if (object instanceof Number)
            return ((Number) object).doubleValue();
        if (isNumber(object)) {
            return Double.parseDouble(object.toString());
        }
        if (null == object) return defaultValue;
        return 0;
    }

    /**
     * 将对象转为Double,如果对象无法转换,将使用默认值0
     *
     * @param object 要转换的对象
     * @return 转换后的值
     */
    public static double toDouble(Object object) {
        return toDouble(object, 0);
    }

    /**
     * 分隔字符串,根据正则表达式分隔字符串,只分隔首个,剩下的的不进行分隔,如: 1,2,3,4 将分隔为 ['1','2,3,4']
     *
     * @param str   要分隔的字符串
     * @param regex 分隔表达式
     * @return 分隔后的数组
     */
    public static String[] splitFirst(String str, String regex) {
        return str.split(regex, 2);
    }

    /**
     * 将对象转为字符串,如果对象为null,则返回null,而不是"null"
     *
     * @param object 要转换的对象
     * @return 转换后的对象
     */
    public static String toString(Object object) {
        return toString(object, null);
    }

    /**
     * 将对象转为字符串,如果对象为null,则使用默认值
     *
     * @param object       要转换的对象
     * @param defaultValue 默认值
     * @return 转换后的字符串
     */
    public static String toString(Object object, String defaultValue) {
        if (object == null) return defaultValue;
        return String.valueOf(object);
    }

    /**
     * 将对象转为String后进行分割，如果为对象为空或者空字符,则返回null
     *
     * @param object 要分隔的对象
     * @param regex  分隔规则
     * @return 分隔后的对象
     */
    public static String[] toStringAndSplit(Object object, String regex) {
        if (isNullOrEmpty(object)) return null;
        return String.valueOf(object).split(regex);
    }

//    private static boolean isChinese(char c) {
//        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
//        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
//            || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
//            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
//            || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
//            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
//            || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
//            return true;
//        }
//        return false;
//    }

    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = 0;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
                chLength++;
            }
        }
        float result = count / chLength;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * byte数组返回值转成String类型
     *
     * @param responseBody
     * @return
     */
    public static String byteArrayToString(byte[] responseBody) {
        String content = null;
        try {
            content = new String(responseBody, "UTF-8");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return content;
    }

    /**
     * @param phone 手机号
     * @return
     */
    public static String toPhone(String phone) {
        String string;
        if (phone.length() == 11) {
            string = phone.substring(0, 3);
            string += " " + phone.substring(3, 7);
            string += " " + phone.substring(7, 11);
            return string;
        }
        return phone;
    }

    public static String toBank(String bankNo) {
        return bankNo.replaceAll("\\d{4}(?!$)", "$0 ");
    }

    /**
     * @param phone 手机号
     * @return
     */
    public static String toPhoneEncryption(String phone) {
        String string;
        if (phone == null || phone.isEmpty()) {
            return phone;
        }
        if (phone.replaceAll(" ", "").length() == 11) {
            string = phone.substring(0, 3);
            string += " **** ";
            string += phone.substring(7, 11);
            return string;
        }
        return phone;
    }

    private static boolean matches(String phoneStr, Context context, String regex) {
        boolean b = false;
        try {
            Pattern p = null;
            Matcher m = null;
            if (!TextUtils.isEmpty(phoneStr)) {
                p = Pattern.compile(regex);
                m = p.matcher(phoneStr);
                b = m.matches();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (b) {
                //手机号是对的，那就存起来
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    private static final String[] regexArray = {
            "^(\\+?213|0)(5|6|7)\\d{8}$",
            "^(!?(\\+?963)|0)?9\\d{8}$",
            "^(!?(\\+?966)|0)?5\\d{8}$",
            "^(\\+?1)?[2-9]\\d{2}[2-9](?!11)\\d{6}$",
            "^(\\+?420)? ?[1-9][0-9]{2} ?[0-9]{3} ?[0-9]{3}$",
            "^(\\+?49[ \\.\\-])?([\\(]{1}[0-9]{1,6}[\\)])?([0-9 \\.\\-\\/]{3,20})((x|ext|extension)[ ]?[0-9]{1,4})?$",
            "^(\\+?45)?(\\d{8})$",
            "^(\\+?30)?(69\\d{8})$",
            "^(\\+?61|0)4\\d{8}$",
            "^(\\+?44|0)7\\d{9}$",
            "^(\\+?852\\-?)?[569]\\d{3}\\-?\\d{4}$",
            "^(\\+?91|0)?[789]\\d{9}$",
            "^(\\+?64|0)2\\d{7,9}$",
            "^(\\+?27|0)\\d{9}$",
            "^(\\+?26)?09[567]\\d{7}$",
            "^(\\+?34)?(6\\d{1}|7[1234])\\d{7}$",
            "^(\\+?358|0)\\s?(4(0|1|2|4|5)?|50)\\s?(\\d\\s?){4,8}\\d$",
            "^(\\+?33|0)[67]\\d{8}$",
            "^(\\+972|0)([23489]|5[0248]|77)[1-9]\\d{6}$",
            "^(\\+?36)(20|30|70)\\d{7}$",
            "^(\\+?39)?\\s?3\\d{2} ?\\d{6,7}$",
            "^(\\+?81|0)\\d{1,4}[ \\-]?\\d{1,4}[ \\-]?\\d{4}$",
            "^(\\+?6?01){1}(([145]{1}(\\-|\\s)?\\d{7,8})|([236789]{1}(\\s|\\-)?\\d{7}))$",
            "^(\\+?47)?[49]\\d{7}$",
            "^(\\+?32|0)4?\\d{8}$",
            "^(\\+?47)?[49]\\d{7}$",
            "^(\\+?48)? ?[5-8]\\d ?\\d{3} ?\\d{2} ?\\d{2}$",
            "^(\\+?55|0)\\-?[1-9]{2}\\-?[2-9]{1}\\d{3,4}\\-?\\d{4}$",
            "^(\\+?351)?9[1236]\\d{7}$",
            "^(\\+?7|8)?9\\d{9}$",
            "^(\\+3816|06)[- \\d]{5,9}$",
            "^(\\+?90|0)?5\\d{9}$",
            "^(\\+?84|0)?((1(2([0-9])|6([2-9])|88|99))|(9((?!5)[0-9])))([0-9]{7})$",
            "^(\\+?0?86\\-?)?1[345789]\\d{9}$",
            "^(\\+?886\\-?|0)?9\\d{8}$"
    };

    public static double toDouble(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0.00;
        }
        if (str.endsWith(".")) {
            str = str.substring(0, str.length() - 1);
        }
        if (!isNumber(str)) {
            return 0.00;
        }
        if (str.startsWith(".")) {
            str = "0.0";
        }
        return Double.parseDouble(str);
    }

    public static int toInt(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    public static long toLong(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0L;
        }
        return Long.parseLong(str);
    }

    /**
     * 身份证号脱敏
     */
    public static String idCardDesensitization(String idCard) {
        return dataDesensitization(idCard);
    }

    /**
     * 通用数据脱敏，左右两侧各保留四位，中间*
     * 小于等于8位则完整显示
     */
    public static String dataDesensitization(String data) {
        if (TextUtils.isEmpty(data)) {
            return data;
        }
        if (data.length() <= 8) {
            return data;
        }
        return dataDesensitization(4, 4, data.length() - 8, false, data);
    }

    /**
     * 手机号脱敏
     */
    public static String phoneDesensitization(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return "";
        }
        return dataDesensitization(3, 4, 4, false, phone);
    }

    /**
     * 姓名脱敏
     */
    public static String nameDesensitization(String name) {
        if (TextUtils.isEmpty(name)) {
            return "";
        }
        if (name.length() <= 1) {
            return name;
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        return name.charAt(0) + "*" + name.substring(name.length() - 1);
    }

    /**
     * 字符串脱敏
     *
     * @param frontLength       前边显示长度
     * @param backLength        后边显示长度
     * @param placeholderLength 中间占位符长度
     * @param isShowBlank       占位符两边是否显示空格
     * @param data              需要脱敏的数据
     * @return
     */
    public static String dataDesensitization(int frontLength, int backLength, int placeholderLength,
                                             boolean isShowBlank, String data) {
        if (TextUtils.isEmpty(data)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (data.length() <= frontLength + backLength) {
            return data;
        }

        sb.append(data.substring(0, frontLength));
        if (isShowBlank) {
            sb.append(" ");
        }
        for (int i = 0; i < placeholderLength; i++) {
            sb.append("*");
        }
        if (isShowBlank) {
            sb.append(" ");
        }
        sb.append(data.substring(data.length() - backLength));
        return sb.toString();
    }

    /**
     * 是否是连续数字的字符串（如：111111，123456）
     *
     * @param str
     * @return
     */
    public static boolean isEqualNumStr(String str) {
        if (str.length() > 1) {
            for (int i = 0; i < str.length() - 1; i++) {
                int num = Integer.parseInt(str.charAt(i) + "") - Integer.parseInt(str.charAt(i + 1) + "");
                if (num != 0) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }

    }

    /**
     * 是否是连续数字的字符串（如：111111，123456）
     *
     * @param str
     * @return
     */
    public static boolean isContinuousNumStr(String str) {
        if (str.length() > 1) {
            int num1 = Integer.parseInt(str.charAt(0) + "") - Integer.parseInt(str.charAt(1) + "");
            for (int i = 0; i < str.length() - 1; i++) {
                int num2 = Integer.parseInt(str.charAt(i) + "") - Integer.parseInt(str.charAt(i + 1) + "");
                if (num1 != 1 && num1 != -1) {
                    return false;
                }
                if (num1 == 1 && num2 != 1) {
                    return false;
                } else if (num1 == -1 && num2 != -1) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }

    }

    /**
     * 数字与小写字母混编字符串
     *
     * @param size
     * @return
     */
    public static String getNumSmallLetter(int size) {
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            if (random.nextInt(2) % 2 == 0) {//字母
                buffer.append((char) (random.nextInt(27) + 'a'));
            } else {//数字
                buffer.append(random.nextInt(10));
            }
        }
        return buffer.toString();
    }

    /**
     * 判断两个字符串中是否存在共同的字符串
     *
     * @param str1
     * @param str2
     * @param count 共同存在的字符串长度
     * @return
     */
    public static boolean isSubCommonStr(String str1, String str2, int count) {
        for (int i = 0; i <= str2.length() - count; i++) {
            if (str1.contains(str2.substring(i, i + count))) {
                return true;
            }
        }
        return false;
    }

    /**
     * double转string（去掉小数末尾的0）
     *
     * @param d
     * @return
     */
    public static String double2String(double d) {
//        try {
//            DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
//            return decimalFormat.format(d);
//        } catch (Exception e) {
//            return "0";
//        }
        return d + "";
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 获取不带空格的字符串
     * @param spaceStr
     * @return
     */
    public static String getNoSpaceStr(String spaceStr) {
        if (isEmpty(spaceStr)) {
            return "";
        }
        return spaceStr.replaceAll(" ", "");
    }

    /**
     * 字符串转大写
     * @param str
     * @return
     */
    public static String toUpperCase(String str) {
        if (isEmpty(str)) {
            return "";
        }
        return str.toUpperCase();
    }
}
