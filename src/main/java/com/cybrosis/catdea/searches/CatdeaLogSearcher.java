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

package com.cybrosis.catdea.searches;

import com.cybrosis.catdea.files.CatdeaFileType;
import com.cybrosis.catdea.lang.psi.PsiCatdeaEntry;
import com.cybrosis.catdea.lang.psi.PsiCatdeaFile;
import com.cybrosis.catdea.lang.psi.PsiCatdeaVisitor;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

/**
 * @author cybrosis
 */
class CatdeaLogSearcher extends QueryExecutorBase<PsiCatdeaEntry, CatdeaLogSearch.Parameters> {
    public static final CatdeaLogSearcher INSTANCE = new CatdeaLogSearcher();

    @Override
    public void processQuery(@NotNull CatdeaLogSearch.Parameters params, @NotNull Processor<? super PsiCatdeaEntry> consumer) {
        final PsiManager psiManager = PsiManager.getInstance(params.project);

        FileTypeIndex.processFiles(CatdeaFileType.INSTANCE, file -> {
            final PsiFile psiFile = psiManager.findFile(file);
            if (!(psiFile instanceof PsiCatdeaFile)) return true;

            psiFile.acceptChildren(new PsiCatdeaVisitor() {
                @Override
                public void visitEntry(@NotNull PsiCatdeaEntry entry) {
                    if (params.test(entry)) {
                        consumer.process(entry);
                    }
                }
            });

            return true;
        }, params.scope);
    }
}
