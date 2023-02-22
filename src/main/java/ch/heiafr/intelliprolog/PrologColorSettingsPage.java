package ch.heiafr.intelliprolog;

import ch.heiafr.intelliprolog.editor.PrologSyntaxHighlighter;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class PrologColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor @NotNull [] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Operator", PrologSyntaxHighlighter.OPERATOR),
            new AttributesDescriptor("Cut", PrologSyntaxHighlighter.CUT),
            new AttributesDescriptor("Quoted Atom", PrologSyntaxHighlighter.QUOTED_ATOM),
            new AttributesDescriptor("Unquoted Atom", PrologSyntaxHighlighter.UNQUOTED_ATOM),
            new AttributesDescriptor("Keyword Atom", PrologSyntaxHighlighter.KEYWORD_ATOM),
            new AttributesDescriptor("Anonymous Variable", PrologSyntaxHighlighter.ANONYMOUS_VARIABLE),
            new AttributesDescriptor("Named Variable", PrologSyntaxHighlighter.NAMED_VARIABLE),
            new AttributesDescriptor("Parenthesis", PrologSyntaxHighlighter.PARENTHESIS),
            new AttributesDescriptor("Brackets", PrologSyntaxHighlighter.BRACKETS),
            new AttributesDescriptor("Bad Character", PrologSyntaxHighlighter.BAD_CHARACTER),
            new AttributesDescriptor("Quoted Compound Name", PrologSyntaxHighlighter.QUOTED_COMPOUND_NAME),
            new AttributesDescriptor("Unquoted Compound Name", PrologSyntaxHighlighter.UNQUOTED_COMPOUND_NAME),
            new AttributesDescriptor("Keyword Compound Name", PrologSyntaxHighlighter.KEYWORD_COMPOUND_NAME)
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return PrologIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new PrologSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return """
                factorial(0, 1).
                factorial(N, F) :-
                        N>0,
                        N1 is N-1,
                        factorial(N1, F1),
                        F is F1 * N.
                not(P) :- P, !, fail.
                not(_).
                """;
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Prolog";
    }
}
