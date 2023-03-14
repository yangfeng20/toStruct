package com.maple.plugs.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.maple.plugs.constant.ContextKeyConstant;
import com.maple.plugs.search.ClassSearcher;
import com.maple.plugs.search.DefaultClassSearcher;
import com.maple.plugs.utils.ClipboardHandler;
import com.maple.plugs.utils.CursorUtil;
import com.maple.plugs.utils.Notifier;
import com.maple.plugs.utils.ThreadContext;
import com.maple.plugs.utils.reflect.ReflectUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


        Map<String, Object> filedMap = new HashMap<>();
        Consumer<Object> addResult = item -> {
            // todo 需要获取的数据为字段名，字段类型，字段描述
            String fieldName = ReflectUtil.invokeNoArg(item, "getName", String.class);

            Object annotations = ReflectUtil.invokeNoArg(item, "getAnnotations");
            Object desc=null;
            if (annotations!=null){
                Object getAnnotations = ((Object[]) annotations)[0];
                desc = ReflectUtil.invoke(getAnnotations, "findAttributeValue", new String[]{"value"}, String.class);
            }

            Object valueType = null;
            Object psiType = ReflectUtil.invokeNoArg(item, "getType");
            String typeVal = ReflectUtil.invokeNoArg(psiType, "getClassName", String.class);

            String typeVal1 = item.toString().split(":")[1];


            JSONObject jsonObject = new JSONObject();
            JSONObject inner = new JSONObject();
            inner.put("desc", desc);
            inner.put("type", typeVal1);
            jsonObject.put(fieldName, inner);

            filedMap.put(fieldName, inner);
        };

        for (Object item : result) {
            Object fields = ReflectUtil.invokeNoArg(item, "getAllFields");
            if (fields == null) {
                throw new IllegalArgumentException("没有getFields方法");
            }
            ReflectUtil.arrayForEach(fields, addResult);
        }


        // 剪切板
        ClipboardHandler.copyToClipboard(JSON.toJSONString(filedMap));
        // 通知
        Notifier.notifyInfo("Convert " + cursorText + " to Struct", project);
        ThreadContext.clear();

    }
}
