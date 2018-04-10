package ch.eif.intelliprolog.psi.impl.decorator;

import ch.eif.intelliprolog.psi.PrologAtom;
import com.intellij.psi.PsiElement;

public class AtomDecorator extends PsiElementDecorator {

    private AtomDecorator(PsiElement psiElement) {
        super(psiElement);
    }

    public static boolean isAtom(PsiElement psiElement) {
        return psiElement instanceof PrologAtom;
    }

    public static AtomDecorator atomDecorator(PsiElement psiElement) {
        if (!isAtom(psiElement)) {
            throw new WrongPsiElementException(psiElement, PrologAtom.class);
        }
        return psiElement instanceof AtomDecorator ? (AtomDecorator) psiElement : new AtomDecorator(psiElement);
    }

/*    public static AtomDecorator fromTerm(PsiElement psiElement) {
        if (isBasicTerm(psiElement)) {
            return fromBasicTerm(psiElement);
        } else if (isTerm(psiElement)) {
            return fromBasicTerm(psiElement.getFirstChild());
        } else {
            throw new WrongPsiElementException(psiElement, LogtalkBasicTerm.class, LogtalkTerm.class);
        }
    }*/

    private static AtomDecorator fromBasicTerm(PsiElement psiElement) {
        return atomDecorator(psiElement.getFirstChild());
    }

    public String getAtomText() {
        return getText();
    }

}
