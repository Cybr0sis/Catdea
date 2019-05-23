/*
 * Copyright 2019 Cybrosis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cybrosis.catdea.navigator;

import com.android.tools.idea.navigator.AndroidProjectViewPane;
import com.cybrosis.catdea.icons.CatdeaIcons;
import com.intellij.ide.SelectInTarget;
import com.intellij.ide.impl.ProjectViewSelectInTarget;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.ProjectAbstractTreeStructureBase;
import com.intellij.ide.projectView.impl.ProjectTreeStructure;
import com.intellij.ide.projectView.impl.ProjectViewPane;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CatdeaProjectViewPane extends ProjectViewPane {
    public static final String TITLE = "Catdea Logs";
    private static final String ID = "CatdeaLogs";
    /** @see AndroidProjectViewPane#getWeight() */
    private static final int WEIGHT = 141;


    public CatdeaProjectViewPane(@NotNull Project project) {
        super(project);
    }

    @NotNull
    @Override
    public String getTitle() {
        return TITLE;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return CatdeaIcons.FOLDER;
    }

    @NotNull
    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int getWeight() {
        return WEIGHT;
    }

    @NotNull
    @Override
    protected ProjectAbstractTreeStructureBase createStructure() {
        return new ProjectTreeStructure(myProject, ID) {
            @Override
            protected AbstractTreeNode createRoot(@NotNull Project project, @NotNull ViewSettings settings) {
                return new CatdeaNode(project, settings);
            }
        };
    }

    @NotNull
    @Override
    public SelectInTarget createSelectInTarget() {
        return new ProjectViewSelectInTarget(myProject) {
            @Override
            public String toString() {
                return TITLE;
            }

            @Override
            public String getMinorViewId() {
                return ID;
            }
        };
    }
}
