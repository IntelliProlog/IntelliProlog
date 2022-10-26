package ch.heiafr.intelliprolog.editor;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ch.heiafr.intelliprolog.psi.PrologTypes.*;

public class PrologPairedBraceMatcher implements PairedBraceMatcher {
    @NotNull
    @Override
    public BracePair[] getPairs() {
        return new BracePair[]{
                new BracePair(LPAREN, RPAREN, true),
                new BracePair(LBRACKET, RBRACKET, false),
                new BracePair(LBRACE, RBRACE, false)
        };
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
