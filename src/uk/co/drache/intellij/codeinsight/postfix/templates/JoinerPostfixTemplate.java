package uk.co.drache.intellij.codeinsight.postfix.templates;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.codeInsight.template.postfix.templates.ExpressionPostfixTemplateWithChooser;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiExpression;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.intellij.codeInsight.template.postfix.util.PostfixTemplatesUtils.isArray;
import static com.intellij.codeInsight.template.postfix.util.PostfixTemplatesUtils.isIterable;
import static uk.co.drache.intellij.codeinsight.postfix.utils.GuavaPostfixTemplatesUtils.isIterator;

/**
 * @author Bob Browning
 */
public class JoinerPostfixTemplate extends ExpressionPostfixTemplateWithChooser {

  @NonNls
  public static final String DESCRIPTION = "Joins pieces of text (specified as an array, Iterable, varargs or even a Map) with a separator";

  @NonNls
  private static final String EXAMPLE = "Joiner.on(',').join(array)";

  @NonNls
  private static final String POSTFIX_COMMAND = "join";

  public JoinerPostfixTemplate() {
    super(POSTFIX_COMMAND, DESCRIPTION, EXAMPLE);
  }

  @Override
  protected void doIt(@NotNull Editor editor, @NotNull PsiExpression expr) {
    Project project = expr.getProject();
    Document document = editor.getDocument();

    document.deleteString(expr.getTextRange().getStartOffset(), expr.getTextRange().getEndOffset());
    TemplateManager manager = TemplateManager.getInstance(project);

    Template template = manager.createTemplate("", "");
    template.setToIndent(true);
    template.setToShortenLongNames(true);
    template.setToReformat(true);

    template.addTextSegment("com.google.common.base.Joiner.on");
    template.addTextSegment("(");
    template.addVariable("separator", new TextExpression("','"), true);
    template.addTextSegment(")");
    template.addTextSegment(".join(");
    template.addTextSegment(expr.getText());
    template.addTextSegment(")");
    template.addEndVariable();

    manager.startTemplate(editor, template);
  }

  @NotNull
  @Override
  protected Condition<PsiExpression> getTypeCondition() {
    return new Condition<PsiExpression>() {
      @Override
      public boolean value(PsiExpression expr) {
        return expr != null && (isIterator(expr.getType()) ||
                                isArray(expr.getType()) ||
                                isIterable(expr.getType()));
      }
    };
  }
}
