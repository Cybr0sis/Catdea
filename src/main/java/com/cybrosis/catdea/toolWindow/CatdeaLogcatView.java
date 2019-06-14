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

import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.logcat.LogCatHeader;
import com.android.ddmlib.logcat.LogCatMessage;
import com.android.tools.idea.ddms.DeviceContext;
import com.android.tools.idea.logcat.AndroidLogcatService;
import com.android.tools.idea.logcat.AndroidLogcatView;
import com.android.tools.idea.logcat.LogcatConsoleActionsPostProcessor;
import com.cybrosis.catdea.highlighting.syntax.CatdeaSyntaxHighlighter;
import com.cybrosis.catdea.lang.CatdeaLanguage;
import com.cybrosis.catdea.lang.psi.PsiCatdeaEntry;
import com.intellij.execution.impl.ConsoleViewUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.undo.UndoUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter;
import com.intellij.openapi.editor.impl.softwrap.SoftWrapAppliancePlaces;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.TestActionEvent;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.android.util.AndroidBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CatdeaLogcatView implements Disposable {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS");
    private static final AnActionEvent FAKE_ACTION_EVENT = new TestActionEvent();

    private final Project project;
    private final DeviceContext deviceContext;
    private final EditorEx editor;
    private volatile IDevice device;
    private CycledPsiFile psiFile;
    private AnAction[] actions;
    private final ScrollToTheEndToolbarAction scrollToTheEndAction;

    private final AndroidLogcatService.LogcatListener logcatListener = new AndroidLogcatService.LogcatListener() {
        @Override
        public void onLogLineReceived(@NotNull LogCatMessage line) {
            if (!filter(line)) return;
            final String log = convert(line);

            final boolean isScrollToTheEnd = ApplicationManager.getApplication().runReadAction(
                    (Computable<Boolean>) () -> scrollToTheEndAction.isSelected(FAKE_ACTION_EVENT)
            );

            final PsiElement[] children = ApplicationManager.getApplication().runReadAction(
                    (Computable<PsiElement[]>) () -> PsiTreeUtil.collectElements(
                            PsiFileFactory.getInstance(project).createFileFromText(CatdeaLanguage.INSTANCE, log),
                            it -> PsiTreeUtil.instanceOf(it, PsiCatdeaEntry.class, PsiWhiteSpace.class)
                    )
            );

            executeCommandLaterInWriteAction(project, () -> {
                psiFile.append(children);

                if (isScrollToTheEnd) scrollToTheEndAction.setSelected(FAKE_ACTION_EVENT, true);
            });
        }

        @Override
        public void onCleared() {
            executeCommandLaterInWriteAction(project, () -> {
                psiFile.clear();
            });
        }
    };

    /**
     * @see LogcatConsoleActionsPostProcessor.ClearLogCatAction
     */
    @SuppressWarnings("JavadocReference")
    private final class ClearLogCatAction extends DumbAwareAction {
        private ClearLogCatAction() {
            super(AndroidBundle.message("android.logcat.clear.log.action.title"),
                  AndroidBundle.message("android.logcat.clear.log.action.tooltip"),
                  AllIcons.Actions.GC);
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            e.getPresentation().setEnabled(editor.getDocument().getTextLength() != 0);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            final IDevice device = deviceContext.getSelectedDevice();
            if (device != null) {
                AndroidLogcatService.getInstance().clearLogcat(device, project);
            }
        }
    }


    CatdeaLogcatView(@NotNull Project project, @NotNull DeviceContext deviceContext) {
        this.project = project;
        this.deviceContext = deviceContext;

        // Use PSI to support references and navigation to source code from log entry
        final PsiFile dummy = PsiFileFactory.getInstance(project).createFileFromText(CatdeaLanguage.INSTANCE, "");
        psiFile = new CycledPsiFile(dummy);

        final Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        assert document != null;

        UndoUtil.disableUndoFor(document);
        editor = (EditorEx) EditorFactory.getInstance().createViewer(document, project, EditorKind.CONSOLE);
        editor.getSettings().setAutoCodeFoldingEnabled(false);
        editor.getSettings().setLineMarkerAreaShown(false);

        ConsoleViewUtil.setupConsoleEditor(editor, false, false);
        editor.setHighlighter(new LexerEditorHighlighter(new CatdeaSyntaxHighlighter(), editor.getColorsScheme()));

        scrollToTheEndAction = new ScrollToTheEndToolbarAction(editor);
        actions = new AnAction[]{
                new ClearLogCatAction(),
                new ToggleUseSoftWrapsToolbarAction(SoftWrapAppliancePlaces.CONSOLE) {
                    @Override
                    protected Editor getEditor(@NotNull AnActionEvent e) {
                        return editor;
                    }
                },
                scrollToTheEndAction
        };

        deviceContext.addListener(new DeviceContext.DeviceSelectionListener() {
            @Override
            public void deviceSelected(@Nullable IDevice device) {
                notifyDeviceUpdated(false);
            }

            @Override
            public void deviceChanged(@NotNull IDevice device, int changeMask) {
                if (CatdeaLogcatView.this.device == device && ((changeMask & IDevice.CHANGE_STATE) == IDevice.CHANGE_STATE)) {
                    notifyDeviceUpdated(true);
                }
            }

            @Override
            public void clientSelected(@Nullable Client c) {
            }
        }, this);

        Disposer.register(project, this);
        updateLogConsole();
    }


    public AnAction[] getActions() {
        return actions;
    }

    public Editor getEditor() {
        return editor;
    }

    private void stopListening() {
        if (device != null) {
            AndroidLogcatService.getInstance().removeListener(device, logcatListener);
        }
        device = null;
    }

    /**
     * @see AndroidLogcatView#notifyDeviceUpdated(boolean)
     */
    @SuppressWarnings("JavadocReference")
    private void notifyDeviceUpdated(final boolean forceReconnect) {
        UIUtil.invokeAndWaitIfNeeded((Runnable) () -> {
            if (project.isDisposed()) return;
            if (forceReconnect) stopListening();
            updateLogConsole();
        });
    }

    /**
     * @see AndroidLogcatView#updateLogConsole()
     */
    @SuppressWarnings("JavadocReference")
    private void updateLogConsole() {
        final IDevice selectedDevice = deviceContext.getSelectedDevice();
        if (device == selectedDevice) return;
        stopListening();

        logcatListener.onCleared();

        device = selectedDevice;
        AndroidLogcatService.getInstance().addListener(device, logcatListener);
    }

    private boolean filter(@NotNull LogCatMessage message) {
        if (deviceContext.getSelectedDevice() == null) return false;
        final Client client = deviceContext.getSelectedClient();
        return client != null && client.getClientData().getPid() == message.getHeader().getPid();
    }

    private static String convert(@NotNull LogCatMessage message) {
        final LogCatHeader header = message.getHeader();
        return String.format(
                "%s %d-%d/%s %s/%s: %s\n",
                LocalDateTime.ofInstant(header.getTimestampInstant(), ZoneId.systemDefault()).format(TIME_FORMATTER),
                header.getPid(),
                header.getTid(),
                header.getAppName(),
                Character.toUpperCase(header.getLogLevel().getPriorityLetter()),
                header.getTag(),
                message.getMessage()
        );
    }

    @Override
    public void dispose() {
        EditorFactory.getInstance().releaseEditor(editor);
        stopListening();
        logcatListener.onCleared();
    }

    private static void executeCommandLaterInWriteAction(Project project, Runnable runnable) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (project.isDisposed()) return;

            CommandProcessor.getInstance().executeCommand(project, () -> {
                ApplicationManager.getApplication().runWriteAction(runnable);
            }, null, null);
        });
    }
}
