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

import com.intellij.ide.highlighter.JavaHighlightingColors;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.xdebugger.ui.DebuggerColors;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * @author cybrosis
 */
public interface CatdeaColors {
    TextAttributesKey COMMENT = createTextAttributesKey("CATDEA.COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    TextAttributesKey UNUSED = createTextAttributesKey("CATDEA.UNUSED", DefaultLanguageHighlighterColors.LINE_COMMENT);

    TextAttributesKey TIMESTAMP = createTextAttributesKey("CATDEA.TIMESTAMP", DefaultLanguageHighlighterColors.LINE_COMMENT);
    TextAttributesKey PID = createTextAttributesKey("CATDEA.PID", DefaultLanguageHighlighterColors.LINE_COMMENT);
    TextAttributesKey PACKAGE = createTextAttributesKey("CATDEA.PACKAGE", DefaultLanguageHighlighterColors.LINE_COMMENT);
    TextAttributesKey LEVEL = createTextAttributesKey("CATDEA.LEVEL", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    TextAttributesKey TAG = createTextAttributesKey("CATDEA.TAG", DefaultLanguageHighlighterColors.KEYWORD);

    TextAttributesKey LEVEL_VERBOSE = createTextAttributesKey("CATDEA.LEVEL_VERBOSE", HighlighterColors.NO_HIGHLIGHTING);
    TextAttributesKey LEVEL_DEBUG = createTextAttributesKey("CATDEA.LEVEL_DEBUG", HighlighterColors.NO_HIGHLIGHTING);
    TextAttributesKey LEVEL_INFO = createTextAttributesKey("CATDEA.LEVEL_INFO", HighlighterColors.NO_HIGHLIGHTING);
    TextAttributesKey LEVEL_WARN = createTextAttributesKey("CATDEA.LEVEL_WARN", HighlighterColors.NO_HIGHLIGHTING);
    TextAttributesKey LEVEL_ERROR = createTextAttributesKey("CATDEA.LEVEL_ERROR", DebuggerColors.BREAKPOINT_ATTRIBUTES);
    TextAttributesKey LEVEL_ASSERT = createTextAttributesKey("CATDEA.LEVEL_ASSERT", HighlighterColors.NO_HIGHLIGHTING);

    TextAttributesKey COLON = createTextAttributesKey("CATDEA.COLON", DefaultLanguageHighlighterColors.SEMICOLON);
    TextAttributesKey SEMICOLON = createTextAttributesKey("CATDEA.SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);
    TextAttributesKey DOT = createTextAttributesKey("CATDEA.DOT", DefaultLanguageHighlighterColors.DOT);
    TextAttributesKey COMMA = createTextAttributesKey("CATDEA.COMMA", DefaultLanguageHighlighterColors.COMMA);

    TextAttributesKey PARENTHESES = createTextAttributesKey("CATDEA.PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
    TextAttributesKey BRACES = createTextAttributesKey("CATDEA.BRACES", DefaultLanguageHighlighterColors.BRACES);
    TextAttributesKey BRACKETS = createTextAttributesKey("CATDEA.BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);

    TextAttributesKey NUMBER = createTextAttributesKey("CATDEA.NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    TextAttributesKey JAVA_KEYWORDS = createTextAttributesKey("CATDEA.JAVA_KEYWORDS", JavaHighlightingColors.KEYWORD);
}
