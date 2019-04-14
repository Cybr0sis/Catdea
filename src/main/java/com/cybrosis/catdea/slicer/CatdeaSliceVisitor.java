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

package com.cybrosis.catdea.slicer;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author cybrosis
 */
public class CatdeaSliceVisitor implements Processor<CatdeaSlice> {
    protected final Deque<CatdeaSlice> stack = new ArrayDeque<>();

    @Override
    public boolean process(CatdeaSlice root) {
        stack.push(root);

        CatdeaSlice slice;
        while (!stack.isEmpty()) {
            ProgressManager.checkCanceled();
            slice = stack.pop();

            if (elementStarted(slice)) {
                slice.processChildren(it -> {
                    stack.push(it);
                    return true;
                });
            }
        }

        return true;
    }

    /**
     * @return <code>false</code> to skip children processing
     */
    public boolean elementStarted(@NotNull CatdeaSlice slice) {
        return true;
    }
}
