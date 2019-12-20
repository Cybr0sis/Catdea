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

import com.intellij.openapi.project.DumbService;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author cybrosis
 */
public abstract class PsiDataIndexer<Key, Value> implements DataIndexer<Key, Value, FileContent> {
    @NotNull
    @Override
    public Map<Key, Value> map(@NotNull FileContent inputData) {
        return DumbService
                .getInstance(inputData.getProject())
                .computeWithAlternativeResolveEnabled(() -> map(inputData.getPsiFile()));
    }

    @NotNull
    public abstract Map<Key, Value> map(@NotNull PsiFile psiFile);
}
