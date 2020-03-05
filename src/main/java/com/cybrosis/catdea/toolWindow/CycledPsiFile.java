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

package com.cybrosis.catdea.toolWindow;

import com.intellij.execution.impl.ConsoleBuffer;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author cybrosis
 */
class CycledPsiFile extends PsiFileBase {
    private final PsiFile delegate;

    CycledPsiFile(@NotNull PsiFile psiFile) {
        super(psiFile.getViewProvider(), psiFile.getLanguage());
        this.delegate = psiFile;
    }

    void append(@NotNull PsiElement... elements) throws IncorrectOperationException {
        ApplicationManager.getApplication().assertWriteAccessAllowed();

        for (PsiElement element : elements) {
            delegate.add(element);
        }

        if (ConsoleBuffer.useCycleBuffer()) {
            while (delegate.getTextLength() > ConsoleBuffer.getCycleBufferSize()) {
                delegate.getFirstChild().delete();
            }

            if (delegate.getFirstChild() instanceof PsiWhiteSpace) delegate.getFirstChild().delete();
        }
    }

    void clear() {
        delegate.deleteChildRange(delegate.getFirstChild(), delegate.getLastChild());
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return delegate.getFileType();
    }
}
