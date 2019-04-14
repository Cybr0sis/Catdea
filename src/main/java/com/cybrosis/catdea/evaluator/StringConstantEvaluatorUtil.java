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

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.siyeh.ig.callMatcher.CallHandler;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author cybrosis
 */
public final class StringConstantEvaluatorUtil {
    private static final CallHandler<PsiClass> CLASS_GET_CALL_HANDLER = CallHandler.of(
            CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_OBJECT, "getClass").parameterCount(0),
            call -> {
                final PsiExpression qualifier = call.getMethodExpression().getQualifierExpression();
                return qualifier == null ?
                        PsiTreeUtil.getParentOfType(call, PsiClass.class) :
                        PsiTypesUtil.getPsiClass(qualifier.getType());
            }
    );

    private static final CallHandler<PsiClass> CLASS_GET_SIMPLE_NAME_CALL_HANDLER = CallHandler.of(
            CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_CLASS, "getSimpleName").parameterCount(0),
            call -> {
                final PsiExpression qualifier = call.getMethodExpression().getQualifierExpression();

                if (qualifier instanceof PsiClassObjectAccessExpression) {
                    return PsiTypesUtil.getPsiClass(((PsiClassObjectAccessExpression) qualifier).getOperand().getType());
                }

                if (qualifier instanceof PsiMethodCallExpression) {
                    return CLASS_GET_CALL_HANDLER.apply((PsiMethodCallExpression) qualifier);
                }

                return null;
            }
    );

    @Nullable
    public static String evaluateClassGetSimpleName(@NotNull PsiMethodCallExpression expression) {
        final PsiClass psiClass = CLASS_GET_SIMPLE_NAME_CALL_HANDLER.apply(expression);
        return psiClass == null ? null : psiClass.getName();
    }
}
