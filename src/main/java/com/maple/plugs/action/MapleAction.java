package com.maple.plugs.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author maple
 */
public class MapleAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        JBPopupFactory instance = JBPopupFactory.getInstance();
        // 添加一个菜单，点击显示文字
        JBPopup popup = instance.createMessage("Hello world");
        popup.showInBestPositionFor(e.getDataContext());
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        FileType fileType = psiFile.getFileType();


        // 创建异步写入程序
        WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
            // do ...
        });


    }
}
