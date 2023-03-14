package com.maple.plugs.parse;

/**
 * @author yangfeng
 * @date : 2023/3/14 10:29
 * desc:
 */

public interface Parse {

    /**
     * 解析
     *
     * @param obj obj
     * @return {@link Object}
     */
    Object parsePsiField(Object obj);


    /**
     * 得到解析psi场结果
     *
     * @return {@link Object}
     */
    Object getParsePsiFieldResult();

    /**
     * 解析类
     *
     * @param psiClass psi类
     */
    Object parseClass(Object psiClass);
}
