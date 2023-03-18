package com.maple.plugs.view;

import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.ide.util.gotoByName.ChooseByNameViewModel;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author yangfeng
 * @date : 2023/3/2 1:14
 * desc: 按照名称选择弹窗框
 * <p>
 * 主要包含【类转换模型】以及当前【project】
 */


public class StructChooseByNameViewModel implements ChooseByNameViewModel {

    @NotNull
    private ChooseByNameModel model;

    @NotNull
    private Project ideaProject;

    public StructChooseByNameViewModel() {
    }

    public StructChooseByNameViewModel(@NotNull ChooseByNameModel model, @NotNull Project ideaProject) {
        this.model = model;
        this.ideaProject = ideaProject;
    }

    @Override
    public Project getProject() {
        return ideaProject;
    }

    @Override
    public @NotNull ChooseByNameModel getModel() {
        return model;
    }

    @Override
    public boolean isSearchInAnyPlace() {
        return model.useMiddleMatching();
    }

    @Override
    public @NotNull String transformPattern(@NotNull String pattern) {
        return ChooseByNamePopup.getTransformedPattern(pattern, model);
    }

    @Override
    public boolean canShowListForEmptyPattern() {
        return false;
    }

    @Override
    public int getMaximumListSizeLimit() {
        return 0;
    }


    public void setModel(@NotNull ChooseByNameModel model) {
        this.model = model;
    }

    public Project getIdeaProject() {
        return ideaProject;
    }

    public void setIdeaProject(@NotNull Project ideaProject) {
        this.ideaProject = ideaProject;
    }
}
