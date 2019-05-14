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

import com.cybrosis.catdea.utils.ExpressionUtil;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.impl.source.JavaFileElementType;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author cybrosis
 */
public class CatdeaIndex extends FileBasedIndexExtension<String, Collection<CatdeaIndexEntry>> implements PsiDependentIndex {
    public static final ID<String, Collection<CatdeaIndexEntry>> INDEX_ID = ID.create("com.cybrosis.catdea.index");

    private static final CatdeaIndexer INDEXER = new CatdeaIndexer();
    private static final FileBasedIndex.InputFilter FILTER = new DefaultFileTypeSpecificInputFilter(JavaFileType.INSTANCE) {
        @Override
        public boolean acceptInput(@NotNull VirtualFile file) {
            return JavaFileElementType.isInSourceContent(file);
        }
    };

    @NotNull
    public static Set<String> getKeys(@NotNull String... string) {
        return Arrays.stream(string)
                     .map(ExpressionUtil::removeStringFormat)
                     .flatMap(it -> StringUtil.getWordsIn(it).stream())
                     .filter(x -> x.length() > 0 && Character.isAlphabetic(x.charAt(0)))
                     .collect(Collectors.toCollection(() -> new TreeSet<>(
                             Comparator.comparingInt(String::length).reversed().thenComparing(String::compareTo)
                     )));
    }

    @Nullable
    public static String getKey(@NotNull String... strings) {
        return getKeys(strings).stream().findFirst().orElse(null);
    }

    @Nullable
    public static String getKey(@NotNull PsiMethodCallExpression call) {
        final AtomicReference<String> result = new AtomicReference<>();

        call.getArgumentList().acceptChildren(new JavaRecursiveElementVisitor() {
            @Override
            public void visitLiteralExpression(PsiLiteralExpression expression) {
                super.visitLiteralExpression(expression);

                final String value = ExpressionUtil.getValueAsString(expression);
                if (value == null) return;

                final String key = getKey(value);
                if (key == null) return;

                result.accumulateAndGet(key, (old, update) -> {
                    if (old == null) return update;
                    return getKey(old, update);
                });
            }
        });

        return result.get();
    }

    @Override
    public int getVersion() {
        return 2;
    }

    @NotNull
    @Override
    public ID<String, Collection<CatdeaIndexEntry>> getName() {
        return INDEX_ID;
    }

    @NotNull
    @Override
    public DataIndexer<String, Collection<CatdeaIndexEntry>, FileContent> getIndexer() {
        return INDEXER;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @NotNull
    @Override
    public DataExternalizer<Collection<CatdeaIndexEntry>> getValueExternalizer() {
        return CatdeaIndexEntry.EXTERNALIZER;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return FILTER;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

}
