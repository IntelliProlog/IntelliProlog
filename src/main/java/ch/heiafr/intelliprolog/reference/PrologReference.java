package ch.heiafr.intelliprolog.reference;

import ch.heiafr.intelliprolog.PrologIcons;
import ch.heiafr.intelliprolog.psi.PrologCompoundName;
import ch.heiafr.intelliprolog.psi.impl.PrologPsiUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrologReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final String name;

    public PrologReference(@NotNull PsiElement element) {
        super(element, element.getTextRange());
        name = element.getText();
        System.out.println("PrologReference: " + name);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        final List<PrologCompoundName> properties = PrologPsiUtil.findCompoundNames(project, name);
        List<ResolveResult> results = new ArrayList<>();
        for (PrologCompoundName property : properties) {
            results.add(new PsiElementResolveResult(property));
        }

        return results.toArray(ResolveResult.EMPTY_ARRAY);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @Override
    public Object @NotNull [] getVariants() {
        Project project = myElement.getProject();
        List<PrologCompoundName> names = PrologPsiUtil.findCompoundNames(project, null);
        List<LookupElement> variants = new ArrayList<>();
        for (final PrologCompoundName name : names) {
            if (name.getName() != null && name.getName().length() > 0) {
                variants.add(LookupElementBuilder
                        .create(name).withIcon(PrologIcons.FILE)
                        .withTypeText(name.getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }
}
