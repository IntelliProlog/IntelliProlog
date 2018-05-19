package ch.eif.intelliprolog.ast.decorator;

import ch.eif.intelliprolog.psi.PrologTypes;
import com.intellij.lang.ASTNode;

public class CommentDecorator extends AstNodeDecorator {
    private CommentDecorator(ASTNode node) {
        super(node);
    }

    public static boolean isComment(ASTNode node) {
        return node.getElementType() == PrologTypes.COMMENT;
    }

    public static CommentDecorator commentDecorator(ASTNode node) {
        if (!isComment(node)) {
            throw new WrongAstNodeException(node.getElementType(), PrologTypes.COMMENT);
        }
        return node instanceof CommentDecorator ? (CommentDecorator) node : new CommentDecorator(node);
    }
}
