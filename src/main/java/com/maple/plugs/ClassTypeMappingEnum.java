package com.maple.plugs;

import com.maple.plugs.constant.ConstantString;
import com.maple.plugs.loader.BizClassLoader;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

/**
 * @author yangfeng
 * @date : 2023/3/12 20:08
 * desc:
 */


public enum ClassTypeMappingEnum {

    /**
     * 类型映射
     */
    null_("", null, ""),
    string("string", new Class[]{String.class, Date.class, Character.class}, ""),
    int_("int", new Class[]{Integer.class, Byte.class, Short.class}, ""),
    long_("long", new Class[]{Long.class}, ""),
    bigDecimal("number", new Class[]{BigDecimal.class, Float.class, Double.class}, ""),
    boolean_("boolean", new Class[]{Boolean.class}, ""),
    array("array", new Class[]{Collection.class}, ""),
    object("object", new Class[]{Object.class}, ""),

    ;


    private final String desc;

    private final Class<?>[] classArr;

    private String fullClassName;

    public String getFullClassName() {
        String result = this.fullClassName;
        this.fullClassName = "";
        return result;
    }

    public void setFullClassName(String name) {
        this.fullClassName = name;
    }

    public String getDesc() {
        return desc;
    }


    ClassTypeMappingEnum(String desc, Class<?>[] classArr, String className) {
        this.desc = desc;
        this.classArr = classArr;
        this.fullClassName = className;
    }

    public static String baseTypeToPackageType(String type) {
        if (Objects.isNull(type)) {
            return "";
        }
        switch (type) {
            case "byte":
                return Byte.class.getName();
            case "short":
                return Short.class.getName();
            case "int":
                return Integer.class.getName();
            case "long":
                return Long.class.getName();
            case "float":
                return Float.class.getName();
            case "double":
                return Double.class.getName();
            case "boolean":
                return Boolean.class.getName();
            case "char":
                return Character.class.getName();
            default:
                return type;
        }
    }

    public static ClassTypeMappingEnum getByClass(Class<?> clazz) {
        if (clazz == null) {
            return object;
        }
        for (ClassTypeMappingEnum item : values()) {
            if (item.classArr == null) {
                continue;
            }
            for (Class<?> self : item.classArr) {
                if (self.isAssignableFrom(clazz)) {
                    return item;
                }
            }
        }

        return null_;
    }

    public static ClassTypeMappingEnum getByClassName(String fullClassName) {
        if (StringUtils.isEmpty(fullClassName)) {
            return null_;
        }
        // 数组类型
        if (fullClassName.endsWith(ConstantString.ARRAY_TYPE_TAG)) {
            ClassTypeMappingEnum array = ClassTypeMappingEnum.array;
            String itemTypeFullName = baseTypeToPackageType(fullClassName.substring(0, fullClassName.length() - 2));
            array.setFullClassName(itemTypeFullName);
            return array;
        }

        Class<?> clazz = BizClassLoader.loadClassByJdk(fullClassName);
        // 基本类型或者集合类型
        if (clazz != null) {
            for (ClassTypeMappingEnum item : values()) {
                if (item.classArr == null) {
                    continue;
                }
                for (Class<?> self : item.classArr) {
                    if (self.isAssignableFrom(clazz)) {
                        item.setFullClassName(clazz.getName());
                        return item;
                    }
                }
            }
        }

        return object;
    }
}
