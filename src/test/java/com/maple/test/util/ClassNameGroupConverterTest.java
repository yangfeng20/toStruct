package com.maple.test.util;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.maple.plugs.entity.ClassNameGroup;
import com.maple.plugs.utils.ClassNameGroupConverter;
import org.junit.jupiter.api.Test;

/**
 * @author yangfeng
 * @date : 2023/3/18 12:30
 * desc:
 */

public class ClassNameGroupConverterTest extends LightJavaCodeInsightFixtureTestCase {


    @Test
    public void convertTest() {
        String genericString = "java.util.List<java.util.Map<java.lang.String, java.util.List<java.util.Map<java.lang.String, java.lang.String>>>>";
        ClassNameGroup classNameGroup = ClassNameGroupConverter.convert(genericString);
        System.out.println(classNameGroup);

        String genericString1 = "Request<InnerParseResult<InnerPackageFileResult>>";
        ClassNameGroup classNameGroup1 = ClassNameGroupConverter.convert(genericString1);
        System.out.println(classNameGroup1);
    }
}
