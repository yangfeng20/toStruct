package com.maple.plugs.search;

import com.intellij.ide.util.gotoByName.DefaultChooseByNameItemProvider;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.util.Processor;
import com.maple.plugs.utils.CursorUtil;
import com.maple.plugs.utils.ThreadContext;
import com.maple.plugs.view.StructChooseByNameViewModelPlus;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author yangfeng
 * @date : 2023/3/2 1:46
 * desc:
 */

public class DefaultClassSearcher implements ClassSearcher {

    private final Map<String, PsiClass> psiClassMap = new HashMap<>();

    public DefaultClassSearcher() {
    }

    public DefaultClassSearcher(int limit) {
        DefaultClassSearcher.limit = limit;
    }

    private static Integer limit;

    @Override
    public List<PsiClass> search(String name) {

        PsiClass psiClass = psiClassMap.get(name);
        if (Objects.nonNull(psiClass)) {
            return Collections.singletonList(psiClass);
        }

        Project ideaProject = (Project) ThreadContext.get("project");
        PsiElement psiElement = CursorUtil.getCursorPsiElement();

        List<PsiClass> result = new ArrayList<>();
        Processor<Object> processor = param -> {
            result.add((PsiClass) param);
            return result.size() < limit;
        };

        // 如果超过限制，将被limit中断，同时返回false
        boolean isAll = DefaultChooseByNameItemProvider.filterElements(new StructChooseByNameViewModelPlus(ideaProject), name,
                true, new ProgressIndicatorBase(false), psiElement, processor);

        if (CollectionUtils.isNotEmpty(result)) {
            psiClassMap.put(name, result.get(0));
        }
        return result;
    }

    @Override
    public @Nullable
    PsiClass findFirst(String name) {
        List<PsiClass> resultList = search(name);
        if (CollectionUtils.isEmpty(resultList)) {
            return null;
        }

        return resultList.get(0);
    }

    @Override
    public void clear() {
        psiClassMap.clear();
    }
}
