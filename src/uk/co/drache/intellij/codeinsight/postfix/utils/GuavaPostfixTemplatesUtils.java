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

import com.intellij.openapi.util.Condition;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.InheritanceUtil;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.getTopmostExpression;
import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.isArray;
import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.isIterable;
import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.isNumber;
import static uk.co.drache.intellij.codeinsight.postfix.utils.ImportUtils.hasImportStatic;

/**
 * Collection of helper methods for surrounding an expression with guava preconditions.
 *
 * @author Bob Browning
 */
public class GuavaPostfixTemplatesUtils {

  /**
   * Condition that returns true if the element is a {@link java.lang.CharSequence}.
   */
  public static final Condition<PsiElement> IS_CHAR_SEQUENCE = new Condition<PsiElement>() {
    @Override
    public boolean value(PsiElement element) {
      return element instanceof PsiExpression
             && isCharSequence(((PsiExpression) element).getType());
    }
  };

  /**
   * Condition that returns true if the element is an iterable or an iterator or an array.
   */
  public static final Condition<PsiElement> IS_ARRAY_OR_ITERABLE_OR_ITERATOR = new Condition<PsiElement>() {
    @Override
    public boolean value(PsiElement element) {
      if (element instanceof PsiExpression) {
        PsiType type = ((PsiExpression) element).getType();
        return isIterator(type) || isArray(type) || isIterable(type);
      }
      return false;
    }
  };

  /**
   * Condition that returns true if the element is an iterable or an iterator or an object array or a collection.
   */
  public static final Condition<PsiElement> IS_NON_PRIMITIVE_ARRAY_OR_ITERABLE_OR_ITERATOR =
      new Condition<PsiElement>() {
        @Override
        public boolean value(PsiElement element) {
          if (element instanceof PsiExpression) {
            PsiType type = ((PsiExpression) element).getType();
            return isObjectArrayTypeExpression(type)
                   || isIterable(type)
                   || isIterator(type);
          }
          return false;
        }
      };

  @NonNls
  private static final String JAVA_LANG_CHAR_SEQUENCE = "java.lang.CharSequence";

  @Contract("null -> false")
  public static boolean isCollection(@Nullable PsiType type) {
    return type != null && InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_UTIL_COLLECTION);
  }

  @Contract("null -> false")
  public static boolean isIterator(@Nullable PsiType type) {
    return type != null && InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_UTIL_ITERATOR);
  }


  @Contract("null -> false")
  public static boolean isCharSequence(@Nullable PsiType type) {
    return type != null && InheritanceUtil.isInheritor(type, JAVA_LANG_CHAR_SEQUENCE);
  }

  @Contract("null -> false")
  public static boolean isObjectArrayTypeExpression(@Nullable PsiType type) {
    return type instanceof PsiArrayType &&
           !(((PsiArrayType) type).getComponentType() instanceof PsiPrimitiveType);
  }

  /**
   * Gets the expression for acquiring the size of the specified expression.
   *
   * @param expr The expression to evaluate
   */
  @Nullable
  public static String getExpressionNumberOrArrayOrIterableBound(@NotNull PsiExpression expr) {
    PsiType type = expr.getType();
    if (isNumber(type)) {
      return expr.getText();
    } else if (isArray(type)) {
      return expr.getText() + ".length";
    } else if (isCollection(type)) {
      return expr.getText() + ".size()";
    } else if (isIterable(type)) {
      return "com.google.common.collect.Iterables.size(" + expr.getText() + ")";
    }
    return null;
  }

  /**
   * Get the prefix required for the specified imported static method within the current context.
   *  @param fqClassName The qualified class name
   * @param methodName  The method name
   * @param context     The current context
   */
  public static String getStaticMethodPrefix(@NotNull String fqClassName, @NotNull String methodName,
                                             @NotNull PsiElement context) {
    return hasImportStatic(fqClassName, methodName, context) ? methodName : (fqClassName + "." + methodName);
  }

  public static boolean isTopmostExpression(@NotNull PsiElement element) {
    return element.equals(getTopmostExpression(element));
  }
}
