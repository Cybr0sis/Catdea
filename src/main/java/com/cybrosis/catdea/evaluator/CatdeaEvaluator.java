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

import com.cybrosis.catdea.utils.ExpressionUtil;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.siyeh.ig.psiutils.FormatUtils;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author cybrosis
 */
public class CatdeaEvaluator extends JavaElementVisitor {
    private static final @Language("RegExp") String UNKNOWN = "(.*)";
    private final Map<PsiElement, String> cache = new HashMap<>();

    @Language("RegExp")
    public String get(PsiElement element) {
        return cache.getOrDefault(element, UNKNOWN);
    }

    @Override
    public void visitLiteralExpression(PsiLiteralExpression expression) {
        cache.put(expression, StringUtil.escapeToRegexp(String.valueOf(expression.getValue())));

        visitExpression(expression);
    }

    @Override
    public void visitReferenceExpression(PsiReferenceExpression expression) {
        final PsiElement resolve = expression.resolve();
        if (resolve != null) {
            if (!cache.containsKey(resolve)) {
                resolve.accept(this);
            }
            cache.put(expression, cache.getOrDefault(resolve, UNKNOWN));
        } else cache.put(expression, UNKNOWN);

        visitExpression(expression);
    }

    @Override
    public void visitMethodCallExpression(PsiMethodCallExpression expression) {
        if (FormatUtils.isFormatCall(expression)) {
            visitFormatCall(expression);
        } else {
            final String value = StringConstantEvaluatorUtil.evaluateClassGetSimpleName(expression);
            cache.put(expression, value == null ? UNKNOWN : value);
        }

        visitExpression(expression);
    }

    private void visitFormatCall(PsiMethodCallExpression call) {
        final Pair<PsiExpression, PsiExpression[]> pair = ExpressionUtil.splitFormatCallArguments(call);
        if (pair == null) return;

        final PsiExpression formatArgument = pair.first;
        final PsiExpression[] arguments = pair.second;

        if (!cache.containsKey(formatArgument)) {
            formatArgument.accept(this);
        }
        final String formatString = cache.get(formatArgument);
        assert formatString != null;


        final Matcher matcher = ExpressionUtil.STRING_FORMAT_PATTERN.matcher(StringUtil.unescapeStringCharacters(formatString));

        final List<String> list = new ArrayList<>();

        int count = 0;
        while (matcher.find()) {
            final String plain = matcher.group("text");

            if (plain == null) {
                final String indexGroup = matcher.group("index");
                final int index = indexGroup == null ? count++ : Integer.parseInt(indexGroup) - 1;
                final int arg = index > 0 ? index : 0;

                try {
                    final PsiExpression argument = arguments[arg];

                    if (!cache.containsKey(argument)) {
                        argument.accept(this);
                    }

                    list.add(cache.getOrDefault(argument, UNKNOWN));
                } catch (IndexOutOfBoundsException e) {
                    list.add(UNKNOWN);
                }
            }
            else list.add(StringUtil.escapeToRegexp(plain));
        }

        final String s = String.join("", list);
        cache.put(call, s.isEmpty() ? UNKNOWN : s);
    }

    @Override
    public void visitPolyadicExpression(PsiPolyadicExpression expression) {
        final StringBuilder builder = new StringBuilder();
        String last = null;

        for (PsiExpression operand : expression.getOperands()) {
            if (!cache.containsKey(operand)) {
                operand.accept(this);
            }

            final String s = cache.getOrDefault(operand, UNKNOWN);
            if (!s.equals(last)) {
                builder.append(s);
            }
            last = s;
        }

        cache.put(expression, builder.toString());

        visitExpression(expression);
    }

    @Override
    public void visitExpression(PsiExpression expression) {
        final PsiParameter parameter = ExpressionUtil.findCorrespondingParameter(expression);
        if (parameter != null) {
            cache.put(parameter, cache.getOrDefault(expression, UNKNOWN));
        }
    }

    @Override
    public void visitField(PsiField field) {
        if (field.hasModifierProperty(PsiModifier.FINAL)) {
            final PsiExpression initializer = field.getInitializer();
            if (initializer != null) {
                if (!cache.containsKey(initializer)) {
                    initializer.accept(this);
                }
                cache.put(field, cache.getOrDefault(initializer, UNKNOWN));
            }
        } else cache.put(field, UNKNOWN);
    }
}
