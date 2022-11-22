package ch.heiafr.intelliprolog.reference;

import ch.heiafr.intelliprolog.PrologLanguage;
import ch.heiafr.intelliprolog.psi.*;
import ch.heiafr.intelliprolog.psi.impl.PrologPsiUtil;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.tree.IElementType;
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

        //Stop here if current element is a declaration
        PrologSentence currentSentence = PsiTreeUtil.getParentOfType(elt, PrologSentence.class);
        if (currentSentence != null && this.findDefinition(currentSentence).equals(elt)) {
            return null;
        }

        //For each sentence, find the first compound name => the predicate name
        Collection<PsiElement> names = declarations.stream()
                .map(this::findDefinition)
                .filter(Objects::nonNull)
                .filter(c -> c instanceof PrologCompound ?
                        Objects.equals(((PrologCompound) c).getCompoundName().getName(), elt.getText()) :
                        Objects.equals(c.getText(), elt.getText()))
                .collect(Collectors.toList());


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

    /**
     * Extract the quoted string from a compound
     *
     * @param compound The compound
     * @return The String without the quotes or null if no string was found
     */
    private String extractQuotedString(PsiElement compound) {
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
    private PsiElement findDefinition(PrologSentence sentence) {
        if (sentence == null) {
            return null;
        }

        //Multiple cases
        // 1. test :- test2. => atom used as predicate name
        Class<?>[] atomAsPredicateWithDefinitionPsiPattern = new Class[]{
                PrologSentence.class,
                PrologOperation.class,
                PrologNativeBinaryOperation.class,
                PrologBasicTerm.class,
                PrologAtom.class
        };

        if (patternFitPsiElement(sentence, atomAsPredicateWithDefinitionPsiPattern)) {
            return findWithPattern(sentence, atomAsPredicateWithDefinitionPsiPattern);
        }

        // 2. test(A) :- test2. => compound used as predicate name
        Class<?>[] compoundAsPredicateWithDefinitionPsiPattern = new Class[]{
                PrologSentence.class,
                PrologOperation.class,
                PrologNativeBinaryOperation.class,
                PrologBasicTerm.class,
                PrologCompound.class
        };

        if (patternFitPsiElement(sentence, compoundAsPredicateWithDefinitionPsiPattern)) {
            return findWithPattern(sentence, compoundAsPredicateWithDefinitionPsiPattern);
        }


        // 3. test(X). => compound without definition
        Class<?>[] compoundAsPredicateWithoutDefinitionPsiPattern = new Class[]{
                PrologSentence.class,
                PrologCompound.class
        };

        if (patternFitPsiElement(sentence, compoundAsPredicateWithoutDefinitionPsiPattern)) {
            return findWithPattern(sentence, compoundAsPredicateWithoutDefinitionPsiPattern);
        }

        // 4. test. => atom without definition
        Class<?>[] atomAsPredicateWithoutDefinitionPsiPattern = new Class[]{
                PrologSentence.class,
                PrologAtom.class
        };

        if (patternFitPsiElement(sentence, atomAsPredicateWithoutDefinitionPsiPattern)) {
            return findWithPattern(sentence, atomAsPredicateWithoutDefinitionPsiPattern);
        }


        //ALL OTHER CASE ARE NOT HANDLED FOR NOW
        //TODO: Find all existing cases and handle them

        return null;
    }

    private PsiElement findWithPattern(PsiElement elt, Class[] pattern) {
        for (Class<?> aClass : pattern) {
            elt = elt.getFirstChild();
        }
        return elt instanceof PrologCompoundName ? elt.getParent() : elt; //Return the compound if the name is found
    }


    private boolean patternFitPsiElement(PsiElement elt, Class[] pattern) {
        for (Class<?> aClass : pattern) {
            if (!aClass.isInstance(elt)) {
                return false;
            }
            elt = elt.getFirstChild();
            if (elt == null) {
                return false;
            }
        }
        return true;
    }
}
