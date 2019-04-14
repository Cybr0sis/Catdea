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
import com.intellij.psi.util.PsiTreeUtil;
import com.siyeh.ig.psiutils.FormatUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author cybrosis
 */
public final class CatdeaSliceFactory {
    @Nullable
    public static CatdeaSliceCaller create(@NotNull PsiElement element, @Nullable CatdeaSlice parent) {
        while (element instanceof PsiExpression) {
            if (PsiTreeUtil.instanceOf(element, PsiNewExpression.class, PsiArrayInitializerExpression.class)) return null;

            final PsiElement father = element.getParent();
            if (father instanceof PsiExpressionList) {
                final PsiElement grandfather = father.getParent();
                if (!(grandfather instanceof PsiMethodCallExpression)) return null;

                final PsiMethodCallExpression call = (PsiMethodCallExpression) grandfather;
                if (FormatUtils.isFormatCall(call)) {
                    element = call;
                    continue;
                }

                if (!(call.getParent() instanceof PsiExpressionStatement)) return null;
                if (PsiTreeUtil.instanceOf(call.getMethodExpression(), PsiThisExpression.class, PsiSuperExpression.class)) return null;

                return new CatdeaSliceCaller(call, (PsiExpression) element, parent);
            }

            element = father;
        }

        return null;
    }

    @Nullable
    public static CatdeaSliceCallee create(@NotNull PsiParameter parameter, @Nullable CatdeaSlice parent) {
        final PsiElement method = parameter.getDeclarationScope();

        return method instanceof PsiMethod ?
                new CatdeaSliceCallee((PsiMethod) method, parameter, parent) :
                null;
    }

}
