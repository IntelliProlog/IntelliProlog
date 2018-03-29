package ch.eif.intelliprolog;

import com.intellij.lexer.FlexAdapter;

public class PrologLexerAdapter extends FlexAdapter {
    public PrologLexerAdapter() {
        super(new ch.eif.intelliprolog.PrologLexer(null));
    }
}
