package ch.heiafr.intelliprolog.reference;

import ch.heiafr.intelliprolog.PrologFileType;
import ch.heiafr.intelliprolog.psi.*;
import ch.heiafr.intelliprolog.psi.impl.PrologPsiUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ReferenceHelper {

    /**
     * Find the definition of a sentence if it exists
     *
     * @param sentence The sentence to search in
     * @return The definition of the sentence if it exists, null otherwise
     */
    public static PsiElement findDefinition(PrologSentence sentence) {

        if (sentence == null) {
            return null;
        }

        //Multiple cases
        Class<?>[][] searchPatterns = new Class[][]{{
                // 1. test :- test2. => atom used as predicate name
                PrologSentence.class, PrologOperation.class, PrologNativeBinaryOperation.class, PrologBasicTerm.class, PrologAtom.class}, {
                // 2. test(A) :- test2. => compound used as predicate name
                PrologSentence.class, PrologOperation.class, PrologNativeBinaryOperation.class, PrologBasicTerm.class, PrologCompound.class}, {
                // 3. test(X). => compound without definition
                PrologSentence.class, PrologCompound.class}, {
                // 4. test. => atom without definition
                PrologSentence.class, PrologAtom.class}};


        for (Class<?>[] searchPattern : searchPatterns) {
            var definition = patternFitPsiElement(sentence, searchPattern);

            if (definition != null) {
                return definition; //Found a definition
            }
        }

        //If not found, return null
        return null;
    }

    /**
     * Check if a pattern fit a PsiElement
     * @param elt The element to check
     * @param pattern The pattern to check
     * @return The element if the pattern fit, null otherwise
     */
    private static PsiElement patternFitPsiElement(PsiElement elt, Class[] pattern) {
        for (Class<?> aClass : pattern) {
            if (!aClass.isInstance(elt)) {
                return null;
            }
            elt = elt.getFirstChild();
            if (elt == null) {
                return null;
            }
        }

        return elt instanceof PrologCompoundName ? elt.getParent() : elt; //Return the compound if the name is found
    }

    /**
     * Extract the quoted string from a compound
     *
     * @param compound The compound
     * @return The String without the quotes or null if no string was found
     */
    public static String extractQuotedString(PsiElement compound) {
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
     * Calculate the arity of a compound
     * @param compound The compound
     * @return The arity of the compound, 0 if this is not a compound
     */
    public static int getArity(PsiElement compound) {

        if (!(compound instanceof PrologCompound)) {
            return 0;
        }

        PsiElement term = PsiTreeUtil.findChildOfType(compound, PrologTerm.class);

        Class<?>[][] singleParameterPattern = new Class<?>[][]{{PrologTerm.class}, {PrologOperation.class}, {PrologNativeBinaryOperation.class}, {PrologBasicTerm.class, PrologKnownBinaryOperator.class, PrologTerm.class}};

        int count = 0;

        while (term != null) {
            count++;
            term = skipComposed(term);
            term = applyIfPossible(term, singleParameterPattern);
        }

        return count;
    }

    /**
     * Find last element of a composed term.
     * Example: A-B-C-D, E => D, E
     * @param term The term to search in
     * @return The last element of the composed term
     */
    private static PsiElement skipComposed(PsiElement term) {
        Class<?>[][] findOperator = new Class<?>[][]{{PrologTerm.class}, {PrologOperation.class}, {PrologNativeBinaryOperation.class},{PrologBasicTerm.class, PrologKnownBinaryOperator.class}};
        PsiElement initial = term;


        var op = applyIfPossible(term, findOperator);
        if(op == null){
            return initial;
        }

        if (op.getText().equals(",")) {
            return initial;
        } else {
            //Find next term and apply
            term = op.getNextSibling();
            while (term instanceof PsiWhiteSpace) {
                //Skip whitespaces
                term = term.getNextSibling();
            }

            return skipComposed(term);
        }
    }

    /**
     * Apply a pattern to a PsiElement to find the next element
     * @param term The element to search in
     * @param pattern The pattern to apply
     * @return The next element if the pattern was found, null otherwise
     */
    private static PsiElement applyIfPossible(PsiElement term, Class<?>[][] pattern) {
        if (term == null) {
            return null;
        }
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern[i].length; j++) {
                if (!pattern[i][j].isInstance(term)) {
                    return null;
                }
                if (j == pattern[i].length - 1) {
                    continue;
                }
                term = term.getNextSibling();
                while(term instanceof PsiWhiteSpace){
                    term = term.getNextSibling(); //Skip whitespaces
                }
                if (term == null) {
                    return null;
                }
            }
            if (i == pattern.length - 1) {
                return term;
            }
            term = term.getFirstChild();
        }
        return term;
    }

    /**
     * Find a compound from a PSI element
     * @param clickedElement The element to search in
     * @return The compound if found, null otherwise
     */
    public static PrologCompound compoundFromClickedElement(PsiElement clickedElement) {
        if (clickedElement.getNode().getElementType() == PrologTypes.UNQUOTED_COMPOUND_NAME
                || clickedElement instanceof PrologCompoundName) {
            return PsiTreeUtil.getParentOfType(clickedElement, PrologCompound.class);
        }
        return null;
    }

    /**
     * Find the Compound name from a Compound
     * @param compound The compound to search in
     * @return The compound name if found, null otherwise
     */
    public static String compoundNameFromCompound(PrologCompound compound) {
        PrologCompoundName compoundName = PsiTreeUtil.findChildOfType(compound, PrologCompoundName.class);
        if (compoundName == null) {
            return null;
        }
        return compoundName.getText();
    }

    /**
     * Find the arity of a PSI element
     * @param elt The element to search in
     * @return The arity if found, 0 otherwise
     */
    public static int getArityFromClicked(PsiElement elt) {
        return getArity(compoundFromClickedElement(elt));
    }

    /**
     * Test if two PSIElemnts are the same PrologSentence
     * @param e1 The first element
     * @param e2 The second element
     * @return True if they are the same, false otherwise
     */
    public static boolean areInSameSentence(PsiElement e1, PsiElement e2) {
        PrologSentence s1  = PsiTreeUtil.getParentOfType(e1, PrologSentence.class);
        PrologSentence s2  = PsiTreeUtil.getParentOfType(e2, PrologSentence.class);

        return Objects.equals(s1, s2);
    }

    /**
     * Find all the files that are imported in the current file and all the files that are imported in those files recursively
     *
     * @param elt   The current element
     * @param files The list of files that are imported
     * @return The list of PsiElements that are used in the current file or in the files that are imported in the current file
     */
    public static Collection<PsiElement> findEveryImportedFile(PsiElement elt, Collection<String> files) {
        if (elt == null) {
            return new ArrayList<>();
        }

        Collection<String> paths = PsiTreeUtil.collectElementsOfType(elt.getContainingFile(), PrologSentence.class).stream()
                .map(ReferenceHelper::findIncludeStatement)// Find the first compound name which is the predicate name
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

    public static PsiElement findIncludeStatement(PrologSentence sentence) {
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
    public static PsiElement pathToPsi(PsiElement elt, String path) {

        Path basePath = Paths.get(elt.getContainingFile().getVirtualFile().getPath()).getParent(); //Get the base path
        Path filePath = basePath.resolve(path); //Resolve the path
        VirtualFile file = VirtualFileManager.getInstance().findFileByNioPath(filePath); //Get the virtual file
        if (file == null) {
            return null;
        }
        return PsiManager.getInstance(elt.getProject()).findFile(file);
    }

}
