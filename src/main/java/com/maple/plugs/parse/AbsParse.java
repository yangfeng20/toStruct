package com.maple.plugs.parse;

import com.intellij.psi.*;
import com.maple.plugs.ClassTypeMappingEnum;
import com.maple.plugs.constant.ConstantString;
import com.maple.plugs.entity.ClassNameGroup;
import com.maple.plugs.entity.DescStruct;
import com.maple.plugs.loader.BizClassLoader;
import com.maple.plugs.log.StructLog;
import com.maple.plugs.search.ClassSearcher;
import com.maple.plugs.search.DefaultClassSearcher;
import com.maple.plugs.spi.EmptyTypeParameterList;
import com.maple.plugs.utils.ClassNameGroupConverter;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author yangfeng
 * @date : 2023/3/14 13:31
 * desc:
 */

public abstract class AbsParse implements Parse {


    private final ClassSearcher classSearcher = new DefaultClassSearcher();

    /**
     * 字段递归解析计数
     */
    protected final Map<String, DescStruct> fieldDescStructMap = new HashMap<>();

    protected Integer fieldRecursionLimit = 2;


    public void clearCacheMap() {
        fieldDescStructMap.clear();
        classSearcher.clear();
    }

    @Override
    public DescStruct parseClass(PsiClass psiClass, List<ClassNameGroup> genericList) {
        DescStruct descStruct = new DescStruct();

        // 获取全选定类名，并转换为包装类型
        String qualifiedName = ClassTypeMappingEnum.baseTypeToPackageType(psiClass.getQualifiedName());

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
        /*
         *         字段类型组（当前为文件的真实文本【字段类型和字段类型的泛型】）
         *         public class Entity<T> {
         *
         *             private T data;
         *             private List<T> result;
         *
         *         }
         *       data字段：ClassNameGroup(className=T, innerClassNameList=null)
         *       data字段：ClassNameGroup(className=List, innerClassNameList=T)
         */
        ClassNameGroup fieldTypeGroupDefine = ClassNameGroupConverter.convert(fieldTypeText);
        if (CollectionUtils.isNotEmpty(genericList) && genericList.stream().map(ClassNameGroup::getClassName)
                .anyMatch(item -> Objects.equals(item, fieldTypeGroupDefine.getClassName()))) {
            // 当前字段是泛型，同时没有指定泛型
            return fieldDescStruct;
        }

        // 构建当前类的泛型定义和实际类型映射
        HashMap<String, ClassNameGroup> genericMap = new HashMap<>(16);
        PsiTypeParameter[] currentClassParameterList = Optional.ofNullable(currentClass.getTypeParameterList()).orElse(new EmptyTypeParameterList()).getTypeParameters();
        for (int i = 0; i < currentClassParameterList.length; i++) {
            String classGenericDefine = currentClassParameterList[i].getText();
            String genericTrueClassName = genericList.get(i).getClassName();
            // note 如果当前类的泛型定义和字段的泛型定义一致，使用真实类型替换泛型
            if (!Objects.equals(classGenericDefine, genericTrueClassName)) {
                StructLog.getLogger().info("当前【 " + key + " 】泛型类与真实类的映射关系: 【" + classGenericDefine + " ---> " + genericTrueClassName + " 】");
                genericMap.put(classGenericDefine, genericList.get(i));
            }


            // note 如果当前类的泛型定义和字段的【类型】的泛型定义一致，使用真实类型替换泛型
            for (ClassNameGroup fieldClassParameter : Optional.ofNullable(fieldTypeGroupDefine.getInnerClassNameList()).orElse(Collections.emptyList())) {
                if (Objects.equals(classGenericDefine, fieldClassParameter.getClassName())) {
                    String fieldGenericTrueClassName = genericList.get(i).getClassName();
                    if (!Objects.equals(classGenericDefine, fieldGenericTrueClassName)) {
                        StructLog.getLogger().info("当前【 " + key + " 】泛型类与真实类的映射关系: 【" + classGenericDefine + " ---> " + fieldGenericTrueClassName + " 】");
                        genericMap.put(classGenericDefine, genericList.get(i));
                    }
                }

            }

        }

        // 获取当前字段的真实类型组
        // note 1.能从map中获取到的是泛型定义和字段一致 class Entity<T>{private T data;}
        // note 2.不能从map中获取，并且通过遍历字段泛型得到字段 class Entity<T>{private List<T> data;}
        // note 3.不能从map中获取，并且通过遍历字段泛型获取的类型为空，表示是直接写出的泛型 class Entity<T>{private List<Student> data;}
        ClassNameGroup fieldTypeTrueNameGroup = genericMap.computeIfAbsent(fieldTypeGroupDefine.getClassName(),
                genericKey -> {
                    // note case 2
                    List<ClassNameGroup> fieldGenericTrueNameGroup = Optional.ofNullable(fieldTypeGroupDefine.getInnerClassNameList())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(fieldTypeGeneric -> genericMap.get(fieldTypeGeneric.getClassName()))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    // note case 3
                    if (CollectionUtils.isEmpty(fieldGenericTrueNameGroup)) {
                        fieldGenericTrueNameGroup = fieldTypeGroupDefine.getInnerClassNameList();
                    }
                    return new ClassNameGroup(genericKey, fieldGenericTrueNameGroup);
                });

        // 获取字段类型映射
        ClassTypeMappingEnum typeMappingEnum = ClassTypeMappingEnum.getByClassName(fieldTypeTrueNameGroup.getClassName());
        String fieldTypeFullName = typeMappingEnum.getFullClassName();
        fieldDescStruct.setJavaType(fieldTypeFullName);

        // 数组类型问题
        if (CollectionUtils.isEmpty(fieldTypeTrueNameGroup.getInnerClassNameList()) && fieldTypeTrueNameGroup.getClassName().endsWith("[]")) {
            fieldTypeTrueNameGroup.setInnerClassNameList(Collections.singletonList(new ClassNameGroup(fieldTypeFullName, null)));
        }

        // 设置映射类型并添加的缓存提前暴露
        fieldDescStruct.setType(typeMappingEnum.getDesc());
        fieldDescStructMap.put(key, fieldDescStruct);

        switch (typeMappingEnum) {
            case null_:
                StructLog.getLogger().warn("类名为空");
                break;
            case array:
                List<ClassNameGroup> innerClassNameList = fieldTypeTrueNameGroup.getInnerClassNameList();
                if (CollectionUtils.isNotEmpty(innerClassNameList)) {
                    ClassNameGroup nameGroup = innerClassNameList.get(0);
                    PsiClass itemClassSearchResult = classSearcher.findFirst(nameGroup.getClassName());
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
                PsiClass propertyClassSearchResult = classSearcher.findFirst(fieldTypeTrueNameGroup.getClassName());
                if (Objects.nonNull(propertyClassSearchResult)) {
                    fieldDescStruct.setProperties(parseClass0(propertyClassSearchResult, fieldTypeTrueNameGroup.getInnerClassNameList()));
                } else {
                    StructLog.getLogger().warn("对象的泛型未搜索到结果：" + fieldTypeTrueNameGroup.getClassName());
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
