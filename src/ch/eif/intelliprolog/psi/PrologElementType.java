package ch.eif.intelliprolog.psi;

import ch.eif.intelliprolog.PrologLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class PrologElementType extends IElementType {
    public PrologElementType(@NotNull String debugName) {
        super(debugName, PrologLanguage.INSTANCE);
    }

    public static boolean isParenthesis(IElementType tokenType) {
        return tokenType.equals(PrologTypes.LPAREN) || tokenType.equals(PrologTypes.RPAREN);
    }
}