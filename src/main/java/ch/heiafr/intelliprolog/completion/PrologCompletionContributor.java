package ch.heiafr.intelliprolog.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

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
