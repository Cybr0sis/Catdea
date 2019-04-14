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

package com.cybrosis.catdea.highlighting.usages;

import com.cybrosis.catdea.lang.psi.*;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author cybrosis
 */
public class CatdeaHighlightUsagesHandler extends HighlightUsagesHandlerBase<PsiElement> {
    private final PsiCatdeaTag target;

    protected CatdeaHighlightUsagesHandler(@NotNull Editor editor, @NotNull PsiFile file, @NotNull PsiCatdeaTag target) {
        super(editor, file);
        this.target = target;
    }

    @Override
    public List<PsiElement> getTargets() {
        return Collections.singletonList(target);
    }

    @Override
    protected void selectTargets(List<PsiElement> targets, Consumer<List<PsiElement>> selectionConsumer) {
        selectionConsumer.consume(targets);
    }

    @Override
    public void computeUsages(List<PsiElement> targets) {
        final PsiFile file = target.getContainingFile();
        if (!(file instanceof PsiCatdeaFile)) return;

        final String targetTag = target.getText();

        file.acceptChildren(new PsiCatdeaVisitor() {
            @Override
            public void visitEntry(@NotNull PsiCatdeaEntry entry) {
                final PsiCatdeaTag tag = entry.getHeader().getTag();

                if (targetTag.equals(tag.getText())) {
                    addOccurrence(tag);
                }
            }
        });
    }
}
