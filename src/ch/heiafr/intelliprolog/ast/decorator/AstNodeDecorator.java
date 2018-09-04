package ch.heiafr.intelliprolog.ast.decorator;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class AstNodeDecorator implements ASTNode {

    private final ASTNode node;

    AstNodeDecorator(ASTNode node) {
        this.node = node;
    }

    public ASTNode getDecoratedNode() {
        return node;
    }

    @NotNull
    @Override
    public IElementType getElementType() {
        return node.getElementType();
    }

    @NotNull
    @Override
    public String getText() {
        return node.getText();
    }

    @NotNull
    @Override
    public CharSequence getChars() {
        return node.getChars();
    }

    @Override
    public boolean textContains(final char c) {
        return node.textContains(c);
    }

    @Override
    public int getStartOffset() {
        return node.getStartOffset();
    }

    @Override
    public int getTextLength() {
        return node.getTextLength();
    }

    @Override
    public TextRange getTextRange() {
        return node.getTextRange();
    }

    @Override
    public ASTNode getTreeParent() {
        return node.getTreeParent();
    }

    @Override
    public ASTNode getFirstChildNode() {
        return node.getFirstChildNode();
    }

    @Override
    public ASTNode getLastChildNode() {
        return node.getLastChildNode();
    }

    @Override
    public ASTNode getTreeNext() {
        return node.getTreeNext();
    }

    @Override
    public ASTNode getTreePrev() {
        return node.getTreePrev();
    }

    @NotNull
    @Override
    public ASTNode[] getChildren(@Nullable TokenSet filter) {
        return node.getChildren(filter);
    }

    @Override
    public void addChild(@NotNull final ASTNode child) {
        node.addChild(child);
    }

    @Override
    public void addChild(@NotNull final ASTNode child, @Nullable ASTNode anchorBefore) {

    }

    @Override
    public void addLeaf(@NotNull final IElementType leafType, CharSequence leafText, @Nullable final ASTNode anchorBefore) {
        node.addLeaf(leafType, leafText, anchorBefore);
    }

    @Override
    public void removeChild(@NotNull final ASTNode child) {
        node.removeChild(child);
    }

    @Override
    public void removeRange(@NotNull final ASTNode firstNodeToRemove, final ASTNode firstNodeToKeep) {
        node.removeRange(firstNodeToRemove, firstNodeToKeep);
    }

    @Override
    public void replaceChild(@NotNull final ASTNode oldChild, @NotNull final ASTNode newChild) {
        node.replaceChild(oldChild, newChild);
    }

    @Override
    public void replaceAllChildrenToChildrenOf(final ASTNode anotherParent) {
        node.replaceAllChildrenToChildrenOf(anotherParent);
    }

    @Override
    public void addChildren(final ASTNode firstChild, final ASTNode firstChildToNotAdd, final ASTNode anchorBefore) {
        node.addChildren(firstChild, firstChildToNotAdd, anchorBefore);
    }

    @NotNull
    @Override
    public Object clone() {
        return node.clone();
    }

    @Override
    public ASTNode copyElement() {
        return node.copyElement();
    }

    @Nullable
    @Override
    public ASTNode findLeafElementAt(final int offset) {
        return node.findLeafElementAt(offset);
    }

    @Nullable
    @Override
    public <T> T getCopyableUserData(@NotNull final Key<T> key) {
        return node.getCopyableUserData(key);
    }

    @Override
    public <T> void putCopyableUserData(@NotNull final Key<T> key, final T value) {
        node.putCopyableUserData(key, value);
    }

    @Nullable
    @Override
    public ASTNode findChildByType(final IElementType type) {
        return node.findChildByType(type);
    }

    @Nullable
    @Override
    public ASTNode findChildByType(final IElementType type, @Nullable final ASTNode anchor) {
        return node.findChildByType(type, anchor);
    }

    @Nullable
    @Override
    public ASTNode findChildByType(@NotNull final TokenSet typesSet) {
        return node.findChildByType(typesSet);
    }

    @Nullable
    @Override
    public ASTNode findChildByType(@NotNull final TokenSet typesSet, @Nullable final ASTNode anchor) {
        return node.findChildByType(typesSet, anchor);
    }

    @Override
    public PsiElement getPsi() {
        return node.getPsi();
    }

    @Override
    public <T extends PsiElement> T getPsi(@NotNull final Class<T> clazz) {
        return node.getPsi(clazz);
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull final Key<T> key) {
        return node.getUserData(key);
    }

    @Override
    public <T> void putUserData(@NotNull final Key<T> key, @Nullable final T value) {
        node.putUserData(key, value);
    }
}
