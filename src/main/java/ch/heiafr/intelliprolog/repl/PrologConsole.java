package ch.heiafr.intelliprolog.repl;

import ch.heiafr.intelliprolog.PrologFileType;
import com.intellij.execution.console.ConsoleHistoryController;
import com.intellij.execution.console.ConsoleRootType;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStreamWriter;

public class PrologConsole extends LanguageConsoleImpl {

    private final ConsoleRootType consoleRootType = new ConsoleRootType("prolog", "Prolog") {
    };
    private ConsoleHistoryController historyController;
    private OutputStreamWriter outputStreamWriter;
    private final String lastCommmand = null;

    public PrologConsole(@NotNull Project project, @NotNull String title) {
        super(project, title, PrologFileType.INSTANCE.getLanguage());
    }
}
