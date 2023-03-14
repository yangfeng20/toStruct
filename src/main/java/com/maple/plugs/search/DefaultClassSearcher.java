package com.maple.plugs.search;

import com.intellij.ide.util.gotoByName.DefaultChooseByNameItemProvider;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.Processor;
import com.maple.plugs.view.StructChooseByNameViewModelPlus;
import com.maple.plugs.utils.CursorUtil;
import com.maple.plugs.utils.ThreadContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangfeng
 * @date : 2023/3/2 1:46
 * desc:
 */

public class DefaultClassSearcher implements ClassSearcher {
    @Override
    public List<Object> search(String name) {

        Project ideaProject = (Project) ThreadContext.get("project");
        PsiElement psiElement = CursorUtil.getCursorPsiElement();

        List<Object> result = new ArrayList<>();
        Processor<Object> processor = param -> {
            result.add(param);
            return true;
        };


        boolean success = DefaultChooseByNameItemProvider.filterElements(new StructChooseByNameViewModelPlus(ideaProject), name,
                true, new ProgressIndicatorBase(false), psiElement, processor);

        return result;
    }
}