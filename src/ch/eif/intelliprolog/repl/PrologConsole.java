package ch.eif.intelliprolog.repl;

import ch.eif.intelliprolog.PrologFileType;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class PrologConsole extends LanguageConsoleImpl {
    public PrologConsole(@NotNull Project project, @NotNull String title) {
        super(project, title, PrologFileType.INSTANCE.getLanguage());
    }
}
