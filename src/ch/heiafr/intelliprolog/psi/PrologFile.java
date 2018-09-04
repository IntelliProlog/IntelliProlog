package ch.heiafr.intelliprolog.psi;

import ch.heiafr.intelliprolog.PrologFileType;
import ch.heiafr.intelliprolog.PrologLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class PrologFile extends PsiFileBase {
    public PrologFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, PrologLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return PrologFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Prolog File";
    }

}
