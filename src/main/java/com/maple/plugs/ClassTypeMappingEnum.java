package com.maple.plugs;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * @author yangfeng
 * @date : 2023/3/12 20:08
 * desc:
 */


public enum ClassTypeMappingEnum {

    /**
     * 类型映射
     */
    null_("", null),
    string("string", String.class),
    int_("int", Integer.class),
    long_("long", Long.class),
    bigDecimal("number", BigDecimal.class),
    boolean_("boolean", Boolean.class),
    array("array", Collection.class),


    ;


    private final String desc;

    private final Class<?> clazz;

    public String getDesc() {
        return desc;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    ClassTypeMappingEnum(String desc, Class<?> clazz) {
        this.desc = desc;
        this.clazz = clazz;
    }


    public static ClassTypeMappingEnum getByClass(Class<?> clazz) {
        for (ClassTypeMappingEnum item : values()) {
            if (item.clazz== null){
                continue;
            }
            if (item.clazz.isAssignableFrom(clazz)) {
                return item;
            }
        }

        return null_;
    }
}
