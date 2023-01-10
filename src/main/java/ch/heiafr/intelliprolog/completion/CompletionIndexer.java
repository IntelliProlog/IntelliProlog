package ch.heiafr.intelliprolog.completion;

import ch.heiafr.intelliprolog.PrologFileType;
import ch.heiafr.intelliprolog.PrologLexerAdapter;
import ch.heiafr.intelliprolog.PrologParser;
import ch.heiafr.intelliprolog.psi.*;
import ch.heiafr.intelliprolog.reference.ReferenceHelper;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.impl.PsiBuilderAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiFileImplUtil;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceUtil;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CompletionIndexer {

    private static Set<String> atomIndex = new HashSet<>();
    private static Set<String> compoundIndex = new HashSet<>();
    private static Set<String> variableIndex = new HashSet<>();

    public static void matchForCompletion(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        //Build index from caret position => find unique atoms, compounds and variables present in the context

        makeIndex(parameters.getOriginalFile(), parameters.getOriginalPosition());


        atomIndex.forEach(atom -> {
            result.addElement(LookupElementBuilder.create(atom).withTypeText("Atom"));
        });

        compoundIndex.forEach(compound -> {
            result.addElement(LookupElementBuilder.create(compound).withTypeText("Compound").withInsertHandler(CompletionIndexer::handleInsert));
        });

        variableIndex.forEach(variable -> {
            result.addElement(LookupElementBuilder.create(variable).withTypeText("Variable"));
        });

    }

    private static void handleInsert(@NotNull InsertionContext ctx, @NotNull LookupElement elt) {
        if (elt.getLookupString().contains("/")) {
            //Compound

            String name = elt.getLookupString().split("/")[0];
            int arity = Integer.parseInt(elt.getLookupString().split("/")[1]);


            ctx.getDocument().deleteString(ctx.getStartOffset() + name.length(), ctx.getTailOffset());
            ctx.getDocument().insertString(ctx.getStartOffset() + name.length(), "(");
            for (int i = 0; i < arity; i++) {
                ctx.getDocument().insertString(ctx.getTailOffset(), "_");
                if (i < arity - 1) {
                    ctx.getDocument().insertString(ctx.getTailOffset(), ", ");
                }
            }
            ctx.getDocument().insertString(ctx.getTailOffset(), ")");


            //More confortable to have the cursor at the end of the compound
            ctx.getEditor().getCaretModel().moveToOffset(ctx.getTailOffset());
        }
    }


    private static void makeIndex( PsiElement rootFile, PsiElement caret) {

        var copyFile = removeErrorForIndexing(rootFile);

        if (copyFile == null) {
            return; //Impossible to index => use previous index if any...
        }

        atomIndex = new HashSet<>();
        compoundIndex = new HashSet<>();
        variableIndex = new HashSet<>();

        var files = ReferenceHelper.findEveryImportedFile(rootFile);
        files.add(copyFile); //Add root file to the list of files to parse

        for (var psiFile : files) {

            //Search all atoms or compounds in the file and imported files
            PsiTreeUtil.findChildrenOfType(psiFile, PrologAtom.class).stream()
                    .filter(atom -> atom.getLastChild().getNode().getElementType().equals(PrologTypes.UNQUOTED_ATOM))
                    .map(PsiNamedElement::getName)
                    .forEach(atomIndex::add);

            PsiTreeUtil.findChildrenOfType(psiFile, PrologCompound.class).stream()
                    .map(CompletionIndexer::compoundToString)
                    .forEach(compoundIndex::add);
        }


        //Search all variables in the current sentence
        PrologSentence sentence = PsiTreeUtil.getParentOfType(caret, PrologSentence.class);
        if (sentence != null) {
            PsiTreeUtil.findChildrenOfType(sentence, PrologVariable.class).stream()
                    //Don't care about anonymous variables
                    .filter(atom -> atom.getLastChild().getNode().getElementType().equals(PrologTypes.NAMED_VARIABLE))
                    .map(PsiNamedElement::getName)
                    .forEach(variableIndex::add);
        }
    }

    private static PsiElement removeErrorForIndexing(PsiElement rootFile) {
        PsiElement copy = rootFile.copy();
        var error = PsiTreeUtil.findChildOfType(copy, PsiErrorElement.class);

        if (error == null) {
            return copy;
        }


        var parent = error.getParent();

        boolean deleteParent = false;
        //Find all children of the parent of the error
        PsiElement lastUsable = null;
        for (var child : parent.getChildren()) {
            //If the child is an error, delete it
            if (child instanceof PsiWhiteSpace || child instanceof PsiComment) {
                continue;
            }
            if (child instanceof PsiErrorElement) {
                if (lastUsable != null) {
                    lastUsable.delete();
                } else {
                    deleteParent = true;
                }
                break;
            }
            lastUsable = child;
        }
        if (deleteParent) {
            if (Objects.equals(parent, copy)) {
                return null; //Impossible to delete the root file
            }
            parent.delete();
        }

        try {
            copy = PrologElementFactory.rebuildTree(copy);
            return copy;
        } catch (Exception e) {
            //More than one error in the file => impossible to rebuild the tree
            return null;
        }
    }

    private static String compoundToString(PrologCompound prologCompound) {
        String name = prologCompound.getCompoundName().getText();
        int arity = ReferenceHelper.getArity(prologCompound);
        return name + "/" + arity;
    }


}
