package com.maple.plugs.parse;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.maple.plugs.ClassTypeMappingEnum;
import com.maple.plugs.constant.ConstantString;
import com.maple.plugs.entity.ClassNameGroup;
import com.maple.plugs.entity.DescStruct;
import com.maple.plugs.loader.BizClassLoader;
import com.maple.plugs.search.ClassSearcher;
import com.maple.plugs.search.DefaultClassSearcher;
import com.maple.plugs.utils.ClassNameGroupConverter;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yangfeng
 * @date : 2023/3/14 13:31
 * desc:
 */

public abstract class AbsParse implements Parse {


    private final ClassSearcher classSearcher = new DefaultClassSearcher();

    @Override
    public DescStruct parseClass(PsiClass psiClass) {
        DescStruct descStruct = new DescStruct();

        String qualifiedName = psiClass.getQualifiedName();
        Class<?> loadClassByJdk = BizClassLoader.loadClassByJdk(qualifiedName);
        if (Objects.nonNull(loadClassByJdk)) {
            descStruct.setType(ClassTypeMappingEnum.getByClass(loadClassByJdk).getDesc());
            descStruct.setJavaType(qualifiedName);
            return descStruct;
        }
        descStruct.setType(ClassTypeMappingEnum.object.name());
        descStruct.setProperties(parseClass0(psiClass));
        return descStruct;
    }

    private Map<String, DescStruct> parseClass0(PsiClass psiClass) {
        PsiField[] psiFields = psiClass.getAllFields();
        Map<String, DescStruct> fieldStructMap = new HashMap<>((int) (psiFields.length / 0.75) + 1);
        for (PsiField psiField : psiFields) {
            String fieldName = psiField.getName();
            DescStruct fieldDescStruct = parseField(psiField);
            fieldStructMap.put(fieldName, fieldDescStruct);
        }
        return fieldStructMap;
    }

    protected DescStruct parseField(PsiField psiField) {
        DescStruct fieldDescStruct = new DescStruct();

        // 获取字段描述
        PsiAnnotation apiModelProperties = parseFieldAnnotation(psiField, ConstantString.API_MODEL_PROPERTIES);
        if (Objects.nonNull(apiModelProperties)) {
            // 字段注解的属性对象
            PsiAnnotationMemberValue fieldAnnotationAttr = apiModelProperties.findAttributeValue(ConstantString.VALUE);
            Optional.ofNullable(fieldAnnotationAttr).ifPresent(self -> {
                Object attrValue = ((PsiLiteralExpressionImpl) (self)).getValue();
                fieldDescStruct.setDescription((String) attrValue);
            });
        }

        // 获取类名组（包含泛型，并且都是全类名）
        PsiType fieldType = psiField.getType();
        String classNameGroupStr = fieldType.getInternalCanonicalText();

        ClassNameGroup classNameGroup = ClassNameGroupConverter.convert(classNameGroupStr);
        Class<?> loaderJdkClass = BizClassLoader.loadClassByJdk(classNameGroup.getClassName());
        ClassTypeMappingEnum classTypeMappingEnum = ClassTypeMappingEnum.getByClass(loaderJdkClass);
        if (Objects.nonNull(loaderJdkClass)) {
            // 是java中的类型【一般为基本数据类型】
            fieldDescStruct.setJavaType(classNameGroup.getClassName());
            fieldDescStruct.setType(classTypeMappingEnum.getDesc());

            // 如果是集合类型
            if (ClassTypeMappingEnum.array.equals(classTypeMappingEnum)) {
                String listInnerParamTypeName = classNameGroup.getInnerClassNameList().get(0).getClassName();
                List<PsiClass> paramTypeSearchResult = classSearcher.search(listInnerParamTypeName);
                fieldDescStruct.setItems(parseClass(paramTypeSearchResult.get(0)));
            }
            return fieldDescStruct;
        }

        // 如果是对象类型
        if (ClassTypeMappingEnum.object.equals(classTypeMappingEnum)) {
            List<PsiClass> bizClassSearchResultList = classSearcher.search(classNameGroup.getClassName());
            fieldDescStruct.setProperties(parseClass0(bizClassSearchResultList.get(0)));
        }

        fieldDescStruct.setType(classTypeMappingEnum.getDesc());
        return fieldDescStruct;
    }


    protected PsiAnnotation parseFieldAnnotation(PsiField psiField, String annotationName) {
        PsiModifierList modifierList = psiField.getModifierList();
        if (Objects.isNull(modifierList)) {
            return null;
        }

        PsiAnnotation[] annotations = modifierList.getAnnotations();
        return Arrays.stream(annotations)
                .filter(item -> {
                    AtomicBoolean hasAnnotation = new AtomicBoolean(false);
                    Optional.ofNullable(item.getQualifiedName()).ifPresent(self -> {
                        String[] split = self.split("\\.");
                        String shortAnnotationName = split[split.length - 1];
                        hasAnnotation.set(Objects.equals(shortAnnotationName, annotationName));
                    });
                    return hasAnnotation.get();
                })
                .findAny()
                .orElse(null);
    }
}
