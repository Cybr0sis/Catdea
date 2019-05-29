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
package com.cybrosis.catdea.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;

import static com.cybrosis.catdea.lang.CatdeaTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class CatdeaParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t instanceof IFileElementType) {
      r = parse_root_(t, b, 0);
    }
    else {
      r = false;
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return file(b, l + 1);
  }

  /* ********************************************************** */
  // BUFFER line*
  public static boolean buffer(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "buffer")) return false;
    if (!nextTokenIs(b, BUFFER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, BUFFER, null);
    r = consumeToken(b, BUFFER);
    p = r; // pin = 1
    r = r && buffer_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // line*
  private static boolean buffer_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "buffer_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "buffer_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // header message
  public static boolean entry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entry")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ENTRY, "<entry>");
    r = header(b, l + 1);
    p = r; // pin = 1
    r = r && message(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (buffer | line)*
  static boolean file(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file")) return false;
    while (true) {
      int c = current_position_(b);
      if (!file_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "file", c)) break;
    }
    return true;
  }

  // buffer | line
  private static boolean file_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file_0")) return false;
    boolean r;
    r = buffer(b, l + 1);
    if (!r) r = line(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // info level tag COLON
  public static boolean header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "header")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HEADER, "<header>");
    r = info(b, l + 1);
    r = r && level(b, l + 1);
    r = r && tag(b, l + 1);
    r = r && consumeToken(b, COLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // [TIMESTAMP_TOKEN] [PID_TOKEN [SLASH PACKAGE_TOKEN]]
  static boolean info(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "info")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = info_0(b, l + 1);
    r = r && info_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // [TIMESTAMP_TOKEN]
  private static boolean info_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "info_0")) return false;
    consumeToken(b, TIMESTAMP_TOKEN);
    return true;
  }

  // [PID_TOKEN [SLASH PACKAGE_TOKEN]]
  private static boolean info_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "info_1")) return false;
    info_1_0(b, l + 1);
    return true;
  }

  // PID_TOKEN [SLASH PACKAGE_TOKEN]
  private static boolean info_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "info_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PID_TOKEN);
    r = r && info_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // [SLASH PACKAGE_TOKEN]
  private static boolean info_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "info_1_0_1")) return false;
    parseTokens(b, 0, SLASH, PACKAGE_TOKEN);
    return true;
  }

  /* ********************************************************** */
  // LEVEL_TOKEN
  public static boolean level(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "level")) return false;
    if (!nextTokenIs(b, LEVEL_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEVEL_TOKEN);
    exit_section_(b, m, LEVEL, r);
    return r;
  }

  /* ********************************************************** */
  // entry | MESSAGE_TOKEN
  static boolean line(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line")) return false;
    boolean r;
    r = entry(b, l + 1);
    if (!r) r = consumeToken(b, MESSAGE_TOKEN);
    return r;
  }

  /* ********************************************************** */
  // MESSAGE_TOKEN+
  public static boolean message(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "message")) return false;
    if (!nextTokenIs(b, MESSAGE_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MESSAGE_TOKEN);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, MESSAGE_TOKEN)) break;
      if (!empty_element_parsed_guard_(b, "message", c)) break;
    }
    exit_section_(b, m, MESSAGE, r);
    return r;
  }

  /* ********************************************************** */
  // TAG_TOKEN
  public static boolean tag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag")) return false;
    if (!nextTokenIs(b, TAG_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TAG_TOKEN);
    exit_section_(b, m, TAG, r);
    return r;
  }

}
