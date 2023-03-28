package com.maple.plugs.parse;

import com.intellij.psi.*;
import com.maple.plugs.ClassTypeMappingEnum;
import com.maple.plugs.GenericMapCache;
import com.maple.plugs.constant.ConstantString;
import com.maple.plugs.constant.ContextKeyConstant;
import com.maple.plugs.entity.ClassNameGroup;
import com.maple.plugs.entity.DescStruct;
import com.maple.plugs.loader.BizClassLoader;
import com.maple.plugs.log.StructLog;
import com.maple.plugs.search.ClassSearcher;
import com.maple.plugs.search.DefaultClassSearcher;
import com.maple.plugs.utils.ClassNameGroupConverter;
import com.maple.plugs.utils.ThreadContext;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * @author yangfeng
 * @date : 2023/3/14 13:31
 * desc:
 */

public abstract class AbsParse implements Parse {


    private final ClassSearcher classSearcher = new DefaultClassSearcher();

    private final Map<String, String> paramTypeMap = new HashMap<>();

    /**
     * 字段递归解析计数
     */
    protected final Map<String, DescStruct> fieldDescStructMap = new HashMap<>();

    protected Integer fieldRecursionLimit = 2;


    public void clearCacheMap() {
        paramTypeMap.clear();
        fieldDescStructMap.clear();
        classSearcher.clear();
    }

    @Override
    public DescStruct parseClass(PsiClass psiClass, List<ClassNameGroup> genericList) {
        DescStruct descStruct = new DescStruct();

        // 获取全选定类名，并转换为包装类型
        String qualifiedName = ClassTypeMappingEnum.baseTypeToPackageType(psiClass.getQualifiedName());

        // 如果有泛型参数，构建泛型和真实类的映射
        paramTypeMap.put(qualifiedName, (String) ThreadContext.get(ContextKeyConstant.PARAM_TYPE));

        // 如果普通类加载器能加载，说明是jdk中的类【基本数据类型或者集合】
        Class<?> loadClassByJdk = BizClassLoader.loadClassByJdk(qualifiedName);
        if (Objects.nonNull(loadClassByJdk)) {
            descStruct.setType(ClassTypeMappingEnum.getByClass(loadClassByJdk).getDesc());
            descStruct.setJavaType(qualifiedName);
            return descStruct;
        }

        // 不能加载，业务对象类型
        descStruct.setType(ClassTypeMappingEnum.object.name());
        descStruct.setProperties(parseClass0(psiClass, genericList));

        // 结构描述对象后置处理
        descObjectPostHandler(descStruct);
        return descStruct;
    }

    protected void descObjectPostHandler(@NotNull DescStruct descStruct) {
        this.clearCacheMap();
    }

    private Map<String, DescStruct> parseClass0(PsiClass psiClass, List<ClassNameGroup> genericList) {
        if (CollectionUtils.isNotEmpty(genericList) && "?".equals(genericList.get(0).getClassName())) {
            genericList = null;
        }
        PsiField[] psiFields = psiClass.getAllFields();
        Map<String, DescStruct> fieldStructMap = new HashMap<>((int) (psiFields.length / 0.75) + 1);
        for (PsiField psiField : psiFields) {
            // 过滤静态字段
            boolean isStatic = psiField.hasModifierProperty(PsiModifier.STATIC);
            if (isStatic) {
                continue;
            }
            String fieldName = psiField.getName();
            DescStruct fieldDescStruct = parseField(psiField, psiClass, genericList);
            fieldStructMap.put(fieldName, fieldDescStruct);
        }
        return fieldStructMap;
    }

    protected DescStruct parseField(PsiField psiField, PsiClass currentClass, List<ClassNameGroup> genericList) {
        DescStruct fieldDescStruct = new DescStruct();

        // 防止自身引用自身，解决无限递归
        String key = currentClass.getQualifiedName() + ConstantString.DIT + psiField.getName();
        DescStruct descStruct = fieldDescStructMap.get(key);
        if (descStruct != null) {
            return descStruct.clone();
        }

        // 获取字段描述
        PsiAnnotation apiModelProperties = parseFieldAnnotation(psiField, ConstantString.API_MODEL_PROPERTIES);
        if (Objects.nonNull(apiModelProperties)) {
            // 字段注解的属性对象
            PsiAnnotationMemberValue fieldAnnotationAttr = apiModelProperties.findAttributeValue(ConstantString.VALUE);
            Optional.ofNullable(fieldAnnotationAttr).ifPresent(self -> {
                Object attrValue = ((PsiLiteralExpression) (self)).getValue();
                fieldDescStruct.setDescription((String) attrValue);
            });
        }

        // 获取类名组（包含泛型，并且都是全类名）
        PsiType fieldType = psiField.getType();
        String fieldTypeText = ClassTypeMappingEnum.baseTypeToPackageType(fieldType.getCanonicalText());
        ClassNameGroup classNameGroup = ClassNameGroupConverter.convert(fieldTypeText);
        // 点击光标所在处有多级泛型
        if (CollectionUtils.isEmpty(classNameGroup.getInnerClassNameList()) && CollectionUtils.isNotEmpty(genericList)){
            classNameGroup.setInnerClassNameList(genericList.get(0).getInnerClassNameList());
        }
        if (CollectionUtils.isNotEmpty(genericList) && genericList.stream().map(ClassNameGroup::getClassName)
                .anyMatch(item -> Objects.equals(item, classNameGroup.getClassName()))) {
            // 当前字段是泛型，同时没有指定泛型
            return fieldDescStruct;
        }


        // 构建自身引用的泛型
        PsiTypeParameterList typeParameterList = currentClass.getTypeParameterList();
        if (Objects.nonNull(typeParameterList) && typeParameterList.getTypeParameters().length > 0) {
            int count = 0;
            for (PsiTypeParameter typeParameter : typeParameterList.getTypeParameters()) {
                if (Objects.equals(typeParameter.getText(), classNameGroup.getClassName()) && CollectionUtils.isNotEmpty(genericList)) {
                    classNameGroup.setClassName(genericList.get(count).getClassName());
                    break;
                }
                count++;
            }
        }

        // 获取字段类型映射
        ClassTypeMappingEnum typeMappingEnum = ClassTypeMappingEnum.getByClassName(classNameGroup.getClassName());
        String fieldTypeFullName = typeMappingEnum.getFullClassName();
        // 数组类型问题
        if (CollectionUtils.isEmpty(classNameGroup.getInnerClassNameList()) && classNameGroup.getClassName().endsWith("[]")){
            classNameGroup.setInnerClassNameList(Collections.singletonList(new ClassNameGroup(fieldTypeFullName, null)));
        }

        // 设置映射类型并添加的缓存提前暴露
        fieldDescStruct.setType(typeMappingEnum.getDesc());
        fieldDescStructMap.put(key, fieldDescStruct);

        Function<String, String> getParamMapping = GenericMapCache::get;

        switch (typeMappingEnum) {
            case null_:
                StructLog.getLogger().warn("类名为空");
                break;
            case array:
                List<ClassNameGroup> innerClassNameList = classNameGroup.getInnerClassNameList();
                if (CollectionUtils.isNotEmpty(innerClassNameList)) {
                    ClassNameGroup nameGroup = innerClassNameList.get(0);
                    PsiClass itemClassSearchResult = classSearcher.findFirst(getParamMapping.apply(nameGroup.getClassName()));
                    if (Objects.nonNull(itemClassSearchResult)) {
                        fieldDescStruct.setItems(parseClass(itemClassSearchResult, nameGroup.getInnerClassNameList()));
                    } else {
                        // 可能为【?】item类型为Object
                        DescStruct emptyItems = new DescStruct();
                        emptyItems.setType("object");
                        fieldDescStruct.setItems(emptyItems);
                        StructLog.getLogger().warn("集合的泛型未搜索到结果：" + nameGroup.getClassName());
                    }
                }
                break;
            case object:
                PsiClass propertyClassSearchResult = classSearcher.findFirst(getParamMapping.apply(classNameGroup.getClassName()));
                if (Objects.nonNull(propertyClassSearchResult)) {
                    fieldDescStruct.setProperties(parseClass0(propertyClassSearchResult, classNameGroup.getInnerClassNameList()));
                } else {
                    StructLog.getLogger().warn("对象的泛型未搜索到结果：" + classNameGroup.getClassName());
                }
                break;
            default:

                break;
        }

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
