package ch.heiafr.intelliprolog.psi.impl.decorator;

import ch.heiafr.intelliprolog.psi.PrologSentence;
import com.intellij.psi.PsiElement;

public class SentenceDecorator extends PsiElementDecorator {

    private SentenceDecorator(PsiElement psiElement) {
        super(psiElement);
    }

    public static boolean isSentence(PsiElement psiElement) {
        return psiElement instanceof PrologSentence;
    }

    public static SentenceDecorator sentenceDecorator(PsiElement psiElement) {
        if (!isSentence(psiElement)) {
            throw new WrongPsiElementException(psiElement, PrologSentence.class);
        }
        return psiElement instanceof SentenceDecorator ? (SentenceDecorator) psiElement : new SentenceDecorator(psiElement);
    }

}
