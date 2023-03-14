package com.maple.plugs.utils.reflect;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author yangfeng
 * @date : 2023/3/2 21:50
 * desc:
 */

public class ReflectUtil {

    @SuppressWarnings("all")
    public static <T> T invokeResultType(Object target, String methodName, Class<T> returnClass) {
        return (T) invoke(target, methodName);
    }


    /**
     * @param target
     * @param methodName
     * @param args
     * @return
     */
    public static Object invoke(Object target, String methodName, Object ...args) {
        if (Objects.isNull(args)) {
            args = new Object[]{};
        }
        Class<?> clazz = target.getClass();
        Method targetMethod;
        try {
            targetMethod = clazz.getDeclaredMethod(methodName, getClasses(args));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }

        Object returnVal = null;
        targetMethod.setAccessible(true);
        try {
            returnVal = targetMethod.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return returnVal;
    }


    /**
     * 获得对象数组的类数组
     *
     * @param objects 对象数组，如果数组中存在{@code null}元素，则此元素被认为是Object类型
     * @return 类数组
     */
    public static Class<?>[] getClasses(Object... objects) {
        Class<?>[] classes = new Class<?>[objects.length];
        Object obj;
        for (int i = 0; i < objects.length; i++) {
            obj = objects[i];
            classes[i] = (null == obj) ? Object.class : obj.getClass();
        }
        return classes;
    }


    public static void listForEach(List<Object> iteration, Consumer<Object> action) {
        iteration.forEach(action);
    }

    public static void arrayForEach(@NotNull Object array, Consumer<Object> action) {
        Class<?> clazz = array.getClass();
        if (!clazz.isArray()) {
            throw new IllegalArgumentException("入参不是数组");
        }

        // 需要显式转换为数组，入参的才是数组
        listForEach(Arrays.asList((Object[]) array), action);
    }
}
