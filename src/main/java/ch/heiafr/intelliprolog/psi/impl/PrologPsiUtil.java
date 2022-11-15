package ch.heiafr.intelliprolog.psi.impl;

import ch.heiafr.intelliprolog.psi.PrologTypes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

import static ch.heiafr.intelliprolog.psi.PrologTypes.KNOWN_BINARY_OPERATOR;
import static ch.heiafr.intelliprolog.psi.PrologTypes.KNOWN_LEFT_OPERATOR;

public class PrologPsiUtil {

    public static IElementType getElementType(PsiElement element) {
        return element.getNode().getElementType();
    }

    public static boolean isOperator(PsiElement element) {
        return isKnownBinaryOperator(element) || isKnownLeftOperator(element);
    }

    public static boolean isKnownBinaryOperator(PsiElement element) {
        return getElementType(element).equals(KNOWN_BINARY_OPERATOR);
    }

    public static boolean isKnownLeftOperator(PsiElement element) {
        return getElementType(element).equals(KNOWN_LEFT_OPERATOR);
    }

    public static boolean isAtomKeyword(PsiElement element) {
        return getElementType(element).equals(PrologTypes.ATOM) && Constants.KEYWORDS.contains(element.getText());
    }

    public static boolean isCompoundNameKeyword(PsiElement element) {
        return getElementType(element).equals(PrologTypes.COMPOUND_NAME) && Constants.KEYWORDS.contains(element.getText());
    }

    public static boolean isCompoundName(PsiElement element) {
        return getElementType(element).equals(PrologTypes.COMPOUND_NAME);
    }

    public static boolean isUserCompoundName(PsiElement element) {
        return isCompoundName(element) && !isCompoundNameKeyword(element);
    }
}
