package ch.heiafr.intelliprolog.psi;

import ch.heiafr.intelliprolog.PrologFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;

public class PrologElementFactory {

    public static PrologCompoundName createCompoundName(Project project, String name) {
        PrologFile file = createFile(project, name);
        return (PrologCompoundName) file.getFirstChild();
    }

    public static PrologFile createFile(Project project, String text) {
        String name = "fake_file.pl";
        return (PrologFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, PrologFileType.INSTANCE, text);
    }

    public static PrologAtom createAtom(Project project, String name) {
        PrologFile file = createFile(project, name);
        return (PrologAtom) file.getFirstChild();
    }
}
