/*
 * Copyright (C) 2014 Bob Browning
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.drache.intellij.codeinsight.postfix.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStaticStatement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.ClassUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.siyeh.ig.psiutils.ClassUtils;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.siyeh.ig.psiutils.ImportUtils.hasDefaultImportConflict;
import static com.siyeh.ig.psiutils.ImportUtils.hasOnDemandImportConflict;

/**
 * Collection of method for supporting importing of members.
 *
 * @author Bob Browning
 */
public class ImportUtils {

  /**
   * Check whether the current context has a static member import, either on-demand or explicit.
   *
   * @param fqClassName The class to import from
   * @param memberName  The class member to import
   * @param context     The context to be imported into
   */
  public static boolean hasImportStatic(String fqClassName, String memberName, PsiElement context) {
    final PsiFile file = context.getContainingFile();
    if (!(file instanceof PsiJavaFile)) {
      return false;
    }
    final PsiJavaFile javaFile = (PsiJavaFile) file;
    final PsiImportList importList = javaFile.getImportList();
    if (importList == null) {
      return false;
    }
    final PsiImportStaticStatement[] importStaticStatements = importList.getImportStaticStatements();
    for (PsiImportStaticStatement importStaticStatement : importStaticStatements) {
      if (importStaticStatement.isOnDemand()) {
        PsiClass psiClass = ClassUtils.findClass(fqClassName, context);
        if (psiClass != null && psiClass.equals(importStaticStatement.resolveTargetClass())) {
          return true;
        }
        continue;
      }
      final String name = importStaticStatement.getReferenceName();
      if (!memberName.equals(name)) {
        continue;
      }
      final PsiJavaCodeReferenceElement importReference = importStaticStatement.getImportReference();
      if (importReference == null) {
        continue;
      }
      final PsiElement qualifier = importReference.getQualifier();
      if (qualifier == null) {
        continue;
      }
      final String qualifierText = qualifier.getText();
      if (fqClassName.equals(qualifierText)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check whether the current context has an on-demand static import for the specified class.
   *
   * @param fqClassName The class to import from
   * @param context     The context to be imported into
   */
  public static boolean hasOnDemandImportStatic(String fqClassName, PsiElement context) {
    final PsiFile file = context.getContainingFile();
    if (!(file instanceof PsiJavaFile)) {
      return false;
    }
    final PsiJavaFile javaFile = (PsiJavaFile) file;
    final PsiImportList importList = javaFile.getImportList();
    if (importList == null) {
      return false;
    }
    final PsiImportStaticStatement[] importStaticStatements = importList.getImportStaticStatements();
    for (PsiImportStaticStatement importStaticStatement : importStaticStatements) {
      if (importStaticStatement.isOnDemand()) {
        PsiClass psiClass = ClassUtils.findClass(fqClassName, context);
        if (psiClass != null && psiClass.equals(importStaticStatement.resolveTargetClass())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Check whether the current context has the specified static import.
   *
   * @param fqClassName The class to import from
   * @param memberName  The class member to import
   * @param context     The context to be imported into
   */
  public static boolean hasExactImportStatic(String fqClassName, String memberName, PsiElement context) {
    final PsiFile file = context.getContainingFile();
    if (!(file instanceof PsiJavaFile)) {
      return false;
    }
    final PsiJavaFile javaFile = (PsiJavaFile) file;
    final PsiImportList importList = javaFile.getImportList();
    if (importList == null) {
      return false;
    }
    final PsiImportStaticStatement[] importStaticStatements = importList.getImportStaticStatements();
    for (PsiImportStaticStatement importStaticStatement : importStaticStatements) {
      if (importStaticStatement.isOnDemand()) {
        continue;
      }
      final String name = importStaticStatement.getReferenceName();
      if (!memberName.equals(name)) {
        continue;
      }
      final PsiJavaCodeReferenceElement importReference = importStaticStatement.getImportReference();
      if (importReference == null) {
        continue;
      }
      final PsiElement qualifier = importReference.getQualifier();
      if (qualifier == null) {
        continue;
      }
      final String qualifierText = qualifier.getText();
      if (fqClassName.equals(qualifierText)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Adds a static import.
   *
   * @param qualifierClass The class to import from
   * @param memberName     The class member to import
   * @param context        The context to be imported into
   * @return returns true if the import is statically imported
   */
  public static boolean addStaticImport(String qualifierClass, String memberName, PsiElement context) {
    return hasImportStatic(qualifierClass, memberName, context) ||
           com.siyeh.ig.psiutils.ImportUtils.addStaticImport(qualifierClass, memberName, context);
  }

  /**
   * Determine if there is an import available for the specified class within the context.
   *
   * @param aClass  The class to check for import
   * @param context The context to be imported into
   */
  public static boolean hasImport(@NotNull PsiClass aClass, @NotNull PsiElement context) {
    final PsiFile file = context.getContainingFile();
    if (!(file instanceof PsiJavaFile)) {
      return true;
    }
    final PsiJavaFile javaFile = (PsiJavaFile) file;
    final PsiClass outerClass = aClass.getContainingClass();
    if (outerClass == null) {
      if (PsiTreeUtil.isAncestor(javaFile, aClass, true)) {
        return false;
      }
    } else if (PsiTreeUtil.isAncestor(outerClass, context, true)) {
      final PsiElement brace = outerClass.getLBrace();
      if (brace != null && brace.getTextOffset() < context.getTextOffset()) {
        return false;
      }
    }
    final String qualifiedName = aClass.getQualifiedName();
    if (qualifiedName == null) {
      return true;
    }
    final PsiImportList importList = javaFile.getImportList();
    if (importList == null) {
      return true;
    }
    final String containingPackageName = javaFile.getPackageName();
    @NonNls final String packageName = ClassUtil.extractPackageName(qualifiedName);
    if (containingPackageName.equals(packageName) || importList.findSingleClassImportStatement(qualifiedName) != null) {
      return false;
    }
    //noinspection RedundantIfStatement
    if (importList.findOnDemandImportStatement(packageName) != null &&
        !hasDefaultImportConflict(qualifiedName, javaFile) && !hasOnDemandImportConflict(qualifiedName, javaFile)) {
      return false;
    }
    return true;
  }
}
