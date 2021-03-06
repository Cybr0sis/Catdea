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

package com.cybrosis.catdea.folding;

import com.cybrosis.catdea.lang.psi.PsiCatdeaBuffer;
import com.cybrosis.catdea.lang.psi.PsiCatdeaFile;
import com.cybrosis.catdea.lang.psi.PsiCatdeaVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cybrosis
 */
public class CatdeaBufferFoldingBuilder extends FoldingBuilderEx {
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        if (!(root instanceof PsiCatdeaFile)) return FoldingDescriptor.EMPTY;
        final PsiCatdeaFile catdeaFile = (PsiCatdeaFile) root;

        final List<FoldingDescriptor> descriptors = new ArrayList<>();

        catdeaFile.acceptChildren(new PsiCatdeaVisitor() {
            @Override
            public void visitBuffer(@NotNull PsiCatdeaBuffer buffer) {
                descriptors.add(new FoldingDescriptor(
                        buffer.getNode(),
                        buffer.getTextRange(),
                        FoldingGroup.newGroup("catdea:buffer")
                ));
            }
        });

        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        final PsiCatdeaBuffer entry = node.getPsi(PsiCatdeaBuffer.class);
        return entry.getBuffer().getText() + ' ' +
                EditorUtil.displayCharInEditor('\u2025', EditorColors.FOLDED_TEXT_ATTRIBUTES, "..");
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return true;
    }
}
