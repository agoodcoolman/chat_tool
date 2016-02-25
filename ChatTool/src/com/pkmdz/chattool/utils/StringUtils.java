package com.pkmdz.chattool.utils;

/**
 * Created by Administrator on 2015/8/7.
 */
public class StringUtils {
    /**
     * 判断是否为空
     * @param targetString
     * @return
     */
    public static boolean isBlank(String targetString) {
        if (targetString == null) {
            return true;
        }

        if ("".equals(targetString)) {
            return true;
        }

        return false;
    }
}
