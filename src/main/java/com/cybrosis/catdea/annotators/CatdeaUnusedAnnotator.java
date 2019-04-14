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

package com.cybrosis.catdea.annotators;

import com.cybrosis.catdea.CatdeaService;
import com.cybrosis.catdea.highlighting.syntax.CatdeaColors;
import com.cybrosis.catdea.lang.psi.PsiCatdeaEntry;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;

/**
 * @author cybrosis
 */
public class CatdeaUnusedAnnotator extends CatdeaAnnotator {
    @Override
    public void annotate(@NotNull PsiCatdeaEntry entry, @NotNull AnnotationHolder holder) {
        if (!CatdeaService.getInstance(entry.getProject()).available()) return;

        final PsiReference reference = entry.getReference();
        if (!(reference instanceof PsiPolyVariantReference)) return;

        final ResolveResult[] results = ((PsiPolyVariantReference) reference).multiResolve(false);
        if (results.length != 0) return;

        holder.createInfoAnnotation(entry, null)
              .setTextAttributes(CatdeaColors.UNUSED);
    }
}
