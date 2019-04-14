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

package com.cybrosis.catdea.settings;

import com.cybrosis.catdea.highlighting.syntax.CatdeaColors;
import com.cybrosis.catdea.highlighting.syntax.CatdeaSyntaxHighlighter;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cybrosis
 */
public class CatdeaColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Log Entry//Comment", CatdeaColors.COMMENT),
            new AttributesDescriptor("Log Entry//Unused", CatdeaColors.UNUSED),

            new AttributesDescriptor("Log Entry//Header//Timestamp", CatdeaColors.TIMESTAMP),
            new AttributesDescriptor("Log Entry//Header//Pid", CatdeaColors.PID),
            new AttributesDescriptor("Log Entry//Header//Package", CatdeaColors.PACKAGE),
            new AttributesDescriptor("Log Entry//Header//Level", CatdeaColors.LEVEL),
            new AttributesDescriptor("Log Entry//Header//Tag", CatdeaColors.TAG),

            new AttributesDescriptor("Log Entry//Message//Number", CatdeaColors.NUMBER),
            new AttributesDescriptor("Log Entry//Message//Java Keywords", CatdeaColors.JAVA_KEYWORDS),

            new AttributesDescriptor("Level//Verbose", CatdeaColors.LEVEL_VERBOSE),
            new AttributesDescriptor("Level//Debug", CatdeaColors.LEVEL_DEBUG),
            new AttributesDescriptor("Level//Info", CatdeaColors.LEVEL_INFO),
            new AttributesDescriptor("Level//Warn", CatdeaColors.LEVEL_WARN),
            new AttributesDescriptor("Level//Error", CatdeaColors.LEVEL_ERROR),
            new AttributesDescriptor("Level//Assert", CatdeaColors.LEVEL_ASSERT),

            new AttributesDescriptor("Separators//Colon", CatdeaColors.COLON),
            new AttributesDescriptor("Separators//Semicolon", CatdeaColors.SEMICOLON),
            new AttributesDescriptor("Separators//Dot", CatdeaColors.DOT),
            new AttributesDescriptor("Separators//Comma", CatdeaColors.COMMA),

            new AttributesDescriptor("Braces//Parentheses", CatdeaColors.PARENTHESES),
            new AttributesDescriptor("Braces//Braces", CatdeaColors.BRACES),
            new AttributesDescriptor("Braces//Brackets", CatdeaColors.BRACKETS),
    };

    public static final Map<String, TextAttributesKey> ADDITIONAL = new HashMap<>(7);
    static {
        ADDITIONAL.put("v", CatdeaColors.LEVEL_VERBOSE);
        ADDITIONAL.put("d", CatdeaColors.LEVEL_DEBUG);
        ADDITIONAL.put("i", CatdeaColors.LEVEL_INFO);
        ADDITIONAL.put("w", CatdeaColors.LEVEL_WARN);
        ADDITIONAL.put("e", CatdeaColors.LEVEL_ERROR);
        ADDITIONAL.put("a", CatdeaColors.LEVEL_ASSERT);
        ADDITIONAL.put("unused", CatdeaColors.UNUSED);

        ADDITIONAL.put("timestamp", CatdeaColors.TIMESTAMP);

        ADDITIONAL.put("pid", CatdeaColors.PID);
        ADDITIONAL.put("package", CatdeaColors.PACKAGE);
        ADDITIONAL.put("level", CatdeaColors.LEVEL);
        ADDITIONAL.put("tag", CatdeaColors.TAG);
        ADDITIONAL.put("number", CatdeaColors.NUMBER);
        ADDITIONAL.put("keyword", CatdeaColors.JAVA_KEYWORDS);
        ADDITIONAL.put("colon", CatdeaColors.COLON);
        ADDITIONAL.put("semicolon", CatdeaColors.SEMICOLON);
        ADDITIONAL.put("dot", CatdeaColors.DOT);
        ADDITIONAL.put("comma", CatdeaColors.COMMA);
        ADDITIONAL.put("parentheses", CatdeaColors.PARENTHESES);
        ADDITIONAL.put("braces", CatdeaColors.BRACES);
        ADDITIONAL.put("brackets", CatdeaColors.BRACKETS);
    }

    private final static String DEMO_TEXT =
            "<v><timestamp>12-04 15:35:49.199</timestamp> <pid>115-115</pid><package>/com.cybrosis.sample</package> <level>V/</level><tag>MainActivity</tag><colon>:</colon> onCreate<parentheses>()</parentheses><semicolon>;</semicolon>\n</v>" +
            "<i><timestamp>12-04 15:35:49.251</timestamp> <pid>115-115</pid><package>/com.cybrosis.sample</package> <level>I/</level><tag>MainActivity</tag><colon>:</colon> has all permissions<colon>:</colon> <keyword>true</keyword>\n</i>" +
            "<i><timestamp>12-04 15:35:49.251</timestamp> <pid>115-115</pid><package>/com.cybrosis.sample</package> <level>I/</level><tag>MainActivity</tag><colon>:</colon> App version = <brackets>[</brackets><number>2</number><dot>.</dot><number>5</number><dot>.</dot><number>12</number><brackets>]</brackets>\n</i>" +
            "<d><timestamp>12-04 15:35:49.251</timestamp> <pid>115-115</pid><package>/com.cybrosis.sample</package> <level>D/</level><tag>Controller</tag><colon>:</colon> status = com.cybrosis.sample.Controller@<number>1400031</number>\n</d>" +
            "# take a look at this\n" +
            "<w><timestamp>12-04 15:35:49.165</timestamp> <pid>115-115</pid><package>/com.cybrosis.sample</package> <level>W/</level><tag>Controller</tag><colon>:</colon> Permission Name<colon>:</colon> PermissionInfo<braces>{</braces><number>338e187</number> android.permission.READ_EXTERNAL_STORAGE<braces>}</braces>\n</w>" +
            "<e><timestamp>12-04 15:35:51.231</timestamp> <pid>115-115</pid><package>/com.cybrosis.sample</package> <level>E/</level><tag>Controller</tag><colon>:</colon> status = <keyword>null</keyword>\n</e>" +
            "<a><timestamp>12-04 15:35:51.311</timestamp> <pid>115-115</pid><package>/com.cybrosis.sample</package> <level>A/</level><tag>MainActivity</tag><colon>:</colon> Expected <keyword>true</keyword> but was <keyword>false</keyword>\n</a>" +
            "<unused>12-04 17:14:37.512 115-115/com.cybrosis.sample I/art: Debugger is no longer active</unused>\n";

    @Nullable
    @Override
    public Icon getIcon() {
        return null;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Catdea";
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new CatdeaSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return DEMO_TEXT;
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return ADDITIONAL;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }
}
