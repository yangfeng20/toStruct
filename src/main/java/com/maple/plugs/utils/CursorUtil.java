package com.maple.plugs.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.maple.plugs.entity.ClassNameGroup;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * @author yangfeng
 * @date : 2023/3/2 22:17
 * desc:
 */

public class CursorUtil {


    public static String getCursorText() {
        // 光标所在PsiElement
        PsiElement cursorPsiElement = getCursorPsiElement();

        // 光标最近泛型字符串
        String cursorGenericStr = getCursorGenericStr(cursorPsiElement);
        ClassNameGroup classNameGroup = ClassNameGroupConverter.convert(cursorGenericStr);

        // 构建泛型映射
        //GenericMapCache.buildMapping(classNameGroup, null);
        return cursorPsiElement.getText();
    }

    public static ClassNameGroup getCursorClassGroup() {
        // 光标所在PsiElement
        PsiElement cursorPsiElement = getCursorPsiElement();

        // 光标最近泛型字符串
        String cursorGenericStr = getCursorGenericStr(cursorPsiElement);
        return ClassNameGroupConverter.convert(cursorGenericStr);
    }

    public static String getCursorGenericStr(PsiElement psiElement) {
        String lineStr = getLineEnd(psiElement);
        int lastIndexOf = lineStr.lastIndexOf(">");
        lineStr = lineStr.substring(0, lastIndexOf + 1);
        return lineStr.replaceFirst(".*?[extends|implements]\\s+", "");
    }

    public static String getLineEnd(PsiElement psiElement) {
        AtomicReference<Function<PsiElement, String>> reference = new AtomicReference<>();
        Function<PsiElement, String> buildCache = element -> {
            if (element == null) {
                return "";
            }
            String text = element.getText();
            if (text.contains("\n")) {
                return text.substring(0, text.indexOf("\n"));
            }
            return text + reference.get().apply(element.getNextSibling());
        };
        reference.set(buildCache);
        return buildCache.apply(psiElement);
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


    public static String getCursorTypeText() {
        PsiElement cursorElement = getCursorPsiElement();
        if (cursorElement == null) {
            return "";
        }

        // 获取到光标所在的父元素
        PsiElement parentElement = PsiTreeUtil.getParentOfType(cursorElement, PsiTypeParameterListOwner.class);
        if (parentElement instanceof PsiMethod) {
            ((PsiMethod) parentElement).getParameterList();
        }
        if (parentElement instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) parentElement;

            // 获取到当前光标所在位置的PsiField或者PsiMethod对象
            Optional<PsiField> fieldOptional = Optional.ofNullable(PsiTreeUtil.getParentOfType(cursorElement, PsiField.class));
            Optional<PsiMethod> methodOptional = Optional.ofNullable(PsiTreeUtil.getParentOfType(cursorElement, PsiMethod.class));
            PsiType psiType;
            if (fieldOptional.isPresent()) {
                psiType = fieldOptional.get().getType();
            } else if (methodOptional.isPresent()) {
                PsiMethod psiMethod = methodOptional.get();
                AnActionEvent event = (AnActionEvent) ThreadContext.get("event");
                Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
                int curOffset = editor.getCaretModel().getOffset();
                int paramIndex = -1;
                for (PsiParameter parameter : psiMethod.getParameterList().getParameters()) {
                    int startOffset = parameter.getTextRange().getStartOffset();
                    int endOffset = parameter.getTextRange().getEndOffset();
                    if (startOffset <= curOffset && curOffset <= endOffset) {
                        paramIndex = Arrays.asList(psiMethod.getParameterList().getParameters()).indexOf(parameter);
                        break;
                    }
                }
                psiType = psiMethod.getParameterList().getParameters()[paramIndex].getType();
            } else {
                return "";
            }

            // 处理泛型信息，返回处理结果
            if (psiType instanceof PsiClassType) {
                PsiClassType classType = (PsiClassType) psiType;
                PsiType[] types = classType.getParameters();
                StringBuilder sb = new StringBuilder();
                for (PsiType type : types) {
                    if (type != null) {
                        sb.append(type.getCanonicalText());
                    }
                }
                return sb.toString();
            }
        }

        return "";
    }

}
