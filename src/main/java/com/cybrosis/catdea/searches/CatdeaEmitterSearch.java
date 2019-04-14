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

import com.android.ddmlib.Log;
import com.cybrosis.catdea.CatdeaInfo;
import com.cybrosis.catdea.lang.psi.PsiCatdeaEntry;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Query;
import com.intellij.util.QueryFactory;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author cybrosis
 */
public class CatdeaEmitterSearch extends QueryFactory<PsiMethodCallExpression, CatdeaEmitterSearch.Parameters> {
    public static final CatdeaEmitterSearch INSTANCE = new CatdeaEmitterSearch();

    public static class Parameters extends ParametersBase<CatdeaInfo> {
        public final @Language("TEXT") String tag;
        public final @Language("TEXT") String msg;

        @Contract(pure = true)
        public Parameters(@NotNull Project project,
                          @Nullable GlobalSearchScope scope,
                          @Nullable Log.LogLevel level,
                          @Nullable @Language("TEXT") String tag,
                          @Nullable @Language("TEXT") String msg) {
            super(project, scope, level);
            this.tag = tag;
            this.msg = msg;
        }

        public Parameters(@NotNull PsiCatdeaEntry entry, @Nullable GlobalSearchScope scope) {
            this(entry.getProject(), scope, entry.getLevel(), entry.getTag(), entry.getMsg());
        }

        @Override
        public boolean test(CatdeaInfo info) {
            return (level == null || level == info.getLevel()) &&
                   (tag == null || tag.matches(info.getTagPattern())) &&
                   (msg == null || msg.matches("(?s)" + info.getMsgPattern()));
        }

    }

    CatdeaEmitterSearch() {
        registerExecutor(new CatdeaEmitterSearcher());
    }

    @NotNull
    public static Query<PsiMethodCallExpression> search(@NotNull Parameters params) {
        return INSTANCE.createUniqueResultsQuery(params);
    }
}
