package com.maple.plugs.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangfeng
 * @date : 2023/3/2 2:11
 * desc:
 */

public class ThreadContext {

    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL_MAP = ThreadLocal.withInitial(HashMap::new);


    public static void init(AnActionEvent event) {
        putIfAbsent("event", event);
        putIfAbsent("project", event.getProject());
    }

    public static Object get(String key) {
        Map<String, Object> localMap = THREAD_LOCAL_MAP.get();
        return localMap.get(key);
    }

    public static void put(String key, Object val) {
        Map<String, Object> localMap = THREAD_LOCAL_MAP.get();
        localMap.put(key, val);
    }

    public static void putIfAbsent(String key, Object val) {
        Map<String, Object> localMap = THREAD_LOCAL_MAP.get();
        localMap.putIfAbsent(key, val);
    }

    public static void clear(){
        THREAD_LOCAL_MAP.remove();
    }
}
