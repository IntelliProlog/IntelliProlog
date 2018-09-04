package ch.heiafr.intelliprolog;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import ch.heiafr.intelliprolog.psi.PrologTypes;
import com.intellij.psi.TokenType;


%%
 // OPTIONS
%class PrologLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{   return;
%eof}

%{

%}


 // MACROS
LPAREN = "("
RPAREN = ")"
LBRACKET = "["
RBRACKET = "]"
LBRACE = "{"
RBRACE = "}"

CUT = "!"
DOT = "."

//CONS = "[|]"  //specific to SWI
//MAP_OP = {DOT}

BIN_PREFIX = "0b"
OCT_PREFIX = "0o"
HEX_PREFIX = "0x"

CHAR_CODE = "0'"
CHAR_CODE_ESCAPED = "0'\\"

CRLF = \R
WHITE_SPACE = [\ \n\t\f]

SIGN = "+" | "-"
EXPONENTIATION = "e"|"E"

SIMPLE_INTEGER = [:digit:]+
INTEGER = {SIGN}? {SIMPLE_INTEGER} ({EXPONENTIATION} {SIGN}? {SIMPLE_INTEGER})?
FLOAT = {SIGN}? {INTEGER} {DOT} {INTEGER} ({EXPONENTIATION} {SIGN}? {SIMPLE_INTEGER})?
BIN_NUMBER = {SIGN}? [0-1]+
OCT_NUMBER = {SIGN}? [0-7]+
HEX_NUMBER = {SIGN}? [a-fA-F0-9_]+

SINGLE_QUOTE = "'"
DOUBLE_QUOTE = \"

ATOM_CHAR = [:jletterdigit:]
UNQUOTED_ATOM = [:lowercase:] {ATOM_CHAR}*

ANONYMOUS_VARIABLE = "_" {ATOM_CHAR}*
NAMED_VARIABLE = [:uppercase:] {ATOM_CHAR}*

STYLE_COMMENT = ("%!"|"%%")[^\r\n]*
END_OF_LINE_COMMENT = ("%")[^\r\n]*
BLOCK_COMMENT = "/*" [^*] ~"*/" | "/*" "*"+ "/"
DOC_COMMENT = "/**" {DOC_COMMENT_CONTENT} "*"+ "/"
DOC_COMMENT_CONTENT = ( [^*] | \*+ [^/*] )*
COMMENT = {STYLE_COMMENT} | {END_OF_LINE_COMMENT} | {BLOCK_COMMENT} | {DOC_COMMENT}
ANY_BLOCK_COMMENT = {BLOCK_COMMENT}|{DOC_COMMENT}

NON_PRINTABLE = ({CRLF}|{WHITE_SPACE})+
NON_CODE = ({NON_PRINTABLE}|{ANY_BLOCK_COMMENT})+

COMBINABLE_OPERATOR_SYMBOLS = "<" | ">" | "?" | "/" | ";" | ":" | "\\" | "|" | "=" | "+" | "-" | "*" | "&" | "^" | "$" | "#" | "@" | "~"

COMMA = ","
NON_COMBINABLE_OPERATOR_SYMBOLS = {COMMA} | {CUT}

OPERATOR_SYMBOLS = {NON_COMBINABLE_OPERATOR_SYMBOLS} | {COMBINABLE_OPERATOR_SYMBOLS}+ | ({COMBINABLE_OPERATOR_SYMBOLS} | {DOT}){2}

//parenthesized operator symbols
PAR_OPERATOR_SYMBOLS = {NON_COMBINABLE_OPERATOR_SYMBOLS} | ({COMBINABLE_OPERATOR_SYMBOLS} | {DOT})+

 // LEXICAL STATES
%state SENTENCE, PARENTHESIZED_SYMBOLS, SINGLE_QUOTE_STRING, DOUBLE_QUOTE_STRING, CHAR_CODE

%%

<CHAR_CODE> {

    ({CHAR_CODE_ESCAPED}|{CHAR_CODE}) .             { yybegin(SENTENCE); return PrologTypes.CHAR_CODE; }

}

<PARENTHESIZED_SYMBOLS> {

    {PAR_OPERATOR_SYMBOLS}                          { yybegin(SENTENCE); return PrologTypes.SYMBOLIC_ATOM;}

}



<YYINITIAL, SENTENCE, PARENTHESIZED_SYMBOLS> {

   {COMMENT}                                       { return PrologTypes.COMMENT; }

   {NON_PRINTABLE}                                 { return TokenType.WHITE_SPACE; }

}



<YYINITIAL, SENTENCE> {

    {SINGLE_QUOTE}                                  { yybegin(SINGLE_QUOTE_STRING);}

    {DOUBLE_QUOTE}                                  { yybegin(DOUBLE_QUOTE_STRING);}

    {UNQUOTED_ATOM}/{LPAREN}                        { yybegin(SENTENCE); return PrologTypes.UNQUOTED_COMPOUND_NAME; }

    {LPAREN} {NON_CODE}* {PAR_OPERATOR_SYMBOLS} {NON_CODE}* {RPAREN}
                                                    { yybegin(PARENTHESIZED_SYMBOLS); yypushback(yylength() - 1); return PrologTypes.LPAREN; }

    {LPAREN}                                        { yybegin(SENTENCE); return PrologTypes.LPAREN; }

    {LBRACKET}                                      { yybegin(SENTENCE); return PrologTypes.LBRACKET; }

    {LBRACE}                                        { yybegin(SENTENCE); return PrologTypes.LBRACE; }

    {BIN_PREFIX} {BIN_NUMBER}                       { yybegin(SENTENCE); return PrologTypes.BIN_NUMBER;}

    {OCT_PREFIX} {OCT_NUMBER}                       { yybegin(SENTENCE); return PrologTypes.OCT_NUMBER;}

    {HEX_PREFIX} {HEX_NUMBER}                       { yybegin(SENTENCE); return PrologTypes.HEX_NUMBER;}

    {INTEGER}                                       { yybegin(SENTENCE); return PrologTypes.INTEGER;}

    {FLOAT}                                         { yybegin(SENTENCE); return PrologTypes.FLOAT;}

    {ANONYMOUS_VARIABLE}                            { yybegin(SENTENCE); return PrologTypes.ANONYMOUS_VARIABLE;}

    {NAMED_VARIABLE}                                { yybegin(SENTENCE); return PrologTypes.NAMED_VARIABLE;}

    {CHAR_CODE_ESCAPED}                             { yybegin(CHAR_CODE); yypushback(3); }

    {CHAR_CODE}                                     { yybegin(CHAR_CODE); yypushback(2); }

    {UNQUOTED_ATOM}                                 { yybegin(SENTENCE); return PrologTypes.UNQUOTED_ATOM;}

    {OPERATOR_SYMBOLS}                              { yybegin(SENTENCE); return PrologTypes.SYMBOLIC_ATOM;}
}



<SENTENCE> {

    {RPAREN}                                        { return PrologTypes.RPAREN; }

    {RBRACKET}                                      { return PrologTypes.RBRACKET; }

    {RBRACE}                                        { return PrologTypes.RBRACE; }

    {DOT}                                           { yybegin(YYINITIAL); return PrologTypes.DOT; }

}



<SINGLE_QUOTE_STRING, DOUBLE_QUOTE_STRING> {

    <<EOF>>                                         {
                                                        yybegin(YYINITIAL); //out of memory errors if this line is not present
                                                        return TokenType.BAD_CHARACTER; }

    {CRLF}                                          {
                                                        yybegin(YYINITIAL);
                                                        return TokenType.BAD_CHARACTER;
                                                    }

    ("\\" | "\n" | "\r" | "\t")                     {}
}



<SINGLE_QUOTE_STRING> {

    {SINGLE_QUOTE} {SINGLE_QUOTE}                   {}

    "\\" {SINGLE_QUOTE}                             {}

    {SINGLE_QUOTE}/{LPAREN}                         {
                                                        yybegin(SENTENCE);
                                                        return PrologTypes.QUOTED_COMPOUND_NAME;
                                                    }

    {SINGLE_QUOTE}                                  {
                                                        yybegin(SENTENCE);
                                                        return PrologTypes.QUOTED_ATOM;

                                                    }

    [^'\n\r\t\\]+                                   { }

}



<DOUBLE_QUOTE_STRING> {

    "\\" {DOUBLE_QUOTE}                             {}

    {DOUBLE_QUOTE}                                  {
                                                        yybegin(SENTENCE);
                                                        return PrologTypes.STRING;

                                                    }

    [^\"\n\r\t\\]+                                  { }

}


    .                                               {
                                                        //System.out.println("BAD_CHARACTER: " + yytext());
                                                        return TokenType.BAD_CHARACTER;
                                                    }
