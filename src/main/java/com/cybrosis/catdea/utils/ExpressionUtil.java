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

package com.cybrosis.catdea.utils;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.*;
import com.intellij.util.ArrayUtil;
import com.siyeh.ig.psiutils.ExpressionUtils;
import com.siyeh.ig.psiutils.FormatUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author cybrosis
 */
public final class ExpressionUtil {
    public static final String FORMAT_PATTERN =
            "(?<format>%" +
            "(?>(?<index>\\d+)\\$)?" +
            "(?<flags>[-#+ 0,(<]*)?" +
            "(?<width>\\d+)?" +
            "(?>\\.(?<precision>\\d*))?" +
            "(?<conversion>[tT]?[a-zA-Z])" +
            ")";

    public static final Pattern STRING_FORMAT_PATTERN = Pattern.compile(
            "(?<text>(?>%%|[^%]+))|" +
            FORMAT_PATTERN
    );

    @Contract("null -> null")
    public static String getValueAsString(PsiLiteralExpression expression) {
        if (expression == null) return null;
        final Object value = expression.getValue();
        return value instanceof String ? ((String) value) : null;
    }

    /**
     * @return {@link Pair#first} - format string expression, {@link Pair#second} - format string arguments
     */
    @Nullable
    public static Pair<PsiExpression, PsiExpression[]> splitFormatCallArguments(@NotNull PsiMethodCallExpression call) {
        final PsiExpressionList argumentList = call.getArgumentList();
        final PsiExpression format = FormatUtils.getFormatArgument(argumentList);

        if (!ExpressionUtils.hasStringType(format)) return null;

        final PsiExpression[] arguments = argumentList.getExpressions();

        final int index = ArrayUtil.find(arguments, format) + 1;
        assert index != 0;

        if (index < arguments.length) {
            return Pair.create(format, Arrays.copyOfRange(arguments, index, arguments.length));
        }
        return null;
    }

    @Nullable
    public static PsiParameter findCorrespondingParameter(@NotNull PsiExpression expression) {
        final PsiElement parent = expression.getParent();
        if (!(parent instanceof PsiExpressionList)) return null;
        final PsiExpressionList expressionList = ((PsiExpressionList) parent);

        final PsiElement grandparent = parent.getParent();
        if (!(grandparent instanceof PsiMethodCallExpression)) return null;
        final PsiMethodCallExpression call = (PsiMethodCallExpression) grandparent;

        final PsiExpression[] expressions = expressionList.getExpressions();
        final int index = ArrayUtil.find(expressions, expression);
        if (index == -1) return null;

        final PsiMethod method = call.resolveMethod();
        if (method == null) return null;

        final PsiParameter[] parameters = method.getParameterList().getParameters();
        if (index < parameters.length) {
            return parameters[index];
        }

        return null;
    }

    @NotNull
    public static String removeStringFormat(@NotNull String string) {
        return string.replaceAll(FORMAT_PATTERN, " ");
    }
}
