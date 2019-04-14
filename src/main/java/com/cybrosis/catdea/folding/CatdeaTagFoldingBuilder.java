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

package com.cybrosis.catdea.folding;

import com.cybrosis.catdea.lang.psi.*;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author cybrosis
 */
public class CatdeaTagFoldingBuilder extends FoldingBuilderEx {

    private static class HeadAndTail<T> {
        private final T head;
        private T tail;

        @Contract(pure = true)
        public HeadAndTail(@NotNull T item) {
            head = tail = item;
        }

        public void add(T item) {
            tail = item;
        }

        @NotNull
        public T getHead() {
            return head;
        }

        @NotNull
        public T getTail() {
            return tail;
        }
    }

    private static class Block extends HeadAndTail<PsiCatdeaEntry> {
        public Block(@NotNull PsiCatdeaEntry item) {
            super(item);
        }
    }

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        if (!(root instanceof PsiCatdeaFile)) return FoldingDescriptor.EMPTY;
        final PsiCatdeaFile catdeaFile = (PsiCatdeaFile) root;

        final Map<String, Collection<Block>> grouped = new HashMap<>();

        catdeaFile.acceptChildren(new PsiCatdeaVisitor() {
            private String previous = null;
            private Block current = null;

            @Override
            public void visitEntry(@NotNull PsiCatdeaEntry entry) {
                final String tag = entry.getTag();

                if (!tag.equals(previous)) {
                    current = new Block(entry);
                    grouped.computeIfAbsent(tag, __ -> new ArrayList<>()).add(current);
                }
                else current.add(entry);

                previous = tag;
            }
        });

        final List<FoldingDescriptor> descriptors = new ArrayList<>();

        for (Collection<Block> values : grouped.values()) {
            final FoldingGroup group = FoldingGroup.newGroup("catdea:tag");

            for (Block block : values) {
                descriptors.add(new FoldingDescriptor(
                        block.getHead().getNode(),
                        TextRange.create(
                                block.getHead().getTextRange().getStartOffset(),
                                block.getTail().getTextRange().getEndOffset()
                        ),
                        group
                ));
            }
        }

        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        final PsiCatdeaEntry entry = node.getPsi(PsiCatdeaEntry.class);
        return entry.getTag() + ' ' +
               EditorUtil.displayCharInEditor('\u2025', EditorColors.FOLDED_TEXT_ATTRIBUTES, "..");
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
