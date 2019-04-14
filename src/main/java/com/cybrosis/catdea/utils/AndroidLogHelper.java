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

import com.android.ddmlib.Log;
import com.intellij.openapi.module.impl.scopes.JdkScope;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.siyeh.ig.callMatcher.CallMapper;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.psi.CommonClassNames.JAVA_LANG_STRING;
import static com.intellij.psi.CommonClassNames.JAVA_LANG_THROWABLE;

/**
 * @author cybrosis
 */
public class AndroidLogHelper {
    public static final String ANDROID_UTIL_LOG = "android.util.Log";
    public final static CallMapper<Log.LogLevel> LOG_LEVEL_MAPPER = new CallMapper<Log.LogLevel>()
            .register(of("v"), Log.LogLevel.VERBOSE)
            .register(of("d"), Log.LogLevel.DEBUG)
            .register(of("i"), Log.LogLevel.INFO)
            .register(of("w"), Log.LogLevel.WARN)
            .register(of("e"), Log.LogLevel.ERROR)
            .register(of("wtf"), Log.LogLevel.ERROR);

    @NotNull
    private static CallMatcher of(String methodName) {
        final CallMatcher.Simple base = CallMatcher.staticCall(ANDROID_UTIL_LOG, methodName);
        return CallMatcher.anyOf(
                base.parameterTypes(/* tag: */ JAVA_LANG_STRING, /* msg: */ JAVA_LANG_STRING),
                base.parameterTypes(/* tag: */ JAVA_LANG_STRING, /* msg: */ JAVA_LANG_STRING, /* error: */ JAVA_LANG_THROWABLE)
        );
    }

    @Nullable
    public static PsiClass getAndroidLogClass(@NotNull Project project) {
        return JavaPsiFacade.getInstance(project).findClass(ANDROID_UTIL_LOG, JdkScope.allScope(project));
    }

    @Nullable
    public static PsiParameter findParameterByName(@NotNull PsiMethod method, @NotNull String name) {
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            if (name.equals(parameter.getName())) {
                return parameter;
            }
        }
        return null;
    }
}
