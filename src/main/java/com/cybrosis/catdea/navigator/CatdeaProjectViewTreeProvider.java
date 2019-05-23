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

import com.cybrosis.catdea.CatdeaService;
import com.cybrosis.catdea.lang.psi.PsiCatdeaFile;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.ProjectViewProjectNode;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CatdeaProjectViewTreeProvider implements TreeStructureProvider {
    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent,
                                               @NotNull Collection<AbstractTreeNode> children,
                                               ViewSettings settings) {
        if (parent instanceof CatdeaNode) return children;

        final Project project = parent.getProject();
        if (project == null) return children;

        final List<AbstractTreeNode> result = new ArrayList<>(children);

        if (parent instanceof ProjectViewProjectNode && CatdeaService.getInstance(project).available()) {
            result.add(new CatdeaNode(project, settings));
            return result;
        }

        result.removeIf(child -> {
            return child instanceof PsiFileNode &&
                    ((PsiFileNode) child).getValue() instanceof PsiCatdeaFile;
        });

        return result;
    }
}
