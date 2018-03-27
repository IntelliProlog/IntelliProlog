package ch.eif.intelliprolog.psi;

import ch.eif.intelliprolog.PrologLanguage;
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
