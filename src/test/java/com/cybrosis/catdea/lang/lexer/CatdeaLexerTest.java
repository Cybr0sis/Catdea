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

package com.cybrosis.catdea.lang.lexer;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static com.cybrosis.catdea.lang.CatdeaTypes.*;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.testng.Assert.assertEquals;

public class CatdeaLexerTest {

    @Test
    public void test1() {
        doTest(
                "--------- beginning of crash\n",
                BUFFER.toString(), "--------- beginning of crash",
                WHITE_SPACE.toString(), "\n"
        );
    }

    @Test
    public void test2() {
        doTest(
                "04-20 20:21:19.508 27745 27745 Z libc: info\n",
                MESSAGE_TOKEN.toString(), "04-20 20:21:19.508 27745 27745 Z libc: info",
                WHITE_SPACE.toString(), "\n"
        );
    }

    @Test
    public void test3() {
        doTest(
                "04-19 19:20:17.497 27745 27745 I libc: Fatal signal 11 (SIGSEGV)\n",
                TIMESTAMP_TOKEN.toString(), "04-19 19:20:17.497",
                WHITE_SPACE.toString(), " ",
                PID_TOKEN.toString(), "27745 27745",
                WHITE_SPACE.toString(), " ",
                LEVEL_TOKEN.toString(), "I ",
                TAG_TOKEN.toString(), "libc",
                COLON.toString(), ": ",
                MESSAGE_TOKEN.toString(), "Fatal signal 11 (SIGSEGV)",
                WHITE_SPACE.toString(), "\n"
        );
    }

    private static void doTest(@NotNull CharSequence log, @NotNull String...expected) {
        assert expected.length % 2 == 0 : "expected parameters should be in format: type1, text1, type2, text2, ..., typeN, textN";

        final CatdeaLexer lexer = new CatdeaLexer();
        lexer.start(log);

        IElementType tokenType;
        for (int i = 0; (tokenType = lexer.getTokenType()) != null; lexer.advance()) {
            assertEquals(tokenType.toString() + ":" + lexer.getTokenText(), expected[i] + ":" + expected[i+1]);
            i += 2;
        }
    }
}