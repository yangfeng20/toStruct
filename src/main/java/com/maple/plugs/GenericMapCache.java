package com.maple.plugs;

import com.intellij.psi.PsiElement;
import com.maple.plugs.entity.ClassNameGroup;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yangfeng
 * @date : 2023/3/20 13:44
 * desc:
 */

public class GenericMapCache {

    private static final Pattern PATTERN = Pattern.compile("\\s+(\\w+)\\s*<\\s*(\\w+)\\s*>");


    private static final Map<String, String> GENERIC_MAP = new HashMap<>();


    public static String get(String genericRealTypeName) {
        return GENERIC_MAP.getOrDefault(genericRealTypeName, genericRealTypeName);
    }

    public static void buildMapping(ClassNameGroup classNameGroup, ClassNameGroup parent) {
        AtomicReference<BiConsumer<ClassNameGroup, ClassNameGroup>> reference = new AtomicReference<>();
        BiConsumer<ClassNameGroup, ClassNameGroup> buildCache = (current, p) -> {
            String className = current.getClassName();
            List<ClassNameGroup> innerClassNameList = current.getInnerClassNameList();
            if (StringUtils.isEmpty(className) || CollectionUtils.isEmpty(innerClassNameList)) {
                return;
            }
            String key = p == null ? current.getClassName() : p + current.getClassName();
            GENERIC_MAP.put(key, innerClassNameList.get(0).getClassName());
            innerClassNameList.forEach(a -> {
                reference.get().accept(a, current);
            });
        };
        reference.set(buildCache);
        buildCache.accept(classNameGroup, parent);
    }

    public static void buildMapping(PsiElement psiElement) {
        if (psiElement == null) {
            return;
        }

        AtomicReference<Consumer<PsiElement>> reference = new AtomicReference<>();
        Consumer<PsiElement> buildCache = element -> {
            if (element == null) {
                return;
            }
            Pair<String, String> pair = parseGeneric(element.getText());
            if (pair != null) {
                GENERIC_MAP.put(pair.getKey(), pair.getValue());
            }
            PsiElement nextElement = element.getNextSibling();
            reference.get().accept(nextElement);
        };
        reference.set(buildCache);
        buildCache.accept(psiElement);
    }

    private static Pair<String, String> parseGeneric(String text) {
        // extends UserEntity<User> 获取结果：<UserEntity,User>
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.find()) {
            return Pair.of(matcher.group(1), matcher.group(2));
        }
        return null;
    }
}
