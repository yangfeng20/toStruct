package com.maple.plugs.entity;

import java.util.Map;

/**
 * @author yangfeng
 * @date : 2023/3/18 10:34
 * desc:
 */


public class DescStruct {

    private String type;

    private String description;

    private Map<String,DescStruct> properties;

    private DescStruct items;

    private String javaType;


    public DescStruct() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, DescStruct> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, DescStruct> properties) {
        this.properties = properties;
    }

    public DescStruct getItems() {
        return items;
    }

    public void setItems(DescStruct items) {
        this.items = items;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }
}
