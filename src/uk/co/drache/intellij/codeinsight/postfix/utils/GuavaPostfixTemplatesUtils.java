package uk.co.drache.intellij.codeinsight.postfix.utils;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.InheritanceUtil;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Collection of helper methods for surrounding an expression with guava preconditions.
 *
 * @author Bob Browning
 */
public class GuavaPostfixTemplatesUtils {

  @NonNls
  public static final String GUAVA_PRECONDITIONS = "com.google.common.base.Preconditions";

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

  public static void createStatement(@NotNull PsiElement context,
                                     @NotNull Editor editor,
                                     @NotNull String prefix,
                                     @NotNull String suffix, int offset) {
    PsiExpression expr = PostfixTemplate.getTopmostExpression(context);
    PsiElement parent = expr != null ? expr.getParent() : null;
    assert parent instanceof PsiStatement;
    PsiElementFactory factory = JavaPsiFacade.getInstance(context.getProject()).getElementFactory();
    PsiStatement statement = factory.createStatementFromText(prefix + expr.getText() + suffix + ";", parent);
    PsiElement replace = parent.replace(statement);
    editor.getCaretModel().moveToOffset(replace.getTextRange().getEndOffset() + offset);
  }

}
