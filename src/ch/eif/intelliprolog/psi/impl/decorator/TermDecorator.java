package ch.eif.intelliprolog.psi.impl.decorator;

import ch.eif.intelliprolog.psi.PrologBasicTerm;
import ch.eif.intelliprolog.psi.PrologTerm;
import com.intellij.psi.PsiElement;

public class TermDecorator extends PsiElementDecorator {

    private TermDecorator(PsiElement psiElement) {
        super(psiElement);
    }

    public static boolean isTerm(PsiElement psiElement) {
        return psiElement instanceof PrologTerm;
    }

    public static boolean isBasicTerm(PsiElement psiElement) {
        return psiElement instanceof PrologBasicTerm;
    }

    public TermDecorator termDecorator(PsiElement psiElement) {
        if (!isTerm(psiElement)) {
            throw new WrongPsiElementException(psiElement, PrologTerm.class);
        }
        return psiElement instanceof TermDecorator ? (TermDecorator) psiElement : new TermDecorator(psiElement);
    }

}
