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

package com.cybrosis.catdea.icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @author cybrosis
 */
public class CatdeaIcons {
    public static final Icon FILE_TYPE = IconLoader.getIcon("/icons/file.svg");

        public final static class Gutter {
            public static final Icon ICON = IconLoader.getIcon("icons/gutter.svg");
            public static final Icon NONE = IconLoader.getIcon("icons/gutter_none.svg");

            public static Icon getIcon(boolean isNone) {
                return isNone ? NONE : ICON;
            }
        }
    }
