package ch.heiafr.intelliprolog.compiler;

import com.intellij.lang.annotation.HighlightSeverity;

public class CompilerResult {
    private final int line;
    private final String message;

    private final HighlightSeverity severity;

    public CompilerResult(int line, String message, HighlightSeverity severity) {
        this.line = line;
        this.message = message;
        this.severity = severity;
    }

    public int getLine() {
        return line;
    }

    public String getMessage() {
        return message;
    }

    public HighlightSeverity getSeverity() {
        return severity;
    }

}
