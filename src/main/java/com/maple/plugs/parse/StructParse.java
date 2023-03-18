package com.maple.plugs.parse;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;

/**
 * @author yangfeng
 * @date : 2023/3/14 10:28
 * desc:
 */

public class StructParse extends AbsParse {

    @Override
    protected PsiAnnotation parseFieldAnnotation(PsiField psiField, String annotationName) {
        return super.parseFieldAnnotation(psiField, annotationName);
    }
}
