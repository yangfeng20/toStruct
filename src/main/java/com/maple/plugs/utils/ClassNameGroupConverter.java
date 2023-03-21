package com.maple.plugs.utils;

import com.maple.plugs.entity.ClassNameGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maple
 */
public class ClassNameGroupConverter {

    /**
     * 将泛型字符串转换为 ClassNameGroup 的树形结构.
     *
     * @param genericString 泛型字符串
     * @return ClassNameGroup 对象
     */
    public static ClassNameGroup convert(String genericString) {

        // 获取泛型类型参数列表
        List<String> parameterList = getParameterList(genericString);

        // 如果没有类型参数，直接返回当前类名
        if (parameterList.isEmpty()) {
            ClassNameGroup classNameGroup = new ClassNameGroup();
            classNameGroup.setClassName(genericString);
            return classNameGroup;
        }

        // 否则，当前类名为泛型类型名称，创建 ClassNameGroup 对象
        String className = getClassName(genericString);
        ClassNameGroup classNameGroup = new ClassNameGroup();
        classNameGroup.setClassName(className);

        // 递归处理每个类型参数
        List<ClassNameGroup> innerClassNameList = new ArrayList<>();
        for (String parameter : parameterList) {
            innerClassNameList.add(convert(parameter));
        }
        classNameGroup.setInnerClassNameList(innerClassNameList);

        return classNameGroup;
    }

    /**
     * 获取泛型类型参数列表
     *
     * @param genericString 泛型字符串
     * @return 泛型类型参数列表
     */
    private static List<String> getParameterList(String genericString) {

        List<String> parameterList = new ArrayList<>();

        int start = genericString.indexOf("<");
        int end = genericString.lastIndexOf(">");

        // 没有类型参数
        if (start == -1 || end == -1) {
            return parameterList;
        }

        // 获取类型参数列表
        String parameterString = genericString.substring(start + 1, end);
        int currentIndex = 0;
        int count = 0;

        for (int i = 0; i < parameterString.length(); i++) {
            char c = parameterString.charAt(i);
            if (c == '<') {
                count++;
            } else if (c == '>') {
                count--;
            } else if (c == ',' && count == 0) {
                parameterList.add(parameterString.substring(currentIndex, i).trim());
                currentIndex = i + 1;
            }
        }
        parameterList.add(parameterString.substring(currentIndex).trim());

        return parameterList;
    }

    /**
     * 获取泛型类型名称
     *
     * @param genericString 泛型字符串
     * @return 泛型类型名称
     */
    private static String getClassName(String genericString) {
        int endIndex = genericString.indexOf("<");
        return endIndex > 0 ? genericString.substring(0, endIndex).trim() : genericString.trim();
    }
}
