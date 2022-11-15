package ch.heiafr.intelliprolog.psi.impl;

import ch.heiafr.intelliprolog.psi.PrologNamedElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public abstract class PrologNamedElementImpl extends ASTWrapperPsiElement implements PrologNamedElement {

  public PrologNamedElementImpl(@NotNull ASTNode node) {
    super(node);
  }

}
