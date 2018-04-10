package ch.eif.intelliprolog.psi.impl.decorator;

import ch.eif.intelliprolog.psi.PrologList;
import com.intellij.psi.PsiElement;

import java.util.Optional;

import static ch.eif.intelliprolog.psi.impl.decorator.SequenceDecorator.isSequence;
import static ch.eif.intelliprolog.psi.impl.decorator.SequenceDecorator.sequenceDecorator;

public class ListDecorator extends PsiElementDecorator {

    private final Optional<SequenceDecorator> optSequence;
    private final Optional<PsiElement> optSingleton;

    private ListDecorator(PsiElement psiElement) {
        super(psiElement);
        SequenceDecorator nestedSequence = null;
        PsiElement singleton = null;

        if (psiElement.getChildren().length == 3) {
            PsiElement middleNode = psiElement.getChildren()[1];
            if (isSequence(middleNode.getFirstChild())) {
                nestedSequence = sequenceDecorator(middleNode.getFirstChild());
            } else {
                singleton = middleNode;
            }
        }
        optSequence = Optional.ofNullable(nestedSequence);
        optSingleton = Optional.ofNullable(singleton);
    }

    public static boolean isList(PsiElement psiElement) {
        return psiElement instanceof PrologList;
    }

    public static ListDecorator listDecorator(PsiElement psiElement) {
        if (!isList(psiElement)) {
            throw new WrongPsiElementException(psiElement, PrologList.class);
        }
        return psiElement instanceof ListDecorator ? (ListDecorator) psiElement : new ListDecorator(psiElement);
    }

    public int size() {
        if (optSequence.isPresent()) {
            return optSequence.get().size();
        } else if (optSingleton.isPresent()) {
            return 1;
        } else {
            return 0;
        }
    }

}
