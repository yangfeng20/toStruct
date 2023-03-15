package com.maple.plugs;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

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
    string("string", String.class, ""),
    int_("int", Integer.class, ""),
    long_("long", Long.class, ""),
    bigDecimal("number", BigDecimal.class, ""),
    boolean_("boolean", Boolean.class, ""),
    array("array", Collection.class, ""),
    object("object", Object.class, ""),

    ;


    private final String desc;

    private final Class<?> clazz;

    private String fullClassName;

    public String getFullClassName() {
        return fullClassName;
    }

    public void setFullClassName(String name) {
        this.fullClassName = name;
    }

    public String getDesc() {
        return desc;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    ClassTypeMappingEnum(String desc, Class<?> clazz, String className) {
        this.desc = desc;
        this.clazz = clazz;
        this.fullClassName = className;
    }

    public static boolean isBaseType(ClassTypeMappingEnum classTypeMappingEnum) {
        return classTypeMappingEnum != ClassTypeMappingEnum.object && classTypeMappingEnum != ClassTypeMappingEnum.array
                && classTypeMappingEnum != ClassTypeMappingEnum.null_;
    }


    public static ClassTypeMappingEnum getByClass(Class<?> clazz) {
        for (ClassTypeMappingEnum item : values()) {
            if (item.clazz == null) {
                continue;
            }
            if (item.clazz.isAssignableFrom(clazz) || (item.equals(string) && Date.class.isAssignableFrom(clazz))) {
                return item;
            }
        }

        return null_;
    }
}
