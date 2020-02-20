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

import com.cybrosis.catdea.files.CatdeaFileType;
import icons.CatdeaIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class CatdeaNode extends ProjectViewNode<String> {
    CatdeaNode(Project project, ViewSettings settings) {
        super(project, CatdeaProjectViewPane.TITLE, settings);
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
        return file.getFileType() instanceof CatdeaFileType;
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode> getChildren() {
        final List<AbstractTreeNode> result = new ArrayList<>();

        final PsiManager psiManager = PsiManager.getInstance(myProject);

        FileTypeIndex.processFiles(
                CatdeaFileType.INSTANCE,
                file -> {
                    final PsiFile psiFile = psiManager.findFile(file);
                    if (psiFile != null) {
                        result.add(new CatdeaFileNode(getProject(), psiFile, getSettings()));
                    }
                    return true;
                },
                GlobalSearchScope.projectScope(myProject)
        );

        return result;
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        presentation.setPresentableText(CatdeaProjectViewPane.TITLE);
        presentation.setIcon(CatdeaIcons.FOLDER);
    }
}
