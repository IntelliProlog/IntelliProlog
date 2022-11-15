package ch.heiafr.intelliprolog.reference;

import ch.heiafr.intelliprolog.PrologLanguage;
import ch.heiafr.intelliprolog.psi.PrologAtom;
import ch.heiafr.intelliprolog.psi.PrologCompound;
import ch.heiafr.intelliprolog.psi.PrologCompoundName;
import ch.heiafr.intelliprolog.psi.PrologSentence;
import ch.heiafr.intelliprolog.psi.impl.PrologPsiUtil;
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

        //For each sentence, find the first compound name => the predicate name
        Collection<PrologCompound> compoundNames = declarations.stream()
                .map(this::findCompoundAndName)//Find the first compound name which is the predicate name
                .filter(Objects::nonNull) //Prevent null values
                .filter(c -> PrologPsiUtil.isUserCompoundName(c.compoundName)) //Filter out keywords
                .filter(c -> c.compoundName.getText().equals(elt.getText())) //Filter by name
                .filter(c -> !elt.equals(c.compoundName)) //Filter by not being the same element
                .map(c -> c.compound) //Get the compound name
                .collect(Collectors.toList()); //Collect to list


        //Find the compound name that matches the current one
        return compoundNames.toArray(PsiElement.EMPTY_ARRAY);
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
                .map(this::findCompoundAndName)// Find the first compound name which is the predicate name
                .filter(Objects::nonNull) //Prevent null values
                .filter(c -> c.compoundName.getText().equals("include")) //Keep only include
                .map(c -> c.compound) //Get the compound name
                .map(this::extractQuotedString)//Extract the quoted string
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

    /**
     * Extract the quoted string from a compound
     *
     * @param compound The compound
     * @return The String without the quotes or null if no string was found
     */
    private String extractQuotedString(PrologCompound compound) {
        PrologAtom atom = PsiTreeUtil.findChildOfType(compound, PrologAtom.class);
        if (atom == null) {
            return null;
        }
        String name = atom.getText();
        name = name.replaceAll("'", "");
        name = name.replaceAll("\"", "");
        return name;
    }

    /**
     * Find the first compound name which is the predicate name
     *
     * @param sentence The sentence to search in
     * @return The compound name or null if not found
     */
    private CompoundWithCompoundName findCompoundAndName(PrologSentence sentence) {
        //Find the first compound name
        PrologCompound compound = PsiTreeUtil.findChildOfType(sentence, PrologCompound.class);

        //If there is no compound, return null
        if (compound == null) {
            return null;
        }

        //Find the first compound name
        PrologCompoundName compoundName = PsiTreeUtil.findChildOfType(compound, PrologCompoundName.class);

        //If there is no compound name, return null
        if (compoundName == null) {
            return null;
        }


        return new CompoundWithCompoundName(compoundName, compound);
    }

    /**
     * A simple class to store a compound name and its parent compound
     * This is used to avoid having to find the compound name again
     */
    private class CompoundWithCompoundName {
        PrologCompoundName compoundName; //The compound name
        PrologCompound compound; //The compound

        /**
         * Create a new compound with compound name
         *
         * @param compoundName The compound name
         * @param compound     The compound
         */
        public CompoundWithCompoundName(PrologCompoundName compoundName, PrologCompound compound) {
            this.compoundName = compoundName;
            this.compound = compound;
        }
    }
}
