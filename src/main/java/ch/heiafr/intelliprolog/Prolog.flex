package ch.heiafr.intelliprolog;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static ch.heiafr.intelliprolog.psi.PrologTypes.*;

%%

%{
  public PrologLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class PrologLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+


%%
<YYINITIAL> {
  {WHITE_SPACE}               { return WHITE_SPACE; }

  "COMMENT"                   { return COMMENT; }
  "CRLF"                      { return CRLF; }
  "DOT"                       { return DOT; }
  "STRING"                    { return STRING; }
  "UNQUOTED_ATOM"             { return UNQUOTED_ATOM; }
  "SYMBOLIC_ATOM"             { return SYMBOLIC_ATOM; }
  "QUOTED_ATOM"               { return QUOTED_ATOM; }
  "LPAREN"                    { return LPAREN; }
  "RPAREN"                    { return RPAREN; }
  "UNQUOTED_COMPOUND_NAME"    { return UNQUOTED_COMPOUND_NAME; }
  "QUOTED_COMPOUND_NAME"      { return QUOTED_COMPOUND_NAME; }
  "LBRACKET"                  { return LBRACKET; }
  "RBRACKET"                  { return RBRACKET; }
  "LBRACE"                    { return LBRACE; }
  "RBRACE"                    { return RBRACE; }
  "INTEGER"                   { return INTEGER; }
  "FLOAT"                     { return FLOAT; }
  "BIN_NUMBER"                { return BIN_NUMBER; }
  "OCT_NUMBER"                { return OCT_NUMBER; }
  "HEX_NUMBER"                { return HEX_NUMBER; }
  "CHAR_CODE"                 { return CHAR_CODE; }
  "ANONYMOUS_VARIABLE"        { return ANONYMOUS_VARIABLE; }
  "NAMED_VARIABLE"            { return NAMED_VARIABLE; }


}

[^] { return BAD_CHARACTER; }
