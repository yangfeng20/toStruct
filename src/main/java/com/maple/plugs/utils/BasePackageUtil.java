package com.maple.plugs.utils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yangfeng
 * @date : 2023/4/20 0:04
 * desc:
 */

public class BasePackageUtil {

    private final static Map<String, Class<?>> PACKAGE_MAP;

    static {
        PACKAGE_MAP = Stream.of(String.class, Integer.class, Long.class, BigDecimal.class, List.class, Collection.class,
                Set.class, Map.class, ArrayList.class,HashMap.class
                ).collect(Collectors.toMap(Class::getSimpleName, Function.identity()));
    }


    public static Class<?> getPackageClass(String simpleName) {
        return PACKAGE_MAP.get(simpleName);
    }
}
