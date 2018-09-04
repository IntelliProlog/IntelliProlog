package ch.heiafr.intelliprolog.editor;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import static ch.heiafr.intelliprolog.psi.impl.PrologPsiUtil.*;

public class PrologAnnotator implements Annotator {

    private static boolean shouldAnnotate(PsiElement element) {
        return isKnownBinaryOperator(element) ||
                isKnownLeftOperator(element) ||
                isCompoundNameKeyword(element) ||
                isAtomKeyword(element);
    }

    private static void highlightTokens(PsiElement element, AnnotationHolder holder, PrologSyntaxHighlighter highlighter) {
        TextAttributesKey[] keys = highlighter.getTokenHighlights(element);
        Annotation annotation = holder.createInfoAnnotation(element.getNode(), getMessage(element));
        for (TextAttributesKey key : keys) {
            TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(key);
            annotation.setEnforcedTextAttributes(attributes);
        }
    }

    private static String getMessage(PsiElement element) {
        if (isKnownBinaryOperator(element)) {
            return "Binary operator";
        } else if (isKnownLeftOperator(element)) {
            return "Left operator";
        } else if (isCompoundNameKeyword(element)) {
            return "Functor keyword";
        } else if (isAtomKeyword(element)) {
            return "Atom keyword";
        } else {
            throw new AssertionError();
        }
    }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (shouldAnnotate(element)) {
            highlightTokens(element, holder, new PrologSyntaxHighlighter());
        }
    }
}
