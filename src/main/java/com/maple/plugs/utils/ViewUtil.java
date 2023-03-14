package com.maple.plugs.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.maple.plugs.constant.ContextKeyConstant;

/**
 * @author yangfeng
 * @date : 2023/3/2 22:30
 * desc:
 */

public class ViewUtil {

    public static void showText(Object content) {
        JBPopupFactory instance = JBPopupFactory.getInstance();
        // 添加一个菜单，点击显示文字
        JBPopup popup = instance.createMessage(String.valueOf(content));
        Editor editor = ((AnActionEvent) (ThreadContext.get(ContextKeyConstant.EVENT))).getRequiredData(CommonDataKeys.EDITOR);
        popup.showInBestPositionFor(editor);
    }
}
