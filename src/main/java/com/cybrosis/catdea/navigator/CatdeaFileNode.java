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

import icons.CatdeaIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * @author cybrosis
 */
class CatdeaFileNode extends PsiFileNode {
    CatdeaFileNode(Project project, @NotNull PsiFile psiFile, ViewSettings settings) {
        super(project, psiFile, settings);
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
        return false;
    }

    @Override
    public Collection<AbstractTreeNode> getChildrenImpl() {
        return Collections.emptyList();
    }

    @Override
    public void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(getValue().getName());
        presentation.setIcon(CatdeaIcons.FILE_TYPE);
    }
}
