package ch.heiafr.intelliprolog.psi;

import ch.heiafr.intelliprolog.PrologFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;

public class PrologElementFactory {

    public static PrologCompoundName createCompoundName(Project project, String name) {
        // Add (a,b). to the end of the file to make sure the parser doesn't fail
        PrologFile file = createFile(project, name+"(a,b).");
        // Find the sentence...
        PrologSentence sentenceRoot = (PrologSentence) file.getFirstChild();
        // ...and find/return the compound name (if any)
        return PsiTreeUtil.findChildOfType(sentenceRoot, PrologCompoundName.class);
    }

    public static PrologCompound createCompound(Project project, String name) {
        // Add (a,b). to the end of the file to make sure the parser doesn't fail
        PrologFile file = createFile(project, name+".");
        // Find the sentence...
        PrologSentence sentenceRoot = (PrologSentence) file.getFirstChild();
        // ...and find/return the compound name (if any)
        return PsiTreeUtil.findChildOfType(sentenceRoot, PrologCompound.class);
    }

    public static PrologFile createFile(Project project, String text) {
        String name = "fake_file.pl";
        return (PrologFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, PrologFileType.INSTANCE, text);
    }

    public static PrologAtom createAtom(Project project, String newName) {
        // Add (a,b). to the end of the file to make sure the parser doesn't fail
        PrologFile file = createFile(project, newName+".");
        // Find the sentence...
        PrologSentence sentenceRoot = (PrologSentence) file.getFirstChild();
        // ...and find/return the atom (if any)
        return PsiTreeUtil.findChildOfType(sentenceRoot, PrologAtom.class);
    }

    public static PrologVariable createVariable(Project project, String newName) {
        // Add (a,b). to the end of the file to make sure the parser doesn't fail
        PrologFile file = createFile(project, "variable("+newName+").");
        // Find the sentence...
        PrologSentence sentenceRoot = (PrologSentence) file.getFirstChild();
        // ...and find/return the variable (if any)
        return PsiTreeUtil.findChildOfType(sentenceRoot, PrologVariable.class);
    }

    public static PsiElement rebuildTree(PsiElement copy) {
        copy = copy.getContainingFile(); //Get root if not already
        return createFile(copy.getProject(), copy.getText()); //Rebuild tree
    }
}
