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

package com.cybrosis.catdea.markers;

import com.cybrosis.catdea.CatdeaService;
import icons.CatdeaIcons;
import com.cybrosis.catdea.lang.psi.PsiCatdeaEntry;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cybrosis
 */
@SuppressWarnings("SameParameterValue")
public class CatdeaGotoLogMarkerProvider extends RelatedItemLineMarkerProvider {
    private final Option option = new Option("catdea.enable", "Log emitter", CatdeaIcons.Gutter.ICON);

    private static final String ACTION_ID = "GotoRelated";


    @Override
    public String getName() {
        return "Catdea line markers";
    }

    @NotNull
    @Override
    public Option[] getOptions() {
        return new Option[]{option};
    }

    @Override
    public void collectNavigationMarkers(@NotNull List<PsiElement> elements,
                                         @NotNull Collection<? super RelatedItemLineMarkerInfo> result,
                                         boolean forNavigation) {
        if (available(elements)) {
            super.collectNavigationMarkers(elements, result, false);
        }
    }

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
                                            @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if (!(element instanceof PsiMethodCallExpression)) return;
        final PsiMethodCallExpression call = (PsiMethodCallExpression) element;

        final PsiElement leaf = PsiTreeUtil.getDeepestVisibleFirst(call);
        if (leaf == null) return;

        final Collection<PsiCatdeaEntry> targets = CatdeaService.getInstance(call.getProject()).findEmittedLogsBy(call);
        if (targets == null) return;

        final RelatedItemLineMarkerInfo info = NavigationGutterIconBuilder
                .create(CatdeaIcons.Gutter.getIcon(targets.isEmpty()))
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setPopupTitle("Choose Log of " + (call).getMethodExpression().getText())
                .setTooltipText(createTooltip("Logged in:<br>", "Logs not found", targets))
                .setTargets(targets)
                .createLineMarkerInfo(leaf);

        result.add(info);
    }

    private boolean available(Collection<PsiElement> elements) {
        if (!option.isEnabled()) return false;

        final PsiElement first = ContainerUtil.getFirstItem(elements);
        if (first == null) return false;

        return CatdeaService.getInstance(first.getProject()).available();
    }


    @NotNull
    private static String createTooltip(@NotNull String start,
                                        @NotNull String otherwise,
                                        @NotNull Collection<? extends PsiElement> targets) {
        if (targets.isEmpty()) return otherwise;

        final Set<String> names = targets
                .stream()
                .map(PsiElement::getContainingFile)
                .filter(Objects::nonNull)
                .map(file -> MessageFormat.format("<a href=\"#element/{0}\">{0}</a>", file.getName()))
                .collect(Collectors.toSet());

        final String shortcut = getShortcut(ACTION_ID);

        final StringBuilder result = new StringBuilder();
        result.append("<html><body>");
        result.append(start);

        String sep = "";
        for (String name : names) {
            result.append(sep).append(name);
            sep = "<br>";
        }

        result.append("<br>")
              .append("<div style='margin-top: 8px'>")
              .append("<font size='2' color='#787878'>")
              .append("Click").append(shortcut == null ? "" : " or press " + shortcut).append(" to navigate")
              .append("</font>")
              .append("</div>");

        result.append("</body></html>");
        return result.toString();
    }


    @Nullable
    private static String getShortcut(@NotNull String actionId) {
        final Shortcut[] shortcuts = ActionManager.getInstance().getAction(actionId).getShortcutSet().getShortcuts();
        final Shortcut shortcut = ArrayUtil.getFirstElement(shortcuts);
        return shortcut == null ? null : KeymapUtil.getShortcutText(shortcut);
    }
}
