package com.maple.plugs.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangfeng
 * @date : 2023/3/18 10:34
 * desc:
 */


public class DescStruct implements Cloneable {

    private String type;

    private String description;

    private Map<String, DescStruct> properties;

    private DescStruct items;

    private String javaType;


    public DescStruct() {
    }

    @Override
    public DescStruct clone() {
        try {
            DescStruct cloned = (DescStruct) super.clone();
            if (cloned.properties != null) {
                cloned.properties = new HashMap<>((int) (properties.size() / 0.75) + 1);
                for (Map.Entry<String, DescStruct> entry : properties.entrySet()) {
                    cloned.properties.put(entry.getKey(), entry.getValue().clone());
                }
            }
            if (cloned.items != null) {
                cloned.items = cloned.items.clone();
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
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
