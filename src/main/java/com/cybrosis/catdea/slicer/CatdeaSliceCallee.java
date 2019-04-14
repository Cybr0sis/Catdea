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

import com.intellij.psi.*;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

/**
 * @author cybrosis
 */
public class CatdeaSliceCallee extends CatdeaSlice {
    public final PsiMethod method;
    public final PsiParameter parameter;

    CatdeaSliceCallee(@NotNull PsiMethod method, @NotNull PsiParameter parameter, CatdeaSlice parent) {
        super(parent);
        this.method = method;
        this.parameter = parameter;
    }

    @Override
    void processChildren(@NotNull Processor<CatdeaSlice> processor) {
        final PsiCodeBlock body = method.getBody();
        if (body == null) return;

        body.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceExpression(PsiReferenceExpression ref) {
                super.visitReferenceExpression(ref);
                if (ref.getQualifierExpression() != null || !ref.isReferenceTo(parameter)) return;

                final CatdeaSlice slice = CatdeaSliceFactory.create(ref.getElement(), CatdeaSliceCallee.this);
                if (slice == null) return;

                processor.process(slice);
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return parameter.equals(((CatdeaSliceCallee) o).parameter);
    }

    @Override
    public int hashCode() {
        return parameter.hashCode();
    }
}
