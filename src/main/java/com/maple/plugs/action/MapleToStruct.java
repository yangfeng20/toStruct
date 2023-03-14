package com.maple.plugs.action;

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
import com.maple.plugs.utils.reflect.ReflectUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author maple
 */
public class MapleToStruct extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 三种方案
        // 1.拿到文件的语法树，直接解析语法树
        // 2.拿到class文件的字符串，内存编译class对象，并通过classLoader加载
        // 3.拼接classes地址，直接加载类的class文件路径，加载class文件

        // 初始化线程上下文
        ThreadContext.init(e);
        Project project = (Project) ThreadContext.get(ContextKeyConstant.PROJECT);

        ClassSearcher classSearcher = new DefaultClassSearcher();

        String cursorText = CursorUtil.getCursorText();
        List<Object> result = classSearcher.search(cursorText);


        // 解析搜索到的PsiClass
        AtomicReference<String> parseResult = new AtomicReference<>();
        Parse structParse = new StructParse();
        Consumer<Object> addResult = structParse::parsePsiField;



        for (Object item : result) {
            Object fields = ReflectUtil.invoke(item, "getAllFields");
            if (fields == null) {
                throw new IllegalArgumentException("没有getFields方法");
            }
            ReflectUtil.arrayForEach(fields, addResult);
        }
        parseResult.set((String) structParse.getParsePsiFieldResult());

        // 剪切板
        ClipboardHandler.copyToClipboard(parseResult.get());
        // 通知
        Notifier.notifyInfo("Convert " + cursorText + " to Struct", project);
        ThreadContext.clear();

    }
}
