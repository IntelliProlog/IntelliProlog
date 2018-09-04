package ch.heiafr.intelliprolog.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ColoredProcessHandler;

import java.nio.charset.Charset;

public class PrologConsoleProcessHandler extends ColoredProcessHandler {

    private final LanguageConsoleImpl console;

    public PrologConsoleProcessHandler(Process process, String commandLine, LanguageConsoleImpl console) {
        super(process, commandLine, Charset.forName("UTF-8"));
        this.console = console;
    }

    public LanguageConsoleImpl getLanguageConsole() {
        return console;
    }
}
