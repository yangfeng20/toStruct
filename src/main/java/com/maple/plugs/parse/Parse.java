package com.maple.plugs.parse;

import com.intellij.psi.PsiClass;
import com.maple.plugs.entity.DescStruct;

/**
 * @author yangfeng
 * @date : 2023/3/14 10:29
 * desc:
 */

public interface Parse {

    /**
     * 解析类
     *
     * @param psiClass psi类
     */
    DescStruct parseClass(PsiClass psiClass);
}
