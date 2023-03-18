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
    string("string", new Class[]{String.class, Date.class}, ""),
    int_("int", new Class[]{Integer.class}, ""),
    long_("long", new Class[]{Long.class}, ""),
    bigDecimal("number", new Class[]{BigDecimal.class}, ""),
    boolean_("boolean", new Class[]{Boolean.class}, ""),
    array("array", new Class[]{Collection.class}, ""),
    object("object", new Class[]{Object.class}, ""),

    ;


    private final String desc;

    private final Class<?>[] classArr;

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


    ClassTypeMappingEnum(String desc, Class<?>[] classArr, String className) {
        this.desc = desc;
        this.classArr = classArr;
        this.fullClassName = className;
    }

    public static boolean isBaseType(ClassTypeMappingEnum classTypeMappingEnum) {
        return classTypeMappingEnum != ClassTypeMappingEnum.object && classTypeMappingEnum != ClassTypeMappingEnum.array
                && classTypeMappingEnum != ClassTypeMappingEnum.null_;
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
}
