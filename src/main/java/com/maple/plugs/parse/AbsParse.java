package com.maple.plugs.parse;

import com.maple.plugs.utils.reflect.ReflectUtil;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yangfeng
 * @date : 2023/3/14 13:31
 * desc:
 */

public abstract class AbsParse implements Parse {

    protected Object parseFieldAnnotation(Object psiField, String annotationName) {
        return parseFieldAnnotation(psiField, annotationName, "value");
    }

    protected Object parseFieldAnnotation(Object psiField, String annotationName, String annotationKey) {

        AtomicReference<Object> result = new AtomicReference<>();

        // 获取字段修饰符
        Object psiFieldModifier = ReflectUtil.invoke(psiField, "getModifierList");
        if (Objects.isNull(psiFieldModifier)) {
            return null;
        }
        // get annotation
        Object annotationList = ReflectUtil.invoke(psiFieldModifier, "getAnnotations");
        if (Objects.isNull(annotationList)) {
            return null;
        }
        ReflectUtil.arrayForEach(annotationList, annotation -> {
            if (!Objects.equals(annotationName, ReflectUtil.invoke(annotation, "getShortName"))) {
                return;
            }
            // 获取注解的某个属性
            Object attributeValue = ReflectUtil.invoke(annotation, "findAttributeValue", annotationKey);
            if (Objects.isNull(attributeValue)) {
                return;
            }
            // 获取属性的value值 annotation.value()
            result.set(ReflectUtil.invoke(attributeValue, "getValue"));
        });

        return result.get();
    }

    protected Object parseFieldType(Object psiField) {
        Object result = null;

        // 获取字段类型psi
        Object psiType = ReflectUtil.invoke(psiField, "getType");
        // 全类名
        String fullTypeName = ReflectUtil.invokeResultType(psiType, "getInternalCanonicalText", String.class);
        Class<?> typeClass = null;
        try {
            typeClass = Thread.currentThread().getContextClassLoader().loadClass(fullTypeName);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // 当前类能被加载，说明是jdk中的类型
        if (typeClass != null) {
            boolean isNumberType = Number.class.isAssignableFrom(typeClass);
            boolean isStr = String.class.isAssignableFrom(typeClass);
            if (isNumberType || isStr) {
                result = typeClass.getSimpleName();
            }
            // 获取参数化类型
            boolean isList = Collection.class.isAssignableFrom(typeClass);
        } else {
            result = ReflectUtil.invokeResultType(psiType, "getClassName", String.class);
        }

        return result;
    }
}
