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

package com.cybrosis.catdea;

import com.cybrosis.catdea.lang.psi.PsiCatdeaEntry;
import com.cybrosis.catdea.lang.psi.PsiCatdeaFile;
import com.cybrosis.catdea.lang.psi.PsiCatdeaVisitor;
import com.intellij.psi.*;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author cybrosis
 */
public class CatdeaServiceTest extends LightCodeInsightFixtureTestCase {

    private static final String CALL_1 = "Log.i(TAG + \"2\", \"foo = \" + x)";
    private static final String CALL_2 = "Logger.info(TAG, \"bar(\" + x + \")\")";
    private static final String CALL_3 = "Logger.info(TAG, String.format(\"boo(%s)\", x))";

    private static final String LOG_1 = "12-04 15:21:49.009 7291-11852/com.cybrosis.catdea I/Test2: foo = x";
    private static final String LOG_2 = "12-04 15:35:12.010 7291-11852/com.cybrosis.catdea I/PRETAG: [Test] bar(y)";
    private static final String LOG_3 = "12-04 17:21:44.011 7291-11852/com.cybrosis.catdea I/PRETAG: [Test] boo(z)";

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return MockProjectDescriptor.INSTANCE;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        assertNotNull(myFixture.findClass("java.lang.Object"));
        assertNotNull(myFixture.findClass("android.util.Log"));
    }

    @Override
    protected String getTestDataPath() {
        final URL resource = getClass().getClassLoader().getResource("com.cybrosis.catdea.tests");
        assertNotNull(resource);
        return resource.getPath();
    }

    public void test1() {
        final PsiFile javaFile = myFixture.configureByFile(getTestDataPath() + "/Test.java");
        assertTrue(javaFile instanceof PsiJavaFile);

        final List<PsiMethodCallExpression> calls = new ArrayList<>();

        javaFile.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression call) {
                super.visitMethodCallExpression(call);
                calls.add(call);
            }
        });

        assertEquals(16, calls.size());
        assertEquals(CALL_1, calls.get(2).getText());
        assertEquals(CALL_2, calls.get(3).getText());
        assertEquals(CALL_3, calls.get(5).getText());


        final PsiFile logFile = myFixture.configureByFile(getTestDataPath() + "/Log.log");
        assertTrue(logFile instanceof PsiCatdeaFile);

        final List<PsiCatdeaEntry> logs = new ArrayList<>();

        logFile.acceptChildren(new PsiCatdeaVisitor() {
            @Override
            public void visitEntry(@NotNull PsiCatdeaEntry entry) {
                logs.add(entry);
            }
        });

        assertEquals(3, logs.size());
        assertEquals(LOG_1, logs.get(0).getText());
        assertEquals(LOG_2, logs.get(1).getText());
        assertEquals(LOG_3, logs.get(2).getText());


        final CatdeaService catdea = CatdeaService.getInstance(getProject());

        assertSameElements(toArray(catdea.findEmittersOf(logs.get(0))), CALL_1);
        assertSameElements(toArray(catdea.findEmittersOf(logs.get(1))), CALL_2);
        assertSameElements(toArray(catdea.findEmittersOf(logs.get(2))), CALL_3);

        assertSameElements(toArray(Objects.requireNonNull(catdea.findEmittedLogsBy(calls.get(2)))), LOG_1);
        assertSameElements(toArray(Objects.requireNonNull(catdea.findEmittedLogsBy(calls.get(3)))), LOG_2);
        assertSameElements(toArray(Objects.requireNonNull(catdea.findEmittedLogsBy(calls.get(5)))), LOG_3);
    }

    private static Object[] toArray(Collection<? extends PsiElement> collection) {
        return collection.stream().map(PsiElement::getText).toArray();
    }
}
