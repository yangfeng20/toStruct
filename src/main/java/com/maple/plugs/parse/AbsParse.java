package com.maple.plugs.parse;

import com.google.gson.JsonObject;
import com.maple.plugs.ClassTypeMappingEnum;
import com.maple.plugs.constant.PsiMethodEnum;
import com.maple.plugs.log.StructLog;
import com.maple.plugs.search.ClassSearcher;
import com.maple.plugs.search.DefaultClassSearcher;
import com.maple.plugs.utils.reflect.ReflectUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yangfeng
 * @date : 2023/3/14 13:31
 * desc:
 */

public abstract class AbsParse implements Parse {

    @Override
    public Object parseClass(Object psiClass) {
        JsonObject result = new JsonObject();
        final JsonObject psiFieldResult = new JsonObject();
        Object fields = ReflectUtil.invoke(psiClass, PsiMethodEnum.getAllFields.name());
        if (fields == null) {
            throw new IllegalArgumentException("没有getFields方法");
        }

        ClassTypeMappingEnum typeMappingEnum = getPsiType(psiClass);
        if (ClassTypeMappingEnum.isBaseType(typeMappingEnum)) {
            psiFieldResult.addProperty("type", typeMappingEnum.getDesc());
            return psiFieldResult;
        }

        result.addProperty("type", typeMappingEnum.getDesc());
        // 解析每个字段
        ReflectUtil.arrayForEach(fields, psiField->{
            Pair<String, JsonObject> pair = parsePsiField(psiField);
            psiFieldResult.add(pair.getKey(), pair.getValue());
        });

        result.add("properties", psiFieldResult);
        return result;
    }


    /**
     * 解析
     * 需要获取的数据为字段名，字段类型，字段描述
     *
     * @param psiField psiField
     */
    @Override
    public Pair<String, JsonObject> parsePsiField(Object psiField) {
        JsonObject fieldJson = new JsonObject();

        // 字段名
        String fieldName = ReflectUtil.invokeResultType(psiField, "getName", String.class);

        // 字段描述
        Object desc = parseFieldAnnotation(psiField, "ApiModelProperties");

        // 字段类型
        ClassTypeMappingEnum fieldType = getPsiType(psiField);

        String objectKey = "";
        if (ClassTypeMappingEnum.array.equals(fieldType)) {
            objectKey = "items";
        } else if (ClassTypeMappingEnum.object.equals(fieldType)) {
            objectKey = "properties";
        }
        // 字段是对象或者list，递归构建json
        if (StringUtils.isNotBlank(objectKey)) {
            // 搜索字段并解析PsiClass
            ClassSearcher classSearcher = new DefaultClassSearcher();
            Object psiClass = classSearcher.search(fieldType.getFullClassName()).get(0);
            Object innerJson = parseClass(psiClass);
            fieldJson.add(objectKey, (JsonObject)innerJson);
        }

        fieldJson.addProperty("description", (String) desc);
        fieldJson.addProperty("type", fieldType.getDesc());

        return Pair.of(fieldName, fieldJson);
    }

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

    /**
     * 获取Psi元素的对应java类型
     * 例如一个Student的类，他的PsiType就是Student
     *
     * @param psiElement psiClass or psiElement
     * @return {@link ClassTypeMappingEnum}
     */
    protected ClassTypeMappingEnum getPsiType(Object psiElement) {

        // 全类名
        String fullClassName = "";
        // 获取PsiField
        Object psiType = ReflectUtil.invoke(psiElement, "getType");
        if (psiType != null) {
            // 是PsiField
            fullClassName = ReflectUtil.invokeResultType(psiType, "getInternalCanonicalText", String.class);
        } else {
            // 如果是PsiClass
            fullClassName = (String) ReflectUtil.invoke(psiElement, "getQualifiedName");
        }

        // java.util.List<java.lang.Long>
        // 如果是参数化类型
        if (isParamType(fullClassName)) {
            String arrayClass = String.join(",", splitParamType(fullClassName));
            ClassTypeMappingEnum array = ClassTypeMappingEnum.array;
            array.setFullClassName(arrayClass.split(",")[1]);
            return array;
        }

        // 加载jdk class
        Class<?> typeClass = null;
        try {
            typeClass = Thread.currentThread().getContextClassLoader().loadClass(fullClassName);
        } catch (ClassNotFoundException e) {
            StructLog.getLogger().printStackTrace(e);
        }

        // 当前类能被加载，说明是jdk中的类型
        if (typeClass != null) {
            return ClassTypeMappingEnum.getByClass(typeClass);
        }

        // 其他默认为Object类型【需要递归解析】
        ClassTypeMappingEnum object = ClassTypeMappingEnum.object;
        object.setFullClassName(fullClassName);
        return object;
    }

    private boolean isParamType(String fullClassName){
        if (StringUtils.isBlank(fullClassName)){
            return false;
        }
        return fullClassName.contains("<");
    }

    private List<String> splitParamType(String fullClassName){
        //List<Map<String, List<Map<String,List<String>>>>>
    //    java.util.List<java.lang.Long>
        String[] result = fullClassName.split("<");
        result[1] = result[1].substring(0, result[1].length()-1);
        return Arrays.asList(result);
    }
}
