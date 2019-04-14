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

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.JavaTokenType;import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.cybrosis.catdea.lang.CatdeaTypes.*;

%%

%{
    public _CatdeaMessageLexer() {
        this((java.io.Reader)null);
    }
%}

%public
%class _CatdeaMessageLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s

WORDS = [a-zA-Z_][_.0-9a-zA-Z]*

DECIMAL = [0-9][_0-9]*
SIGNED_DECIMAL = [+-]?{DECIMAL}

RESOLUTION_LOWER = {DECIMAL}(x{DECIMAL})+
RESOLUTION_UPPER = {DECIMAL}(X{DECIMAL})+
RESOLUTION = {RESOLUTION_LOWER} | {RESOLUTION_UPPER}

HEX_LOWER = [0-9a-f][_0-9a-f]*
HEX_UPPER = [0-9A-F][_0-9A-F]*
HEX = 0x{HEX_LOWER} | 0X{HEX_UPPER}

PROBABLY_HEX_LOWER = {HEX_LOWER}*([0-9][a-f]|[a-f][0-9]){HEX_LOWER}*
PROBABLY_HEX_UPPER = {HEX_UPPER}*([0-9][A-F]|[A-F][0-9]){HEX_UPPER}*
PROBABLY_HEX = {PROBABLY_HEX_LOWER} | {PROBABLY_HEX_UPPER}

DIGITS = {SIGNED_DECIMAL} | {RESOLUTION} | {HEX} | {PROBABLY_HEX}

%%

<YYINITIAL> {
    {WHITE_SPACE}+       { return WHITE_SPACE; }

    "("   { return JavaTokenType.LPARENTH; }
    ")"   { return JavaTokenType.RPARENTH; }
    "{"   { return JavaTokenType.LBRACE; }
    "}"   { return JavaTokenType.RBRACE; }
    "["   { return JavaTokenType.LBRACKET; }
    "]"   { return JavaTokenType.RBRACKET; }
    ":"   { return JavaTokenType.COLON; }
    ";"   { return JavaTokenType.SEMICOLON; }
    ","   { return JavaTokenType.COMMA; }
    "."   { return JavaTokenType.DOT; }

    "true"  { return JavaTokenType.TRUE_KEYWORD; }
    "false" { return JavaTokenType.FALSE_KEYWORD; }
    "null"  { return JavaTokenType.NULL_KEYWORD; }

    {DIGITS} { return JavaTokenType.INTEGER_LITERAL; }
    {WORDS} {return MESSAGE_TOKEN; }
}

[^]  { return BAD_CHARACTER; }

