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


import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author cybrosis
 */
public abstract class PsiValuesProcessor<V> implements FileBasedIndex.ValueProcessor<Collection<V>> {
    private final PsiManager psiManager;

    public PsiValuesProcessor(@NotNull Project project) {
        this.psiManager = PsiManager.getInstance(project);
    }

    @Override
    public boolean process(@NotNull VirtualFile file, Collection<V> values) {
        final PsiFile psiFile = psiManager.findFile(file);
        assert psiFile != null;

        for (V value : values) {
            if (!process(psiFile, value)) return false;
        }
        return true;
    }

    abstract public boolean process(@NotNull PsiFile psiFile, V value);
}
