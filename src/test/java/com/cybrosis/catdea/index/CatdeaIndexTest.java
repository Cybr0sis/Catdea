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

import com.cybrosis.catdea.MockProjectDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.*;

/**
 * @author cybrosis
 */
public class CatdeaIndexTest extends LightCodeInsightFixtureTestCase {

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

        FileBasedIndex.getInstance().requestRebuild(CatdeaIndex.INDEX_ID);
    }

    @Override
    protected String getTestDataPath() {
        final URL resource = getClass().getClassLoader().getResource("com.cybrosis.catdea.tests");
        assertNotNull(resource);
        return resource.getPath();
    }

    public void test1() {
        myFixture.configureByFile(getTestDataPath() + "/Test.java");

        final Map<String, List<CatdeaIndexEntry>> actual = collectIndexes(getProject());
        assertEquals(new TreeSet<>(Arrays.asList("bar", "boo", "foo")), actual.keySet());

        assertEquals(1, actual.get("bar").size());
        assertEquals(1, actual.get("boo").size());
        assertEquals(1, actual.get("foo").size());
    }

    private static Map<String, List<CatdeaIndexEntry>> collectIndexes(@NotNull Project project) {
        final Map<String, List<CatdeaIndexEntry>> result = new HashMap<>();
        final GlobalSearchScope scope = GlobalSearchScope.projectScope(project);

        FileBasedIndex.getInstance().processAllKeys(
                CatdeaIndex.INDEX_ID,
                key -> FileBasedIndex.getInstance().processValues(
                        CatdeaIndex.INDEX_ID, key, null,
                        (file, entries) -> {
                            for (CatdeaIndexEntry entry : entries) {
                                result.computeIfAbsent(key, __ -> new ArrayList<>()).add(entry);
                            }
                            return true;
                        },
                        scope
                ),
                project
        );

        return result;
    }
}
