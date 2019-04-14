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

import com.cybrosis.catdea.index.CatdeaIndex;
import com.cybrosis.catdea.index.CatdeaIndexEntry;
import com.cybrosis.catdea.index.PsiValuesProcessor;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author cybrosis
 */
public class CatdeaEmitterSearcher extends QueryExecutorBase<PsiMethodCallExpression, CatdeaEmitterSearch.Parameters> {
    @Override
    public void processQuery(@NotNull CatdeaEmitterSearch.Parameters params, @NotNull Processor<? super PsiMethodCallExpression> consumer) {
        final Ref<Boolean> skip = new Ref<>(false);
        final Collection<String> keys = CatdeaIndex.getKeys(params.msg);

        for (String key : keys) {
            FileBasedIndex.getInstance().processValues(
                    CatdeaIndex.INDEX_ID, key, null,
                    new PsiValuesProcessor<CatdeaIndexEntry>(params.project) {
                        @Override
                        public boolean process(@NotNull PsiFile psiFile, CatdeaIndexEntry entry) {
                            if (!params.test(entry)) return true;

                            final PsiMethodCallExpression call = PsiTreeUtil.findElementOfClassAtOffset(
                                    psiFile, entry.callOffset, PsiMethodCallExpression.class, true
                            );

                            if (call == null) {
                                FileBasedIndex.getInstance().requestRebuild(CatdeaIndex.INDEX_ID);
                                throw IndexNotReadyException.create();
                            }

                            skip.set(true);
                            return consumer.process(call);
                        }
                    }, params.scope
            );

            if (skip.get()) break;
        }
    }
}
