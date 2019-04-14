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

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.cybrosis.catdea.lang.psi.CatdeaElementType;
import com.cybrosis.catdea.lang.lexer.CatdeaTokenType;
import com.cybrosis.catdea.lang.psi.impl.*;

public interface CatdeaTypes {

  IElementType ENTRY = new CatdeaElementType("ENTRY");
  IElementType HEADER = new CatdeaElementType("HEADER");
  IElementType LEVEL = new CatdeaElementType("LEVEL");
  IElementType MESSAGE = new CatdeaElementType("MESSAGE");
  IElementType TAG = new CatdeaElementType("TAG");

  IElementType COLON = new CatdeaTokenType(": ");
  IElementType COMMENT = new CatdeaTokenType("COMMENT");
  IElementType LEVEL_TOKEN = new CatdeaTokenType("LEVEL_TOKEN");
  IElementType MESSAGE_TOKEN = new CatdeaTokenType("MESSAGE_TOKEN");
  IElementType PACKAGE_TOKEN = new CatdeaTokenType("PACKAGE_TOKEN");
  IElementType PID_TOKEN = new CatdeaTokenType("PID_TOKEN");
  IElementType SLASH = new CatdeaTokenType("/");
  IElementType TAG_TOKEN = new CatdeaTokenType("TAG_TOKEN");
  IElementType TIMESTAMP_TOKEN = new CatdeaTokenType("TIMESTAMP_TOKEN");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ENTRY) {
        return new PsiCatdeaEntryImpl(node);
      }
      else if (type == HEADER) {
        return new PsiCatdeaHeaderImpl(node);
      }
      else if (type == LEVEL) {
        return new PsiCatdeaLevelImpl(node);
      }
      else if (type == MESSAGE) {
        return new PsiCatdeaMessageImpl(node);
      }
      else if (type == TAG) {
        return new PsiCatdeaTagImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
