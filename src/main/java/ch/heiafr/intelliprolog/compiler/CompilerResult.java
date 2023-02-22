package ch.heiafr.intelliprolog.compiler;

import com.intellij.lang.annotation.HighlightSeverity;

public record CompilerResult(int line, String message, HighlightSeverity severity) {

}
