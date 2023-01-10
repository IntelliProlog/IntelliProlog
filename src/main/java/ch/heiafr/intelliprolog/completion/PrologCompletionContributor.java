package ch.heiafr.intelliprolog.completion;

import ch.heiafr.intelliprolog.psi.PrologTypes;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.intellij.patterns.StandardPatterns.or;

public class PrologCompletionContributor extends CompletionContributor {

    public PrologCompletionContributor() {

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {


                if(parameters.getOriginalPosition() == null){
                    return;
                }

                //First letter needs to be typed...
                if (parameters.getOriginalPosition().getText().length() < 1) {
                    return;
                }

                //...and it needs to be a letter (check only the last character)
                if(!Character.isLetter(parameters.getOriginalPosition().getText().charAt(parameters.getOriginalPosition().getText().length() - 1))) {
                    return;
                }

                CompletionIndexer.matchForCompletion(parameters, context, result);
            }
        });
    }
}
