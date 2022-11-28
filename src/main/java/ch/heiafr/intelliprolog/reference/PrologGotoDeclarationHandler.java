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
        Collection<PsiElement> files = findEveryImportedFile(elt, new ArrayList<>());
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


        for(PsiElement name : names){
            System.out.println(name.getText()+ "\t => \t" + ReferenceHelper.getArity(name));
        }

        PrologSentence currentSentence = PsiTreeUtil.getParentOfType(elt, PrologSentence.class);
        if(names.contains(ReferenceHelper.findDefinition(currentSentence))) {
            return null; //Display usage instead of declaration
        }
        //Find the compound name that matches the current one
        return names.toArray(PsiElement.EMPTY_ARRAY);
    }

    /**
     * Find all the files that are imported in the current file and all the files that are imported in those files recursively
     *
     * @param elt   The current element
     * @param files The list of files that are imported
     * @return The list of PsiElements that are used in the current file or in the files that are imported in the current file
     */
    private Collection<PsiElement> findEveryImportedFile(PsiElement elt, Collection<String> files) {
        if (elt == null) {
            return new ArrayList<>();
        }

        Collection<String> paths = PsiTreeUtil.collectElementsOfType(elt.getContainingFile(), PrologSentence.class).stream()
                .map(this::findIncludeStatement)// Find the first compound name which is the predicate name
                .filter(Objects::nonNull) //Prevent null values
                .map(ReferenceHelper::extractQuotedString)//Extract the quoted string
                .filter(Objects::nonNull)//Prevent null values
                .filter(s -> !files.contains(s)) //Filter out already visited files
                .collect(Collectors.toList()); //Collect to list

        files.addAll(paths); //Add the new paths to the list of visited files to prevent infinite recursion

        Collection<PsiElement> psiFiles = new ArrayList<>(); //Create a new list of psi files
        for (String path : paths) {
            PsiElement rootElt = pathToPsi(elt, path); //Get the psi element from the path
            psiFiles.add(rootElt); //Add the psi element to the list
            psiFiles.addAll(findEveryImportedFile(rootElt, files)); //Find imported files recursively
        }
        return psiFiles;
    }

    private PsiElement findIncludeStatement(PrologSentence sentence) {
        return PsiTreeUtil.collectElementsOfType(sentence, PrologCompound.class).stream()
                .filter(Objects::nonNull)
                .filter(c -> Objects.equals(c.getCompoundName().getName(), "include"))
                .findFirst()
                .orElse(null);
    }


    /**
     * Extract PsiElement from a relative path
     *
     * @param elt  The element to use as a reference for the path
     * @param path The relative path
     * @return The PsiElement of the file at the given path or null if the file does not exist
     */
    private PsiElement pathToPsi(PsiElement elt, String path) {

        Path basePath = Paths.get(elt.getContainingFile().getVirtualFile().getPath()).getParent(); //Get the base path
        Path filePath = basePath.resolve(path); //Resolve the path
        VirtualFile file = VirtualFileManager.getInstance().findFileByNioPath(filePath); //Get the virtual file
        if (file == null) {
            return null;
        }
        return PsiManager.getInstance(elt.getProject()).findFile(file);
    }
}
