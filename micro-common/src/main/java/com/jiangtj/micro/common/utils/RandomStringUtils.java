package com.jiangtj.micro.common.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生成随机字符串工具类
 */
public class RandomStringUtils {
    private static final String src_number = "0123456789";
    private static final String src_lower = "abcdefghijklmnopqrstuvwxyz";
    private static final String src_upper = src_lower.toUpperCase();
    private static final String src_hex_lower = "0123456789abcdef";
    private static final String src_hex_upper = src_hex_lower.toUpperCase();
    private static final String esc_char = "?";

    private static final Object locker = new Object();

    public static String get(int size) {
        StringBuilder r = new StringBuilder(size);
        String src = src_number + src_upper;
        for (int i = 0; i < size; i++) {
            r.append(getRandomChar(src));
        }
        return r.toString();
    }

    public static String get(String format) {
        StringBuilder r = new StringBuilder(format.length());
        String src = src_number + src_upper;
        for (int i = 0; i < format.length(); i++) {
            String curr = String.valueOf(format.charAt(i));
            if (curr.equalsIgnoreCase(esc_char)) {
                r.append(getRandomChar(src));
            } else {
                r.append(curr);
            }
        }
        return r.toString();
    }

    public static String get(String format, char esc) {
        StringBuilder r = new StringBuilder(format.length());
        String src = src_number + src_upper;
        for (int i = 0; i < format.length(); i++) {
            String curr = String.valueOf(format.charAt(i));
            if (curr.equalsIgnoreCase(String.valueOf(esc))) {
                r.append(getRandomChar(src));
            } else {
                r.append(curr);
            }
        }
        return r.toString();
    }

    public static String getNum(int size) {
        StringBuilder r = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            r.append(getRandomChar(src_number));
        }
        return r.toString();
    }

    public static String getNum(String format) {
        StringBuilder r = new StringBuilder(format.length());
        for (int i = 0; i < format.length(); i++) {
            String curr = String.valueOf(format.charAt(i));
            if (curr.equalsIgnoreCase(esc_char)) {
                r.append(getRandomChar(src_number));
            } else {
                r.append(curr);
            }
        }
        return r.toString();
    }

    public static String getNum(String format, char esc) {
        StringBuilder r = new StringBuilder(format.length());
        for (int i = 0; i < format.length(); i++) {
            String curr = String.valueOf(format.charAt(i));
            if (curr.equalsIgnoreCase(String.valueOf(esc))) {
                r.append(getRandomChar(src_number));
            } else {
                r.append(curr);
            }
        }
        return r.toString();
    }

    public static String getHex(int size) {
        StringBuilder r = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            r.append(getRandomChar(src_hex_upper));
        }
        return r.toString();
    }

    public static String getHex(String format) {
        StringBuilder r = new StringBuilder(format.length());
        for (int i = 0; i < format.length(); i++) {
            String curr = String.valueOf(format.charAt(i));
            if (curr.equalsIgnoreCase(esc_char)) {
                r.append(getRandomChar(src_hex_upper));
            } else {
                r.append(curr);
            }
        }
        return r.toString();
    }

    public static String getHex(String format, char esc) {
        StringBuilder r = new StringBuilder(format.length());
        for (int i = 0; i < format.length(); i++) {
            String curr = String.valueOf(format.charAt(i));
            if (curr.equalsIgnoreCase(String.valueOf(esc))) {
                r.append(getRandomChar(src_hex_upper));
            } else {
                r.append(curr);
            }
        }
        return r.toString();
    }

    private static String getRandomChar(String src) {
        if (null == src || src.isEmpty()) {
            return "";
        }
        return String.valueOf((src.charAt((int) (Math.random() * src.length()))));
    }

    /**
     * 唯一码生成器 返回值26位 数字字符串（yyyyMMddHHmmssSSS+11位随机数）
     */
    public static String getNextVal() {
        synchronized (locker) {
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            return formatDate.format(new Date()) +
                Math.round(Math.random() * 899999999 + 100000000);
        }
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     */
    public static List<String> subStringByLength(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return subStringByLengthAndSize(inputString, length, size);
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     */
    public static List<String> subStringByLengthAndSize(String inputString, int length,
                                                        int size) {
        List<String> list = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length,
                (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    /**
     * 分割字符串，如果开始位置大于字符串长度，返回空
     */
    public static String substring(String str, int f, int t) {
        if (f >= str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f);
        } else {
            return str.substring(f, t);
        }
    }
}
