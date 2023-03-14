package com.maple.plugs.loader;

/**
 * @author yangfeng
 * @date : 2023/3/2 3:36
 * desc:
 */

public class BizClassLoader extends ClassLoader{
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        // 拿到目标java文件的class路径
        return super.findClass(name);
    }
}
