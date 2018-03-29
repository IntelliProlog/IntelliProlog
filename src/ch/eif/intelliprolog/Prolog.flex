package ch.eif.intelliprolog;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import ch.eif.intelliprolog.psi.PrologTypes;
import com.intellij.psi.TokenType;

%%
%class PrologLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{   return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \n\t\f]

LPAREN = "("
RPAREN = ")"
LBRACKET = "["
RBRACKET = "]"
LBRACE = "{"
RBRACE = "}"

CUT = "!"
DOT =  "."
COMMA = ","

SIMPLE_INTEGER = [:digit:]+
INTEGER = {SIGN}? {SIMPLE_INTEGER}

SINGLE_QUOTE = "'"
DOUBLE_QUOTE = \"

ATOM_CHAR = [:jletterdigit:]
UNQUOTED_ATOM = [:lowercase:] {ATOM_CHAR}*

NAMED_VARIABLE = [:uppercase:] {ATOM_CHAR}*
ANONYMOUS_VARIABLE = "_" ([:uppercase:]) {ATOM_CHAR}*

COMMENT = ("%")[^\r\n]*
NON_PRINTABLE = ({CRLF}|{WHITE_SPACE})+

NON_COMBINABLE_OPERATOR_SYMBOLS = {COMMA} | {CUT}

%state SENTENCE, PARENTHESIZED_SYMBOLS, SINGLE_QUOTE_STRING, DOUBLE_QUOTE_STRING, CHAR_CODE

%%

<YYINITIAL, SENTENCE, PARENTHESIZED_SYMBOLS> {

   {COMMENT}                                       { return PrologTypes.COMMENT; }

   {NON_PRINTABLE}                                 { return TokenType.WHITE_SPACE; }

}