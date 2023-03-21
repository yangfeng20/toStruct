package com.maple.plugs.entity;

import java.util.List;

/**
 * @author yangfeng
 * @date : 2023/3/18 12:07
 * desc:
 */

public class ClassNameGroup {


    private String className;

    private List<ClassNameGroup> innerClassNameList;

    public ClassNameGroup() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<ClassNameGroup> getInnerClassNameList() {
        return innerClassNameList;
    }

    public void setInnerClassNameList(List<ClassNameGroup> innerClassNameList) {
        this.innerClassNameList = innerClassNameList;
    }


    public String toString1() {
        return "ClassNameGroup{" +
                "className='" + className + '\'' +
                ", innerClassNameList=" + innerClassNameList +
                '}';
    }
    @Override
    public String toString() {
        return "{" +
                "\"className\"=\"" + className + '\"' +
                ", \"innerClassNameList\"=" + innerClassNameList +
                '}';
    }
}
