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

package com.cybrosis.catdea;

import com.cybrosis.catdea.lang.psi.PsiCatdeaEntry;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Service for mapping Android Logcat <em>log entries</em>(represented by {@linkplain PsiCatdeaEntry})
 * to the source code (i.e <em>log emitter</em>) that emitted these entries
 * (represented by {@linkplain PsiMethodCallExpression} like <code>android.util.Log.v(String tag, String msg)</code>),
 * and vice versa.
 *
 * @author cybrosis
 */
public interface CatdeaService {
    @NotNull
    static CatdeaService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, CatdeaService.class);
    }

    /**
     * @return <code>true</code> if service is available, <code>false</code> otherwise
     */
    boolean available();

    /**
     * Get info about log emitter.
     *
     * @param call specified log emitter
     * @return log emitter info, or <code>null</code> if it is not a log emitter.
     */
    @Nullable
    CatdeaInfo getInfo(@NotNull PsiMethodCallExpression call);

    /**
     * Search for all log emitters that could emit specified log entry.
     *
     * @param entry specified log entry
     * @return founded emitters
     * @see #findEmittedLogsBy(PsiMethodCallExpression)
     */
    @NotNull
    Collection<PsiMethodCallExpression> findEmittersOf(@NotNull PsiCatdeaEntry entry);

    /**
     * Search for all log entries that could be emitted by specified log emitter.
     *
     * @param call specified log emitter
     * @return founded log entries, or <code>null</code> if it is not a log emitter.
     * @see #findEmittersOf(PsiCatdeaEntry)
     */
    @Nullable
    Collection<PsiCatdeaEntry> findEmittedLogsBy(@NotNull PsiMethodCallExpression call);
}
