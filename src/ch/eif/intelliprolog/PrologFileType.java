package ch.eif.intelliprolog;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PrologFileType extends LanguageFileType {
    public static final PrologFileType INSTANCE = new PrologFileType();

    private PrologFileType() {
        super(PrologLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Prolog file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Prolog language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "pl";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return PrologIcons.FILE;
    }
}
