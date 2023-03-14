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
    public static <T> T invokeNoArg(Object target, String methodName, Class<T> returnClass) {
        return (T) invoke(target, methodName, null);
    }

    public static Object invokeNoArg(Object target, String methodName) {
        return invoke(target, methodName, null);
    }


    /**
     * @param target
     * @param methodName
     * @param args
     * @param parameterTypes
     * @return
     */
    public static Object invoke(Object target, String methodName, Object[] args, Class<?>... parameterTypes) {
        if (Objects.isNull(args)) {
            args = new Object[]{};
        }
        Class<?> clazz = target.getClass();
        Method targetMethod;
        try {
            targetMethod = clazz.getDeclaredMethod(methodName, parameterTypes);
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
