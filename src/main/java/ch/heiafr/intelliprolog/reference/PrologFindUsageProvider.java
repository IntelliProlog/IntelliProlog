package ch.heiafr.intelliprolog.reference;

import ch.heiafr.intelliprolog.PrologLexerAdapter;
import ch.heiafr.intelliprolog.psi.PrologCompoundName;
import ch.heiafr.intelliprolog.psi.PrologTokenType;
import ch.heiafr.intelliprolog.psi.PrologTypes;
import ch.heiafr.intelliprolog.psi.impl.PrologPsiUtil;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordOccurrence;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.Processor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrologFindUsageProvider implements FindUsagesProvider {
    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiNamedElement;
    }

    @Override
    public @Nullable @NonNls String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @Override
    public @Nls @NotNull String getType(@NotNull PsiElement element) {
        if(element instanceof PrologCompoundName) {
            return "Prolog predicate";
        }
        return "";
    }

    @Override
    public @Nls @NotNull String getDescriptiveName(@NotNull PsiElement element) {
        return "Descriptor";
    }

    @Override
    public @Nls @NotNull String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        System.out.println(element.getText());
        return element.getText();
    }

    @Override
    public @Nullable WordsScanner getWordsScanner() {
        return (fileText, processor) -> {
            PrologLexerAdapter lexer = new PrologLexerAdapter();
            lexer.start(fileText);
            IElementType tokenType;
            while ((tokenType = lexer.getTokenType()) != null) {
                if (tokenType == PrologTypes.UNQUOTED_COMPOUND_NAME) {
                    CharSequence text = fileText.subSequence(lexer.getTokenStart(), lexer.getTokenEnd());
                    processor.process(new WordOccurrence(text, 0, text.length(), WordOccurrence.Kind.CODE));
                    System.out.println("Found word: " + fileText.subSequence(lexer.getTokenStart(), lexer.getTokenEnd()));
                }
                lexer.advance();
            }
        };
    }

}
