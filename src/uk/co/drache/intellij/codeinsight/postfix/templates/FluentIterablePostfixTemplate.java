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
package uk.co.drache.intellij.codeinsight.postfix.templates;

import com.intellij.codeInsight.template.postfix.templates.ExpressionPostfixTemplateWithChooser;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.JAVA_PSI_INFO;
import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.isIterable;
import static uk.co.drache.intellij.codeinsight.postfix.utils.GuavaClassName.FLUENT_ITERABLE;

/**
 * Postfix template for guava {@code com.google.common.collect.FluentIterable#from(Iterable)}.
 *
 * @author Bob Browning
 */
public class FluentIterablePostfixTemplate extends ExpressionPostfixTemplateWithChooser {

  @NonNls
  private static final String FQ_METHOD_FROM = FLUENT_ITERABLE.getQualifiedStaticMethodName("from");

  public FluentIterablePostfixTemplate() {
    super("fluentIterable", "FluentIterable.from(iterable)", JAVA_PSI_INFO);
  }

  @Override
  protected void doIt(@NotNull Editor editor, @NotNull PsiElement element) {
    PsiElement replacement = myInfo.createExpression(element, FQ_METHOD_FROM + "(", ")");
    JavaCodeStyleManager.getInstance(element.getProject()).shortenClassReferences(replacement);
    element.replace(replacement);
  }

  @NotNull
  @Override
  protected Condition<PsiElement> getTypeCondition() {
    return new Condition<PsiElement>() {
      @Override
      public boolean value(PsiElement element) {
        return element instanceof PsiExpression && isIterable(((PsiExpression) element).getType());
      }
    };
  }
}
