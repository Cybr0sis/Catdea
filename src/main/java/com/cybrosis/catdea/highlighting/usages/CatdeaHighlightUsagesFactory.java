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

import com.cybrosis.catdea.lang.psi.PsiCatdeaFile;
import com.cybrosis.catdea.lang.psi.PsiCatdeaTag;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerFactoryBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author cybrosis
 */
public class CatdeaHighlightUsagesFactory extends HighlightUsagesHandlerFactoryBase {
    @Nullable
    @Override
    public HighlightUsagesHandlerBase createHighlightUsagesHandler(@NotNull Editor editor, @NotNull PsiFile file, @NotNull PsiElement target) {
        if (!(file instanceof PsiCatdeaFile)) return null;

        final PsiElement parent = target.getParent();
        if (!(parent instanceof PsiCatdeaTag)) return null;

        return new CatdeaHighlightUsagesHandler(editor, file, (PsiCatdeaTag) parent);
    }
}
