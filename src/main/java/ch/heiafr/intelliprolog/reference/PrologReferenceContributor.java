package ch.heiafr.intelliprolog.reference;

import ch.heiafr.intelliprolog.psi.*;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.*;
import static com.intellij.patterns.StandardPatterns.or;

public class PrologReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(or(
                psiComment(),
                psiElement(),
                psiElement(PrologTypes.UNQUOTED_COMPOUND_NAME),
                psiElement(PrologTypes.COMPOUND_NAME),
                psiElement(PrologTypes.QUOTED_COMPOUND_NAME),
                psiElement(PrologTypes.QUOTED_ATOM),
                psiElement(PrologTypes.ATOM)
        ), new PrologReferenceProvider());
    }
}
