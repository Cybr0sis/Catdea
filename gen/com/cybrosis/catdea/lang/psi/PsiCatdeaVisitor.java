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
package com.cybrosis.catdea.lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class PsiCatdeaVisitor extends PsiElementVisitor {

  public void visitEntry(@NotNull PsiCatdeaEntry o) {
    visitPsiElement(o);
  }

  public void visitHeader(@NotNull PsiCatdeaHeader o) {
    visitPsiElement(o);
  }

  public void visitLevel(@NotNull PsiCatdeaLevel o) {
    visitPsiElement(o);
  }

  public void visitMessage(@NotNull PsiCatdeaMessage o) {
    visitPsiElement(o);
  }

  public void visitTag(@NotNull PsiCatdeaTag o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
