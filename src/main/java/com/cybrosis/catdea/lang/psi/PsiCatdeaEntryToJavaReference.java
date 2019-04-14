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

package com.cybrosis.catdea.lang.psi;

import com.cybrosis.catdea.CatdeaService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author cybrosis
 */
public class PsiCatdeaEntryToJavaReference extends PsiReferenceBase.Poly<PsiCatdeaEntry> {

    public PsiCatdeaEntryToJavaReference(@NotNull PsiCatdeaEntry entry) {
        super(entry, TextRange.create(0, entry.getTextLength()),true);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final PsiCatdeaEntry entry = getElement();
        final Project project = entry.getProject();

        return ResolveCache.getInstance(project).resolveWithCaching(
                this,
                (ref, __) -> PsiElementResolveResult.createResults(
                    CatdeaService.getInstance(project).findEmittersOf(entry)
                ),
                false, incompleteCode
        );
    }

    // To fix compatibility issue with IU-182.5262.2
    @NotNull
    @Override
    public Object[] getVariants() {
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }
}
