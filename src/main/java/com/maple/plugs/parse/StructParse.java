package com.maple.plugs.parse;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import com.maple.plugs.entity.DescStruct;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author yangfeng
 * @date : 2023/3/14 10:28
 * desc:
 */

public class StructParse extends AbsParse {

    protected boolean ignoreJavaType;

    public StructParse() {
    }

    public StructParse(boolean ignoreJavaType) {
        this.ignoreJavaType = ignoreJavaType;
    }

    @Override
    protected PsiAnnotation parseFieldAnnotation(PsiField psiField, String annotationName) {
        return super.parseFieldAnnotation(psiField, annotationName);
    }

    @Override
    protected void descObjectPostHandler(@NotNull DescStruct descStruct) {
        if (!ignoreJavaType) {
            return;
        }

        // 删除JavaType值
        AtomicReference<Consumer<DescStruct>> reference = new AtomicReference<>();
        Consumer<DescStruct> removeJavaTypeValue = self -> {
            self.setJavaType(null);
            if (Objects.nonNull(self.getItems())) {
                reference.get().accept(self.getItems());
            }
            Optional.ofNullable(self.getProperties()).ifPresent(map -> {
                map.values().forEach(reference.get());
            });
        };
        reference.set(removeJavaTypeValue);
        removeJavaTypeValue.accept(descStruct);
    }
}
