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
import com.cybrosis.catdea.lang.psi.PsiCatdeaEntry;
import com.cybrosis.catdea.lang.psi.PsiCatdeaEntryToJavaReference;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author cybrosis
 */
public abstract class PsiCatdeaEntryMixin extends ASTWrapperPsiElement implements PsiCatdeaEntry {
    public PsiCatdeaEntryMixin(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        return getHeader().getTag();
    }

    @Override
    public PsiReference getReference() {
        return new PsiCatdeaEntryToJavaReference(this);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public String getPresentableText() {
                return StringUtil.shortenTextWithEllipsis(
                        StringUtil.escapeLineBreak(getMessage().getText())
                                  .replaceAll("\\s{2,}", " "),
                        80, 0, true
                );
            }

            @Nullable
            @Override
            public String getLocationString() {
                return getHeader().getTag().getText();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return null;
            }
        };
    }

    @NotNull
    @Override
    public Log.LogLevel getLevel() {
        return getHeader().getLevel().getLevel();
    }

    @NotNull
    @Override
    public String getTag() {
        return getHeader().getTag().getText();
    }

    @NotNull
    @Override
    public String getMsg() {
        return getMessage().getText();
    }
}
