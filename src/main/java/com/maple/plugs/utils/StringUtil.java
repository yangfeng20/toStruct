package com.maple.plugs.utils;

/**
 * @author yangfeng
 * @date : 2023/3/18 19:47
 * desc:
 */

public class StringUtil {

    public static String getFirstParamTypeName(String string) {
        if (!string.startsWith("<")) {
            return string;
        }

        String result = string.split("<")[1];
        return result.substring(0, result.length()-1);
    }
}
