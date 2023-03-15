package com.maple.plugs.action;


import com.google.gson.GsonBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.maple.plugs.constant.ContextKeyConstant;
import com.maple.plugs.parse.Parse;
import com.maple.plugs.parse.StructParse;
import com.maple.plugs.search.ClassSearcher;
import com.maple.plugs.search.DefaultClassSearcher;
import com.maple.plugs.utils.ClipboardHandler;
import com.maple.plugs.utils.CursorUtil;
import com.maple.plugs.utils.Notifier;
import com.maple.plugs.utils.ThreadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author maple
 */
public class MapleToStruct extends AnAction {

    static class ClassTest{
        private String name;

        private Integer age;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 三种方案
        // 1.拿到文件的语法树，直接解析语法树
        // 2.拿到class文件的字符串，内存编译class对象，并通过classLoader加载
        // 3.拼接classes地址，直接加载类的class文件路径，加载class文件

        final GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();

        // 初始化线程上下文
        ThreadContext.init(e);
        Project project = (Project) ThreadContext.get(ContextKeyConstant.PROJECT);

        ClassSearcher classSearcher = new DefaultClassSearcher();

        String cursorText = CursorUtil.getCursorText();
        List<Object> result = classSearcher.search(cursorText);


        // 解析搜索到的PsiClass
        Parse structParse = new StructParse();


        // PSI转换为json
        Object resultJson = structParse.parseClass(result.get(0));

        // 剪切板
        String jsonStr = gsonBuilder.create().toJson(resultJson);
        ClipboardHandler.copyToClipboard(jsonStr);
        // 通知
        Notifier.notifyInfo("Convert " + cursorText + " to Struct", project);
        ThreadContext.clear();

    }
}
