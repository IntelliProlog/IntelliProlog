package ch.heiafr.intelliprolog.editor;


import ch.heiafr.intelliprolog.PrologLexerAdapter;
import ch.heiafr.intelliprolog.psi.PrologTypes;
import ch.heiafr.intelliprolog.psi.impl.PrologPsiUtil;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static ch.heiafr.intelliprolog.psi.PrologElementType.isBrackets;
import static ch.heiafr.intelliprolog.psi.PrologElementType.isParenthesis;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class PrologSyntaxHighlighter extends SyntaxHighlighterBase {

    // BAPST: maybe consider getOrCreate (?)
    public static final TextAttributesKey OPERATOR =
            createTextAttributesKey("OPERATOR", DefaultLanguageHighlighterColors.CONSTANT);

    public static final TextAttributesKey CUT =
            createTextAttributesKey("CUT", DefaultLanguageHighlighterColors.CONSTANT);

    private static final TextAttributesKey STRING =
            createTextAttributesKey("STRING_TERM", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE);

    private static final TextAttributesKey INTEGER =
            createTextAttributesKey("INTEGER_TERM", DefaultLanguageHighlighterColors.NUMBER);

    private static final TextAttributesKey FLOAT =
            createTextAttributesKey("FLOAT_TERM", DefaultLanguageHighlighterColors.NUMBER);

    private static final TextAttributesKey BIN_NUMBER =
            createTextAttributesKey("BIN_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

    private static final TextAttributesKey OCT_NUMBER =
            createTextAttributesKey("OCT_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

    private static final TextAttributesKey HEX_NUMBER =
            createTextAttributesKey("HEX_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

    private static final TextAttributesKey CHAR_CODE =
            createTextAttributesKey("CHAR_CODE", DefaultLanguageHighlighterColors.NUMBER);

    public static final TextAttributesKey QUOTED_ATOM =
            createTextAttributesKey("QUOTED_ATOM", DefaultLanguageHighlighterColors.STRING);

    public static final TextAttributesKey UNQUOTED_ATOM =
            createTextAttributesKey("UNQUOTED_ATOM", DefaultLanguageHighlighterColors.IDENTIFIER);

    public static final TextAttributesKey KEYWORD_ATOM =
            createTextAttributesKey("KEYWORD_ATOM", DefaultLanguageHighlighterColors.STATIC_FIELD);

    public static final TextAttributesKey QUOTED_COMPOUND_NAME =
            createTextAttributesKey("QUOTED_COMPOUND_NAME", DefaultLanguageHighlighterColors.STRING);

    public static final TextAttributesKey UNQUOTED_COMPOUND_NAME =
            createTextAttributesKey("UNQUOTED_COMPOUND_NAME", DefaultLanguageHighlighterColors.IDENTIFIER);

    public static final TextAttributesKey KEYWORD_COMPOUND_NAME =
            createTextAttributesKey("KEYWORD_COMPOUND_NAME", DefaultLanguageHighlighterColors.STATIC_FIELD);

    public static final TextAttributesKey ANONYMOUS_VARIABLE =
            createTextAttributesKey("ANONYMOUS_VARIABLE", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);

    public static final TextAttributesKey NAMED_VARIABLE =
            createTextAttributesKey("NAMED_VARIABLE", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);

    public static final TextAttributesKey PARENTHESIS =
            createTextAttributesKey("PARENTHESIS", DefaultLanguageHighlighterColors.PARENTHESES);

    public static final TextAttributesKey BRACKETS =
            createTextAttributesKey("BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);

    private static final TextAttributesKey COMMENT =
            createTextAttributesKey("PROLOG_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("PROLOG_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] OPERATOR_KEYS = new TextAttributesKey[]{OPERATOR};
    private static final TextAttributesKey[] CUT_KEYS = new TextAttributesKey[]{CUT};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] INTEGER_KEYS = new TextAttributesKey[]{INTEGER};
    private static final TextAttributesKey[] FLOAT_KEYS = new TextAttributesKey[]{FLOAT};
    private static final TextAttributesKey[] BIN_NUMBER_KEYS = new TextAttributesKey[]{BIN_NUMBER};
    private static final TextAttributesKey[] OCT_NUMBER_KEYS = new TextAttributesKey[]{OCT_NUMBER};
    private static final TextAttributesKey[] HEX_NUMBER_KEYS = new TextAttributesKey[]{HEX_NUMBER};
    private static final TextAttributesKey[] CHAR_CODE_KEYS = new TextAttributesKey[]{CHAR_CODE};
    private static final TextAttributesKey[] QUOTED_ATOM_KEYS = new TextAttributesKey[]{QUOTED_ATOM};
    private static final TextAttributesKey[] UNQUOTED_ATOM_KEYS = new TextAttributesKey[]{UNQUOTED_ATOM};
    private static final TextAttributesKey[] KEYWORD_ATOM_KEYS = new TextAttributesKey[]{KEYWORD_ATOM};
    private static final TextAttributesKey[] QUOTED_COMPOUND_NAME_KEYS = new TextAttributesKey[]{QUOTED_COMPOUND_NAME};
    private static final TextAttributesKey[] UNQUOTED_COMPOUND_NAME_KEYS = new TextAttributesKey[]{UNQUOTED_COMPOUND_NAME};
    private static final TextAttributesKey[] KEYWORD_COMPOUND_NAME_KEYS = new TextAttributesKey[]{KEYWORD_COMPOUND_NAME};
    private static final TextAttributesKey[] ANONYMOUS_VARIABLE_KEYS = new TextAttributesKey[]{ANONYMOUS_VARIABLE};
    private static final TextAttributesKey[] NAMED_VARIABLE_KEYS = new TextAttributesKey[]{NAMED_VARIABLE};
    private static final TextAttributesKey[] PARENTHESIS_KEYS = new TextAttributesKey[]{PARENTHESIS};
    private static final TextAttributesKey[] BRACKETS_KEYS = new TextAttributesKey[]{BRACKETS};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_ATTRIBUTES = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new PrologLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(PrologTypes.STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(PrologTypes.INTEGER)) {
            return INTEGER_KEYS;
        } else if (tokenType.equals(PrologTypes.FLOAT)) {
            return FLOAT_KEYS;
        } else if (tokenType.equals(PrologTypes.BIN_NUMBER)) {
            return BIN_NUMBER_KEYS;
        } else if (tokenType.equals(PrologTypes.OCT_NUMBER)) {
            return OCT_NUMBER_KEYS;
        } else if (tokenType.equals(PrologTypes.HEX_NUMBER)) {
            return HEX_NUMBER_KEYS;
        } else if (tokenType.equals(PrologTypes.CHAR_CODE)) {
            return CHAR_CODE_KEYS;
        } else if (tokenType.equals(PrologTypes.QUOTED_ATOM)) {
            return QUOTED_ATOM_KEYS;
        } else if (tokenType.equals(PrologTypes.UNQUOTED_ATOM)) {
            return UNQUOTED_ATOM_KEYS;
        } else if (tokenType.equals(PrologTypes.QUOTED_COMPOUND_NAME)) {
            return QUOTED_COMPOUND_NAME_KEYS;
        } else if (tokenType.equals(PrologTypes.UNQUOTED_COMPOUND_NAME)) {
            return UNQUOTED_COMPOUND_NAME_KEYS;
        } else if (tokenType.equals(PrologTypes.ANONYMOUS_VARIABLE)) {
            return ANONYMOUS_VARIABLE_KEYS;
        } else if (tokenType.equals(PrologTypes.NAMED_VARIABLE)) {
            return NAMED_VARIABLE_KEYS;
        } else if (isParenthesis(tokenType)) {
            return PARENTHESIS_KEYS;
        } else if (isBrackets(tokenType)) {
            return BRACKETS_KEYS;
        } else if (tokenType.equals(PrologTypes.COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_ATTRIBUTES;
        }
    }

    public TextAttributesKey[] getTokenHighlights(PsiElement element) {
        if (PrologPsiUtil.isAtomKeyword(element)) {
            return KEYWORD_ATOM_KEYS;
        } else if (PrologPsiUtil.isCompoundNameKeyword(element)) {
            return KEYWORD_COMPOUND_NAME_KEYS;
        } else if (PrologPsiUtil.isOperator(element)) {
            return OPERATOR_KEYS;
        } else {
            return getTokenHighlights(PrologPsiUtil.getElementType(element));
        }

    }
}