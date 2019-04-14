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
import com.intellij.openapi.application.DumbAwareSearchParameters;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * @author cybrosis
 */
abstract class ParametersBase<T> implements DumbAwareSearchParameters, Predicate<T> {
    public final Project project;
    public final GlobalSearchScope scope;
    public final Log.LogLevel level;

    public ParametersBase(@NotNull Project project, @Nullable GlobalSearchScope scope, @Nullable Log.LogLevel level) {
        this.project = project;
        this.scope = scope != null ? scope : GlobalSearchScope.projectScope(project);
        this.level = level;
    }

    @NotNull
    @Override
    public Project getProject() {
        return project;
    }
}
