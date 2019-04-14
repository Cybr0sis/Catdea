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
public class CatdeaLogSearch extends QueryFactory<PsiCatdeaEntry, CatdeaLogSearch.Parameters> {
    public static final CatdeaLogSearch INSTANCE = new CatdeaLogSearch();

    public static class Parameters extends ParametersBase<PsiCatdeaEntry> {
        public final @Language("RegExp") String tagPattern;
        public final @Language("RegExp") String msgPattern;

        @Contract(pure = true)
        public Parameters(@NotNull Project project,
                          @Nullable GlobalSearchScope scope,
                          @Nullable Log.LogLevel level,
                          @Nullable @Language("RegExp") String tagPattern,
                          @Nullable @Language("RegExp") String msgPattern) {
            super(project, scope, level);
            this.tagPattern = tagPattern;
            this.msgPattern = msgPattern;
        }

        public Parameters(@NotNull Project project, @NotNull CatdeaInfo info, @Nullable GlobalSearchScope scope) {
            this(project, scope, info.getLevel(), info.getTagPattern(), info.getMsgPattern());
        }

        @Override
        public boolean test(PsiCatdeaEntry entry) {
            return (level == null || entry.getLevel() == level) &&
                   (tagPattern == null || entry.getTag().matches(tagPattern)) &&
                   (msgPattern == null || entry.getMsg().matches("(?s)" + msgPattern));
        }

    }

    CatdeaLogSearch() {
        registerExecutor(new CatdeaLogSearcher());
    }

    @NotNull
    public static Query<PsiCatdeaEntry> search(@NotNull Parameters params) {
        return INSTANCE.createUniqueResultsQuery(params);
    }
}
