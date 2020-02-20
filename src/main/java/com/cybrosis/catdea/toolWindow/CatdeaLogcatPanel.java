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

import com.android.tools.idea.ddms.DeviceContext;
import com.android.tools.idea.ddms.DevicePanel;
import icons.CatdeaIcons;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class CatdeaLogcatPanel extends JBLoadingPanel {
    private JPanel panel;
    private final CatdeaLogcatView catdeaLogcatView;


    public CatdeaLogcatPanel(@NotNull Project project) {
        super(new BorderLayout(), project);

        final DeviceContext context = new DeviceContext();
        final DevicePanel devices = new DevicePanel(project, context);
        catdeaLogcatView = new CatdeaLogcatView(project, context);

        final Editor editor = catdeaLogcatView.getEditor();
        final JComponent editorComponent = editor.getComponent();
        panel.add(editorComponent, BorderLayout.CENTER);

        final DefaultActionGroup group = new DefaultActionGroup();
        for (AnAction action : catdeaLogcatView.getActions()) {
            group.add(action);
        }
        group.addSeparator();

        final ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("CatdeaLogcatPanel", group, false);
        toolbar.setTargetComponent(editorComponent);
        panel.add(toolbar.getComponent(), BorderLayout.WEST);

        RunnerLayoutUi ui = RunnerLayoutUi.Factory.getInstance(project).create("Catdea", "Catdea", "Catdea", project);

        Content content = ui.createContent("Catdea Logcat", panel, "Catdea", CatdeaIcons.TOOL, editorComponent);
        content.setCloseable(false);
        content.setDisposer(catdeaLogcatView);

        ui.addContent(content, 0, PlaceInGrid.center, false);

        final JPanel devicesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        devicesPanel.add(devices.getDeviceComboBox());
        devicesPanel.add(devices.getClientComboBox());

        add(devicesPanel, BorderLayout.NORTH);
        add(ui.getComponent(), BorderLayout.CENTER);
    }
}
