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

import com.cybrosis.catdea.index.CatdeaIndex;
import com.cybrosis.catdea.index.CatdeaIndexEntry;
import com.cybrosis.catdea.index.PsiValuesProcessor;
import com.cybrosis.catdea.lang.psi.PsiCatdeaEntry;
import com.cybrosis.catdea.searches.CatdeaEmitterSearch;
import com.cybrosis.catdea.searches.CatdeaLogSearch;
import com.cybrosis.catdea.utils.AndroidLogHelper;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ex.ProjectRootManagerEx;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.reference.SoftReference;
import com.intellij.util.ArrayUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * @author cybrosis
 */
public class CatdeaServiceImpl implements CatdeaService, ProjectRootManagerEx.ProjectJdkListener {
    private final Project project;
    private SoftReference<PsiClass> androidLogClassRef;

    CatdeaServiceImpl(@NotNull Project project) {
        this.project = project;
        ProjectRootManagerEx.getInstanceEx(project).addProjectJdkListener(this);
    }

    @Override
    public boolean available() {
        final PsiClass androidLogClass = SoftReference.dereference(androidLogClassRef);
        if (androidLogClass != null) return true;

        DumbService.getInstance(project).runWhenSmart(() -> {
            final PsiClass psiClass = AndroidLogHelper.getAndroidLogClass(project);
            if (psiClass != null) {
                androidLogClassRef = new SoftReference<>(psiClass);
            }
        });

        return SoftReference.dereference(androidLogClassRef) != null;
    }

    @Override
    public void projectJdkChanged() {
        androidLogClassRef.clear();
    }

    @Nullable
    @Override
    public CatdeaInfo getInfo(@NotNull PsiMethodCallExpression call) {
        if (!available()) return null;

        final String key = CatdeaIndex.getKey(call);
        if (key == null) return null;

        return CachedValuesManager.getCachedValue(call, () -> {
            final Ref<CatdeaInfo> result = new Ref<>(null);

            final PsiFile psiFile = call.getContainingFile();
            final int callOffset = call.getTextOffset();

            FileBasedIndex.getInstance().processValues(
                    CatdeaIndex.INDEX_ID, key, psiFile.getVirtualFile(),
                    new PsiValuesProcessor<CatdeaIndexEntry>(call.getProject()) {
                        @Override
                        public boolean process(@NotNull PsiFile psiFile, CatdeaIndexEntry entry) {
                            if (entry.callOffset == callOffset) {
                                result.set(entry);
                                return false;
                            }
                            return true;
                        }
                    }, GlobalSearchScope.fileScope(psiFile)
            );

            return Result.createSingleDependency(result.get(), call);
        });
    }

    @NotNull
    @Override
    public Collection<PsiMethodCallExpression> findEmittersOf(@NotNull PsiCatdeaEntry entry) {
        if (!available()) return Collections.emptyList();

        return CachedValuesManager.getCachedValue(entry, () -> {
            final Collection<PsiMethodCallExpression> result = CatdeaEmitterSearch
                    .search(new CatdeaEmitterSearch.Parameters(entry, null))
                    .findAll();

            return Result.create(result, ArrayUtil.append(result.toArray(), entry));
        });
    }

    @Nullable
    @Override
    public Collection<PsiCatdeaEntry> findEmittedLogsBy(@NotNull PsiMethodCallExpression call) {
        if (!available()) return null;

        final CatdeaInfo info = getInfo(call);
        if (info == null) return null;

        return CachedValuesManager.getCachedValue(call, () -> {
            final Collection<PsiCatdeaEntry> result = CatdeaLogSearch
                    .search(new CatdeaLogSearch.Parameters(project, info, null))
                    .findAll();

            return Result.create(result, ArrayUtil.append(result.toArray(), call));
        });
    }
}
