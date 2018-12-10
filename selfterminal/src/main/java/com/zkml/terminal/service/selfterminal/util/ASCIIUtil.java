package com.zkml.terminal.service.selfterminal.util;

public class ASCIIUtil {
    public static String convertStringToHex(String str) {

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }

        return hex.toString();
    }

    public static String convertHexASCToStr(String hexASC) {

        StringBuilder sb = new StringBuilder("");
        int len = hexASC.length();
        for (int i = 0; i < len - 1; i += 2) {

            String output = hexASC.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
        }
        return sb.toString();
    }

    public static String checkingCode(String commandCode) {
        long sum = 0;
        if (null != commandCode && !"".equals(commandCode)) {

            String[] hexStr = commandCode.replaceAll("(.{2})", "$1 ").split(" ");
            if (null != hexStr && hexStr.length > 0) {

                sum = Long.valueOf(hexStr[0], 16);
                for (int i = 1; i < hexStr.length; i++) {
                    sum ^= Long.valueOf(hexStr[i], 16);
                }
            }
        }
        String checkCode = Long.toHexString(sum).toUpperCase();
        checkCode = checkCode.length() < 2 ? "0" + checkCode : checkCode;
        return checkCode;
    }
}
