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

package com.cybrosis.catdea.lang.psi.impl;

import com.android.ddmlib.Log;
import com.cybrosis.catdea.lang.psi.PsiCatdeaLevel;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author cybrosis
 */
public abstract class PsiCatdeaLevelMixin extends ASTWrapperPsiElement implements PsiCatdeaLevel {
    public PsiCatdeaLevelMixin(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    public Log.LogLevel getLevel() {
        try {
            return Log.LogLevel.getByLetterString(getText());
        } catch (NullPointerException e) {
            // Should never happen but warn seems like a decent default just in case
            return Log.LogLevel.WARN;
        }
    }
}
