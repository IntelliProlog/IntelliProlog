package ch.heiafr.intelliprolog.psi;

import ch.heiafr.intelliprolog.PrologLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class PrologTokenType extends IElementType {
    public PrologTokenType(@NotNull String debugName) {
        super(debugName, PrologLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "PrologTokenType." + super.toString();
    }
}
