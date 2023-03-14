package com.maple.plugs;

/**
 * @author yangfeng
 * @date : 2023/3/12 20:08
 * desc:
 */


public enum ClassTypeMappingEnum {

    /**
     * 类型映射
     */
    int_("int", Integer.class),
    string("string", String.class),
    long_("long", Long.class),
    ;


    private String desc;

    private Class<?> clazz;

    ClassTypeMappingEnum(String desc, Class<?> clazz) {
        this.desc = desc;
        this.clazz = clazz;
    }
}
