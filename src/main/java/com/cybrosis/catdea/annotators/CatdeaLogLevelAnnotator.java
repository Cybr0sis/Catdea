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

package com.cybrosis.catdea.annotators;

import com.android.ddmlib.Log;
import com.cybrosis.catdea.highlighting.syntax.CatdeaColors;
import com.cybrosis.catdea.lang.psi.PsiCatdeaEntry;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author cybrosis
 */
public class CatdeaLogLevelAnnotator extends CatdeaAnnotator {
    private static final Function<Log.LogLevel, TextAttributesKey> COLOR_MAPPER = level -> {
        switch (level) {
            case DEBUG: return CatdeaColors.LEVEL_DEBUG;
            case INFO: return CatdeaColors.LEVEL_INFO;
            case WARN: return CatdeaColors.LEVEL_WARN;
            case ERROR: return CatdeaColors.LEVEL_ERROR;
            case ASSERT: return CatdeaColors.LEVEL_ASSERT;
            default: return CatdeaColors.LEVEL_VERBOSE;
        }
    };

    @Override
    public void annotate(@NotNull PsiCatdeaEntry entry, @NotNull AnnotationHolder holder) {
        final Document document = PsiDocumentManager
                .getInstance(entry.getProject())
                .getDocument(entry.getContainingFile());
        if (document == null) return;

        final int line = document.getLineNumber(entry.getTextOffset());
        final int last = document.getLineCount();

        final PsiCatdeaEntry next = PsiTreeUtil.getNextSiblingOfType(entry, PsiCatdeaEntry.class);

        final TextRange range = TextRange.create(
                document.getLineStartOffset(line),
                line == last - 1 ?
                        document.getLineEndOffset(line) :
                        document.getLineStartOffset(
                                next == null ?
                                        line + 1 :
                                        document.getLineNumber(next.getTextOffset())
                        )
        );

        final Log.LogLevel level = entry.getLevel();

        holder.createInfoAnnotation(range, null)
              .setTextAttributes(COLOR_MAPPER.apply(level));
    }
}
