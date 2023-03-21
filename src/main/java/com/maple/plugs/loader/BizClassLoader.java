package com.maple.plugs.loader;

import com.maple.plugs.log.StructLog;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yangfeng
 * @date : 2023/3/2 3:36
 * desc:
 */

public class BizClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        // 拿到目标java文件的class路径
        return super.findClass(name);
    }

    public static Class<?> loadClassByJdk(String className) {
        if (StringUtils.isEmpty(className)) {
            return null;
        }

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Class<?> clazz = null;
        try {
            clazz = contextClassLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            StructLog.getLogger().printStackTrace(e);
        }
        return clazz;
    }
}
