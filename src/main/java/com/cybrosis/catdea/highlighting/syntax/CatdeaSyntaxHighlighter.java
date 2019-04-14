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

package com.cybrosis.catdea.highlighting.syntax;

import com.cybrosis.catdea.lang.lexer.CatdeaLayeredLexer;
import com.cybrosis.catdea.lang.CatdeaTypes;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cybrosis
 */
public class CatdeaSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final Map<IElementType, TextAttributesKey> map = new HashMap<>();

    static {
        fillMap(map, CatdeaColors.COMMENT, CatdeaTypes.COMMENT);

        fillMap(map, CatdeaColors.TIMESTAMP, CatdeaTypes.TIMESTAMP_TOKEN);
        fillMap(map, CatdeaColors.PID, CatdeaTypes.PID_TOKEN);
        fillMap(map, CatdeaColors.PACKAGE, CatdeaTypes.PACKAGE_TOKEN, CatdeaTypes.SLASH);
        fillMap(map, CatdeaColors.LEVEL, CatdeaTypes.LEVEL_TOKEN);
        fillMap(map, CatdeaColors.TAG, CatdeaTypes.TAG_TOKEN);

        fillMap(map, CatdeaColors.COLON, CatdeaTypes.COLON, JavaTokenType.COLON);
        fillMap(map, CatdeaColors.SEMICOLON, JavaTokenType.SEMICOLON);
        fillMap(map, CatdeaColors.DOT, JavaTokenType.DOT);
        fillMap(map, CatdeaColors.COMMA, JavaTokenType.COMMA);

        fillMap(map, CatdeaColors.PARENTHESES, JavaTokenType.LPARENTH, JavaTokenType.RPARENTH);
        fillMap(map, CatdeaColors.BRACES,  JavaTokenType.LBRACE, JavaTokenType.RBRACE);
        fillMap(map, CatdeaColors.BRACKETS,  JavaTokenType.LBRACKET, JavaTokenType.RBRACKET);

        fillMap(map, CatdeaColors.NUMBER, JavaTokenType.INTEGER_LITERAL);
        fillMap(map, CatdeaColors.JAVA_KEYWORDS, JavaTokenType.NULL_KEYWORD, JavaTokenType.TRUE_KEYWORD, JavaTokenType.FALSE_KEYWORD);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new CatdeaLayeredLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(map.get(tokenType));
    }
}
