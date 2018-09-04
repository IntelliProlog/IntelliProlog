package ch.heiafr.intelliprolog.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static ch.heiafr.intelliprolog.ast.decorator.CommentDecorator.isComment;
import static ch.heiafr.intelliprolog.psi.impl.decorator.ListDecorator.isList;
import static ch.heiafr.intelliprolog.psi.impl.decorator.ListDecorator.listDecorator;
import static ch.heiafr.intelliprolog.psi.impl.decorator.SentenceDecorator.isSentence;

public class PrologFoldingBuilder implements FoldingBuilder {

    private static final int LIST_SIZE_FOLDING_THRESHOLD = 3;

    private static final Pattern WHITES = Pattern.compile("[\\s]+");

    private static boolean isFoldable(ASTNode node, Document document) {
        PsiElement psi = node.getPsi();
        if (isComment(node) || isSentence(psi) || isList(psi) && spanMultipleLines(node, document)) {
            return true;
        }

        return isList(psi) && listDecorator(psi).size() > LIST_SIZE_FOLDING_THRESHOLD;
    }

    private static boolean spanMultipleLines(ASTNode node, Document document) {
        final TextRange range = node.getTextRange();
        return document.getLineNumber(range.getStartOffset()) < document.getLineNumber(range.getEndOffset());
    }

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        collectDescriptorsRecursively(node, document, descriptors);
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    private void collectDescriptorsRecursively(ASTNode node, Document document, List<FoldingDescriptor> descriptors) {
        if (isFoldable(node, document)) {
            descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
        }
        for (ASTNode child : node.getChildren(null)) {
            collectDescriptorsRecursively(child, document, descriptors);
        }
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        PsiElement psi = node.getPsi();
        if (isComment(node)) {
            return "/*...*/";
        } else if (isList(psi)) {
            return "[...]";
        }

        return collapseWhiteSpace(psi.getText()).substring(0, 10) + "...";
    }

    private String collapseWhiteSpace(String text) {
        return WHITES.matcher(text).replaceAll(" ");
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
