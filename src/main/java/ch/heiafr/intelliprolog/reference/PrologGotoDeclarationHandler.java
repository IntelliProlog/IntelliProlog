package ch.heiafr.intelliprolog.reference;

import ch.heiafr.intelliprolog.PrologLanguage;
import ch.heiafr.intelliprolog.psi.*;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class PrologGotoDeclarationHandler implements GotoDeclarationHandler {

    /**
     * Returns the list of targets to navigate to when the user invokes "Go to Declaration" on the specified source element.
     *
     * @param elt    input PSI element
     * @param offset offset in the file
     * @param editor editor instance
     * @return the list of targets to navigate to, or null if the handler is not applicable to the specified element.
     */
    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement elt, int offset, Editor editor) {
        //Guards
        if (elt == null) {
            return null; //No element
        }
        if (!elt.getLanguage().is(PrologLanguage.INSTANCE)) {
            return null; //Not a prolog file
        }

        //Get all psi elements related to the current element
        Collection<PsiElement> files = ReferenceHelper.findEveryImportedFile(elt, new ArrayList<>());
        files.add(elt); //Don't forget the current file

        //Find all declarations of any type
        Collection<PrologSentence> declarations = new ArrayList<>();
        for (PsiElement file : files) {
            declarations.addAll(PsiTreeUtil.findChildrenOfType(file.getContainingFile(), PrologSentence.class));
        }



        int arity = ReferenceHelper.getArityFromClicked(elt);

        //For each sentence, find the first compound name => the predicate name
        Collection<PsiElement> names = declarations.stream()
                .map(ReferenceHelper::findDefinition)
                .filter(Objects::nonNull)
                .filter(compound -> ReferenceHelper.getArity(compound) == arity)
                .filter(c -> c instanceof PrologCompound ?
                        Objects.equals(((PrologCompound) c).getCompoundName().getName(), elt.getText()) :
                        Objects.equals(c.getText(), elt.getText()))
                .collect(Collectors.toList());


        PrologSentence currentSentence = PsiTreeUtil.getParentOfType(elt, PrologSentence.class);
        if(names.contains(ReferenceHelper.findDefinition(currentSentence))) {
            return null; //Display usage instead of declaration
        }
        //Find the compound name that matches the current one
        return names.toArray(PsiElement.EMPTY_ARRAY);
    }

}
