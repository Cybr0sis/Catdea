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

package com.cybrosis.catdea.index;

import com.android.ddmlib.Log;
import com.cybrosis.catdea.evaluator.CatdeaEvaluator;
import com.cybrosis.catdea.slicer.*;
import com.cybrosis.catdea.utils.AndroidLogHelper;
import com.cybrosis.catdea.utils.ExpressionUtil;
import com.intellij.psi.*;
import com.intellij.util.SmartList;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author cybrosis
 */
class CatdeaIndexer extends PsiDataIndexer<String, Collection<CatdeaIndexEntry>> {
    @NotNull
    @Override
    public Map<String, Collection<CatdeaIndexEntry>> map(@NotNull PsiFile psiFile) {
        final Set<CatdeaSliceCaller> roots = collectRoots(psiFile);
        final Map<String, Collection<CatdeaIndexEntry>> result = new HashMap<>();

        for (CatdeaSliceCaller root : roots) {
            final String key = CatdeaIndex.getKey(root.call);
            if (key == null) continue;

            new CatdeaSliceVisitor() {
                private final Set<PsiMethodCallExpression> path = new LinkedHashSet<>();

                @Override
                public boolean elementStarted(@NotNull CatdeaSlice slice) {
                    if (slice instanceof CatdeaSliceCaller) {
                        return path.add(((CatdeaSliceCaller) slice).call);
                    }

                    if (slice instanceof CatdeaSliceCallee) {
                        final CatdeaSliceCallee callee = (CatdeaSliceCallee) slice;

                        final Log.LogLevel level = AndroidLogHelper.LOG_LEVEL_MAPPER.mapFirst(callee.method);
                        if (level == null) return true;
                        if (!"msg".equals(callee.parameter.getName())) return true;

                        final PsiParameter tag = AndroidLogHelper.findParameterByName(callee.method, "tag");
                        assert tag != null;

                        final CatdeaEvaluator evaluator = new CatdeaEvaluator();

                        for (PsiMethodCallExpression call : path) {
                            final PsiExpression[] arguments = call.getArgumentList().getExpressions();
                            for (PsiExpression argument : arguments) {
                                argument.accept(evaluator);
                            }
                        }

                        final @Language("RegExp") String msgPattern = evaluator.get(callee.parameter);
                        assert msgPattern != null;

                        final @Language("RegExp") String tagPattern = evaluator.get(tag);
                        assert tagPattern != null;

                        result.computeIfAbsent(key, __ -> new SmartList<>())
                              .add(new CatdeaIndexEntry(
                                      root.call, level, msgPattern, tagPattern
                              ));
                    }

                    return false;
                }
            }.process(root);
        }

        return result;
    }

    private Set<CatdeaSliceCaller> collectRoots(@NotNull PsiFile psiFile) {
        final Set<CatdeaSliceCaller> result = new HashSet<>();

        psiFile.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitLiteralExpression(PsiLiteralExpression expression) {
                super.visitLiteralExpression(expression);

                final String value = ExpressionUtil.getValueAsString(expression);
                if (value == null) return;

                final String key = CatdeaIndex.getKey(value);
                if (key == null) return;

                final CatdeaSliceCaller root = CatdeaSliceFactory.create(expression, null);
                if (root == null) return;

                result.add(root);
            }
        });

        return result;
    }
}
