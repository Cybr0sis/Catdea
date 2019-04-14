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
package com.cybrosis.catdea.lang.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.cybrosis.catdea.lang.CatdeaTypes.*;
import com.cybrosis.catdea.lang.psi.*;

public class PsiCatdeaLevelImpl extends PsiCatdeaLevelMixin implements PsiCatdeaLevel {

  public PsiCatdeaLevelImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiCatdeaVisitor visitor) {
    visitor.visitLevel(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PsiCatdeaVisitor) accept((PsiCatdeaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getLevelToken() {
    return findNotNullChildByType(LEVEL_TOKEN);
  }

}
