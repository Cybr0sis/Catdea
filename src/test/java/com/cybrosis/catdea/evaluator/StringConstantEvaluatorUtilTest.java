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

package com.cybrosis.catdea.evaluator;

import com.cybrosis.catdea.MockProjectDescriptor;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

/**
 * @author cybrosis
 */
public class StringConstantEvaluatorUtilTest extends LightCodeInsightFixtureTestCase {

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return MockProjectDescriptor.INSTANCE;
    }

    @Test
    public void test1() {
        doTest("Foo.class.getSimpleName()", "Foo");
    }

    @Test
    public void test2() {
        doTest("this.getClass().getSimpleName()", "Foo");
    }

    public void test3() {
        doTest("super.getClass().getSimpleName()", "Object");
    }

    @Test
    public void test4() {
        doTest("static class Boo extends Foo {{ this.getClass().getSimpleName(); }}", "Boo");
    }

    @Test
    public void test5() {
        doTest("static class Boo extends Foo {{ super.getClass().getSimpleName(); }}", "Foo");
    }

    @Test
    public void test6() {
        doTest("getClass().getSimpleName()", "Foo");
    }

    @Test
    public void test7() {
        doTest("Integer.valueOf(0).getClass().getSimpleName()", "Integer");
    }

    private void doTest(@Language("JShellLanguage") String code, String expected) {
        final PsiMethodCallExpression call = PsiTreeUtil.findChildOfType(
                myFixture.configureByText("Foo.java", "class Foo {{ " + code + ";}}"),
                PsiMethodCallExpression.class
        );
        assertNotNull(call);

        final String actual = StringConstantEvaluatorUtil.evaluateClassGetSimpleName(call);
        assertEquals(expected, actual);
    }
}
