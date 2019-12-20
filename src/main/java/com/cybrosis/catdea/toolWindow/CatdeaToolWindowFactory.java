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

package com.cybrosis.catdea.toolWindow;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.tools.idea.adb.AdbService;
import com.android.tools.idea.concurrent.EdtExecutor;
import com.cybrosis.catdea.icons.CatdeaIcons;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.android.sdk.AndroidSdkUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class CatdeaToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setIcon(CatdeaIcons.TOOL);

        final ContentManager contentManager = toolWindow.getContentManager();

        final CatdeaLogcatPanel catdeaLogcatPanel = new CatdeaLogcatPanel(project);

        final Content content = contentManager.getFactory().createContent(catdeaLogcatPanel, "", false);
        contentManager.addContent(content);

        final File adb = AndroidSdkUtils.getAdb(project);
        if (adb == null) return;

        catdeaLogcatPanel.setLoadingText("Initializing ADB");
        catdeaLogcatPanel.startLoading();

        final ListenableFuture<AndroidDebugBridge> future = AdbService.getInstance().getDebugBridge(adb);
        Futures.addCallback(future, new FutureCallback<AndroidDebugBridge>() {
            @Override
            public void onSuccess(@Nullable AndroidDebugBridge bridge) {
                catdeaLogcatPanel.stopLoading();
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                catdeaLogcatPanel.stopLoading();
                Messages.showErrorDialog(AdbService.getDebugBridgeDiagnosticErrorMessage(t, adb), "ADB Connection Error");
            }
        }, EdtExecutor.INSTANCE);
    }
}
