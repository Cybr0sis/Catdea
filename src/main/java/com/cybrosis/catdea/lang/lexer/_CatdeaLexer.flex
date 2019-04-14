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
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.cybrosis.catdea.lang.CatdeaTypes.*;

%%

%{
    public _CatdeaLexer() {
        this((java.io.Reader)null);
    }
%}

%public
%class _CatdeaLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

COLON=": "
SLASH="/"
COMMENT=#(.*({EOL}{WHITE_SPACE})?)*

DATE=([0-9][0-9][0-9][0-9]-)?[0-9][0-9]-[0-9][0-9]
TIME=[0-9][0-9]:[0-9][0-9]:[0-9][0-9](\.[0-9]+)?
TIMESTAMP_TOKEN=({DATE}{WHITE_SPACE})?{TIME}
PID_TOKEN=[0-9]+(-|{WHITE_SPACE})[0-9]+
LEVEL_TOKEN=[VDIWEA](\/|{WHITE_SPACE})
PACKAGE_TOKEN=[a-z][a-z0-9_]*(\.[a-z0-9_]+)+|"?"
TAG_TOKEN=(([^:]+)|(:\S))+
MESSAGE_TOKEN=.+

%state IN_TAG
%state IN_MESSAGE

%%
{SLASH}                 { return SLASH; }

<YYINITIAL> {
    ^{COMMENT}              { return COMMENT; }
    {WHITE_SPACE}           { return WHITE_SPACE; }

    {TIMESTAMP_TOKEN}       { return TIMESTAMP_TOKEN; }
    {PID_TOKEN}             { return PID_TOKEN; }
    {PACKAGE_TOKEN}         { return PACKAGE_TOKEN; }
    {LEVEL_TOKEN}           { yybegin(IN_TAG); return LEVEL_TOKEN; }

    [^]                     { yybegin(IN_MESSAGE); }
}

<IN_TAG> {
    {TAG_TOKEN}/{COLON}     { return TAG_TOKEN; }
    {COLON}                 { yybegin(IN_MESSAGE); return COLON; }
}

<IN_MESSAGE> {
    {MESSAGE_TOKEN}|{EOL}         { yybegin(YYINITIAL); return MESSAGE_TOKEN; }
}

[^]  { return BAD_CHARACTER; }
