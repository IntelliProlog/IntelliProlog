package ch.heiafr.intelliprolog.reference;

import ch.heiafr.intelliprolog.PrologLanguage;
import ch.heiafr.intelliprolog.psi.impl.PrologPsiUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class PrologReferenceProvider extends PsiReferenceProvider {
    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        //System.out.println("PrologReferenceProvider: " + element.getText());
        if (!element.getLanguage().is(PrologLanguage.INSTANCE)) {
            return PsiReference.EMPTY_ARRAY; // we only want to process Prolog files
        }

        //Process only user defined predicates
        if (PrologPsiUtil.isUserCompoundName(element)) {
            return new PsiReference[]{new PrologReference(element)};
        }


        //All other cases
        return PsiReference.EMPTY_ARRAY;
    }
}
