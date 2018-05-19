package ch.eif.intelliprolog.ast.decorator;

import com.intellij.psi.tree.IElementType;

class WrongAstNodeException extends RuntimeException {
    private final IElementType actual;
    private final String expected;

    public WrongAstNodeException(IElementType actual, IElementType expected) {
        this(actual, expected.toString());
    }

    private WrongAstNodeException(IElementType actual, String expected) {
        this.actual = actual;
        this.expected = expected;
    }

    public IElementType getActual() {
        return actual;
    }

    public String getExpected() {
        return expected;
    }

    @Override
    public String getMessage() {
        return expected + " expected. Actual: " + actual;
    }
}
