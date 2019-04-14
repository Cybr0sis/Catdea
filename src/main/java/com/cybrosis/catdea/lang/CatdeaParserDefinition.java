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

package com.cybrosis.catdea.lang;

import com.cybrosis.catdea.lang.lexer.CatdeaLexer;
import com.cybrosis.catdea.lang.psi.PsiCatdeaFile;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
 * @author cybrosis
 */
public class CatdeaParserDefinition implements ParserDefinition {
    private static final IFileElementType FILE = new IFileElementType(CatdeaLanguage.INSTANCE);
    private static final TokenSet COMMENTS = TokenSet.create(CatdeaTypes.COMMENT);
    private static final TokenSet STRINGS = TokenSet.create(CatdeaTypes.MESSAGE_TOKEN);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new CatdeaLexer();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new CatdeaParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return STRINGS;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return CatdeaTypes.Factory.createElement(node);
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new PsiCatdeaFile(viewProvider);
    }
}
