package com.maple.test.entity;

import java.util.List;
import java.util.Map;

/**
 * @author yangfeng
 * @date : 2023/3/28 15:37
 * desc:
 */

public class GenericEntity<T> {

    List<Integer> listInt;

    List<GenericEntity<?>> listSelf;

    Map<String, String> mapSelf;

    T self;

    List<?> lists;
}
