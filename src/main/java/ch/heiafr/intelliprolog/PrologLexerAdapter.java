package ch.heiafr.intelliprolog;

import com.intellij.lexer.FlexAdapter;

public class PrologLexerAdapter extends FlexAdapter {
    public PrologLexerAdapter() {
        super(new PrologLexer(null));
    }
}
