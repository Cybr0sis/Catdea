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

package com.cybrosis.catdea.lang.psi;

import com.cybrosis.catdea.files.CatdeaFileType;
import com.cybrosis.catdea.lang.CatdeaLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * @author cybrosis
 */
public class PsiCatdeaFile extends PsiFileBase {
    public PsiCatdeaFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CatdeaLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return CatdeaFileType.INSTANCE;
    }
}
