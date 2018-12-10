package com.zkml.terminal.service.selfterminal.util;

/**
 * @Author: likun
 * @Date: Created in  2018/12/10 10:41
 * @Description:公共实用类
 */
public class CommonUtil {
    private static final String hexString = "0123456789ABCDEF";

    public static String convertStriToHexStr(String str) {

        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    public static String formatStr(String str) {

        if (null != str && !"".equals(str)) {
            return str.toString().replaceAll(" ", "").trim();
        }
        return "";
    }

    public static String getStrByGiveDigit(String hexStr, int digit) {

        int len = hexStr.length();
        StringBuilder builder = new StringBuilder("");
        if (len < digit) {
            for (int i = 0; i < digit - len; i = i + 1) {
                builder.append("0");
            }
        }
        builder.append(hexStr);
        return builder.toString();
    }

    public static Integer formatInt(String intStr) {

        if (null != intStr && !"".equals(intStr) && intStr.matches("^[0-9]*$")) {
            return Integer.parseInt(intStr);
        }
        return -1;
    }

    public static String getHexStrByLong(long longVal) {

        return Long.toHexString(longVal);
    }
}
