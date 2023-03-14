package com.maple.plugs.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;

/**
 * @author yangfeng
 * @date : 2023/3/2 22:17
 * desc:
 */

public class CursorUtil {

    public static String getCursorText() {
        return getCursorPsiElement().getText();
    }

    public static PsiElement getCursorPsiElement() {
        AnActionEvent event = (AnActionEvent) ThreadContext.get("event");
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        return getCursorPsiFile().findElementAt(editor.getCaretModel().getOffset());
    }

    public static PsiFile getCursorPsiFile() {
        AnActionEvent event = (AnActionEvent) ThreadContext.get("event");
        Project ideaProject = (Project) ThreadContext.get("project");
        VirtualFile virtualFile = event.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
        return PsiUtilBase.getPsiFile(ideaProject, virtualFile);
    }
}
