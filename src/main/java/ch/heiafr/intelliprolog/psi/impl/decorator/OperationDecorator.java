package ch.heiafr.intelliprolog.psi.impl.decorator;

import ch.heiafr.intelliprolog.psi.PrologNativeBinaryOperation;
import ch.heiafr.intelliprolog.psi.PrologOperation;
import com.intellij.psi.PsiElement;

public class OperationDecorator extends PsiElementDecorator {

    private static final String RULE_OPERATOR = ":-";
    private static final String GRAMMAR__RULE_OPERATOR = "-->";
    private static final String DIRECTIVE_OPERATOR = RULE_OPERATOR;

    private OperationDecorator(PsiElement psiElement) {
        super(psiElement);
    }

    public static boolean isOperation(PsiElement psiElement) {
        return psiElement instanceof PrologOperation;
    }

    public static OperationDecorator operationDecorator(PsiElement psiElement) {
        if (!isOperation(psiElement)) {
            throw new WrongPsiElementException(psiElement, PrologOperation.class);
        }
        return psiElement instanceof OperationDecorator ? (OperationDecorator) psiElement : new OperationDecorator(psiElement);
    }

    private boolean isBinary() {
        return getFirstChild() instanceof PrologNativeBinaryOperation;
    }

    private boolean isLeft() {
        return !isBinary();
    }

    private PsiElement getOperator() {
        if (isLeft()) {
            return getFirstChild().getFirstChild();
        } else {
            return getFirstChild().getChildren()[1];
        }
    }

    public String getOperatorSymbol() {
        return getOperator().getText();
    }

    public boolean isDirective() {
        return isLeft() && getOperatorSymbol().equals(DIRECTIVE_OPERATOR);
    }


    public boolean isRule() {
        return isBinary() && getOperatorSymbol().equals(RULE_OPERATOR);
    }

    public boolean isGrammarRule() {
        return isBinary() && getOperatorSymbol().equals(GRAMMAR__RULE_OPERATOR);
    }

}
