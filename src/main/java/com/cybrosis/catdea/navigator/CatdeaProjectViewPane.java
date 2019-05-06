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
import com.cybrosis.catdea.CatdeaService;
import com.cybrosis.catdea.files.CatdeaFileType;
import com.cybrosis.catdea.icons.CatdeaIcons;
import com.cybrosis.catdea.lang.psi.PsiCatdeaFile;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.ProjectAbstractTreeStructureBase;
import com.intellij.ide.projectView.impl.ProjectTreeStructure;
import com.intellij.ide.projectView.impl.ProjectViewPane;
import com.intellij.ide.projectView.impl.nodes.ProjectViewProjectNode;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CatdeaProjectViewPane extends ProjectViewPane {
    public static final String ID = "CatdeaLogs";

    public class CatdeaStructureProvider implements TreeStructureProvider {
        @NotNull
        @Override
        public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent,
                                                   @NotNull Collection<AbstractTreeNode> children,
                                                   ViewSettings settings) {
            if (parent instanceof RootNode) return children;

            final Project project = parent.getProject();
            if (project == null) return children;

            final List<AbstractTreeNode> result = new ArrayList<>(children);

            if (parent instanceof ProjectViewProjectNode && CatdeaService.getInstance(myProject).available()) {
                result.add(new RootNode(project, settings));
                return result;
            }

            result.removeIf(child -> {
                return child instanceof PsiFileNode &&
                        ((PsiFileNode) child).getValue() instanceof PsiCatdeaFile;
            });

            return result;
        }
    }

    private class RootNode extends ProjectViewNode<String> {
        RootNode(Project project, ViewSettings viewSettings) {
            super(project, "Logs", viewSettings);
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
                            result.add(new Node(getProject(), psiFile, getSettings()));
                        }
                        return true;
                    },
                    GlobalSearchScope.projectScope(myProject)
            );

            return result;
        }

        @Override
        protected void update(@NotNull PresentationData presentation) {
            presentation.setPresentableText(CatdeaProjectViewPane.this.getTitle());
            presentation.setIcon(CatdeaProjectViewPane.this.getIcon());
        }
    }

    private static class Node extends PsiFileNode {
        Node(Project project, @NotNull PsiFile psiFile, ViewSettings viewSettings) {
            super(project, psiFile, viewSettings);
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


    public CatdeaProjectViewPane(@NotNull Project project) {
        super(project);
    }

    @NotNull
    @Override
    public String getTitle() {
        return "Catdea Logs";
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

    @SuppressWarnings("DanglingJavadoc")
    @Override
    public int getWeight() {
        /** @see AndroidProjectViewPane#getWeight() */
        return 141;
    }

    @NotNull
    @Override
    protected ProjectAbstractTreeStructureBase createStructure() {
        return new ProjectTreeStructure(myProject, ID) {
            @Override
            protected AbstractTreeNode createRoot(@NotNull Project project, @NotNull ViewSettings settings) {
                return new RootNode(project, settings);
            }
        };
    }
}
