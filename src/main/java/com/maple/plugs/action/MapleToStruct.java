package com.maple.plugs.action;


import com.google.gson.GsonBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
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

    ClassSearcher classSearcher = new DefaultClassSearcher(10);

    final GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 三种方案
        // 1.拿到文件的语法树，直接解析语法树
        // 2.拿到class文件的字符串，内存编译class对象，并通过classLoader加载
        // 3.拼接classes地址，直接加载类的class文件路径，加载class文件


        // todo 后续使用异步模型
        ApplicationManager.getApplication().executeOnPooledThread(()->{

        });

        // 初始化线程上下文
        ThreadContext.init(e);
        Project project = (Project) ThreadContext.get(ContextKeyConstant.PROJECT);

        try {
            String cursorText = CursorUtil.getCursorText();
            List<PsiClass> result = classSearcher.search(cursorText);

            // 解析搜索到的PsiClass
            Parse structParse = new StructParse(true);

            // PSI转换为json
            Object resultJson = structParse.parseClass(result.get(0));
            String jsonStr = gsonBuilder.create().toJson(resultJson);

            // 剪切板
            ClipboardHandler.copyToClipboard(jsonStr);
            // 通知
            Notifier.notifyInfo("ToStruct Convert " + cursorText + " to Struct", project);
            ThreadContext.clear();
        } catch (Exception exception) {
            exception.printStackTrace();
            Notifier.notifyError("ToStruct 发生异常", project);
        } finally {
            ThreadContext.clear();
        }

    }
}
