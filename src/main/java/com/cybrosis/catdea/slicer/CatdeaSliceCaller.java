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
import com.intellij.util.ArrayUtil;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

/**
 * @author cybrosis
 */
public class CatdeaSliceCaller extends CatdeaSlice {
    public final PsiMethodCallExpression call;
    public final PsiExpression expression;

    CatdeaSliceCaller(@NotNull PsiMethodCallExpression call, @NotNull PsiExpression expression, CatdeaSlice parent) {
        super( parent);
        this.call = call;
        this.expression = expression;
    }

    @Override
    void processChildren(@NotNull Processor<CatdeaSlice> processor) {
        final int index = ArrayUtil.find(call.getArgumentList().getExpressions(), expression);
        if (index == -1) return;

        final PsiMethod method;
        try {
            method = call.resolveMethod();
            if (method == null) return;
        } catch (Throwable ignored) {
            return;
        }

        PsiParameter[] parameters = method.getParameterList().getParameters();
        if (index >= parameters.length) return;

        final PsiParameter parameter = parameters[index];
        if (parameter == null) return;

        final CatdeaSlice slice = CatdeaSliceFactory.create(parameter, this);
        assert slice != null;

        processor.process(slice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return expression.equals(((CatdeaSliceCaller) o).expression);
    }

    @Override
    public int hashCode() {
        return expression.hashCode();
    }
}
