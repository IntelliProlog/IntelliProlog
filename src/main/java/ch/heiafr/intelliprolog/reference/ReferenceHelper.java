package ch.heiafr.intelliprolog.reference;

import ch.heiafr.intelliprolog.psi.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.Objects;

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

        System.out.println("------------------------------------------------------------");
        System.out.println("Arity of " + compound.getText());

        while (term != null) {
            count++;
            term = skipComposed(term);
            System.out.println("End of skipComposed: " + term.getText());
            term = applyIfPossible(term, singleParameterPattern);
        }

        System.out.printf("Arity of %s is %d%n", compound.getText(), count);
        System.out.println("------------------------------------------------------------");
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

        System.out.println("Skip composed: " + term.getText());

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
}
