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

package com.cybrosis.catdea.validators;

import com.cybrosis.catdea.files.CatdeaFileType;
import com.intellij.codeInsight.daemon.impl.JavaProjectSdkSetupValidator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author cybrosis
 */
public class CatdeaSdkSetupValidator extends JavaProjectSdkSetupValidator {
    @Override
    public boolean isApplicableFor(@NotNull Project project, @NotNull VirtualFile file) {
        return file.getFileType() == CatdeaFileType.INSTANCE;
    }
}
