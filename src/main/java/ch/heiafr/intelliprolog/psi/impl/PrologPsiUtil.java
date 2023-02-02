package ch.heiafr.intelliprolog.psi.impl;

import ch.heiafr.intelliprolog.PrologFileType;
import ch.heiafr.intelliprolog.psi.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.heiafr.intelliprolog.psi.PrologTypes.KNOWN_BINARY_OPERATOR;
import static ch.heiafr.intelliprolog.psi.PrologTypes.KNOWN_LEFT_OPERATOR;

public class PrologPsiUtil {

    public static IElementType getElementType(PsiElement element) {
        return element.getNode().getElementType();
    }

    public static boolean isOperator(PsiElement element) {
        return isKnownBinaryOperator(element) || isKnownLeftOperator(element);
    }

    public static boolean isKnownBinaryOperator(PsiElement element) {
        return getElementType(element).equals(KNOWN_BINARY_OPERATOR);
    }

    public static boolean isKnownLeftOperator(PsiElement element) {
        return getElementType(element).equals(KNOWN_LEFT_OPERATOR);
    }

    public static boolean isAtomKeyword(PsiElement element) {
        return getElementType(element).equals(PrologTypes.ATOM) && Constants.KEYWORDS.contains(element.getText());
    }

    public static boolean isCompoundNameKeyword(PsiElement element) {
        return getElementType(element).equals(PrologTypes.COMPOUND_NAME) && Constants.KEYWORDS.contains(element.getText());
    }

    public static boolean isCompoundName(PsiElement element) {
        return getElementType(element).equals(PrologTypes.COMPOUND_NAME);
    }

    public static boolean isUserCompoundName(PsiElement element) {
        return (isCompoundName(element) && !isCompoundNameKeyword(element));
    }

    /*
        NAMED ELEMENT UTILITIES
     */

    public static String getName(PrologCompoundName element) {
        return element.getText();
    }

    public static PsiElement setName(PrologCompoundName element, String newName) {
        ASTNode keyNode = element.getNode().findChildByType(PrologTypes.UNQUOTED_COMPOUND_NAME);
        if (keyNode != null) {
            PrologCompoundName property =
                    PrologElementFactory.createCompoundName(element.getProject(), newName);
            ASTNode newKeyNode = property.getFirstChild().getNode();
            element.getNode().replaceChild(keyNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(PrologCompoundName element) {
        ASTNode keyNode = element.getNode().findChildByType(PrologTypes.UNQUOTED_COMPOUND_NAME);
        return keyNode != null ? keyNode.getPsi() : null;
        //return element;
    }


    public static String getName(PrologAtom element) {
        return element.getText();
    }

    public static PsiElement setName(PrologAtom element, String newName) {
        ASTNode keyNode = element.getNode().findChildByType(PrologTypes.UNQUOTED_ATOM);
        if (keyNode != null) {
            PrologAtom property =
                    PrologElementFactory.createAtom(element.getProject(), newName);
            ASTNode newKeyNode = property.getFirstChild().getNode();
            element.getNode().replaceChild(keyNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(PrologAtom element) {
        ASTNode keyNode = element.getNode().findChildByType(PrologTypes.UNQUOTED_ATOM);
        return keyNode != null ? keyNode.getPsi() : null;
        //return element;
    }


    public static String getName(PrologVariable element) {
        return element.getText();
    }

    public static PsiElement setName(PrologVariable element, String newName) {

        //Check if the first letter must be uppercase => we don't change the type but only the name
        if(!Character.isUpperCase(newName.charAt(0))){
            return element; //No change
        }
        if(newName.charAt(0) == '_'){ //We don't change the type
            return element; //No change
        }

        ASTNode keyNode = element.getNode().findChildByType(PrologTypes.NAMED_VARIABLE);
        if (keyNode != null) {
            PrologVariable property =
                    PrologElementFactory.createVariable(element.getProject(), newName);
            ASTNode newKeyNode = property.getFirstChild().getNode();
            element.getNode().replaceChild(keyNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(PrologVariable element) {
        ASTNode keyNode = element.getNode().findChildByType(PrologTypes.NAMED_VARIABLE);
        return keyNode != null ? keyNode.getPsi() : null;
        //return element;
    }

    /*
        REFERENCE UTILITIES
     */
    public static List<PrologCompoundName> findCompoundNames(Project project, String searchName) {
        return findCompoundNames(project, GlobalSearchScope.allScope(project), searchName);
    }

    public static List<PrologCompoundName> findCompoundNames(Project project, GlobalSearchScope scope, String searchName) {
        List<PrologCompoundName> result = new ArrayList<>();

        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(PrologFileType.INSTANCE, scope);

        for (VirtualFile virtualFile : virtualFiles) {
            PrologFile prologFile = (PrologFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (prologFile != null) {
                PrologCompoundName[] names = PsiTreeUtil.getChildrenOfType(prologFile, PrologCompoundName.class);
                if (names != null) {
                    if(searchName == null) {
                        result.addAll(List.of(names));
                    } else {
                        for (PrologCompoundName name : names) {
                            if (name.getText().equals(searchName)) {
                                result.add(name);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}
