package com.maple.plugs.parse;

import com.alibaba.fastjson.JSONObject;
import com.maple.plugs.utils.reflect.ReflectUtil;

/**
 * @author yangfeng
 * @date : 2023/3/14 10:28
 * desc:
 */

public class StructParse extends AbsParse {

    private final JSONObject psiFieldResult = new JSONObject();


    /**
     * 解析
     * 需要获取的数据为字段名，字段类型，字段描述
     *
     * @param psiField psiField
     */
    @Override
    public void parsePsiField(Object psiField) {

        // 字段名
        String fieldName = ReflectUtil.invokeResultType(psiField, "getName", String.class);

        // 字段描述
        Object desc = super.parseFieldAnnotation(psiField, "ApiModelProperties");

        // 字段类型
        Object type = super.parseFieldType(psiField);

        // 如果字段是对象，循环解析


        JSONObject fieldJson = new JSONObject();
        fieldJson.put("desc", desc);
        fieldJson.put("type", type);

        psiFieldResult.put(fieldName, fieldJson);
    }

    @Override
    public Object getParsePsiFieldResult() {
        return psiFieldResult.toJSONString();
    }
}
