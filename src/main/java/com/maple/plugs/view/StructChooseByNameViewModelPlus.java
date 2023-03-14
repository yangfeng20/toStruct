package com.maple.plugs.view;

import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.GotoClassModel2;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author yangfeng
 * @date : 2023/3/2 1:14
 * desc: 按照名称选择弹窗框
 * <p>
 * 主要包含【类转换模型】以及当前【project】
 */


public class StructChooseByNameViewModelPlus extends StructChooseByNameViewModel {

    public StructChooseByNameViewModelPlus(@NotNull ChooseByNameModel model, @NotNull Project ideaProject) {
        super(model, ideaProject);
    }


    public StructChooseByNameViewModelPlus(AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project)) {
            throw new RuntimeException("获取project失败,可能为key错误");
        }
        setModel(new GotoClassModel2(project));
        setIdeaProject(project);
    }

    public StructChooseByNameViewModelPlus(@NotNull Project project) {
        setModel(new GotoClassModel2(project));
        setIdeaProject(project);
    }

}
