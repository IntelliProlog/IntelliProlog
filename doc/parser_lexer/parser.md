## Parser

A parser for the IntelliJ Platform is composed of three elements, these elements being:

+ A token type, represented by a class that extends `IElementType` [@IElType], used for the lexer which we will see soon but
  it is easier to define it now.
+ An element type, also represented by a class extending `IElementType`, used for the parser.
+ A grammar in the Backus-Naur Form [@BNF].

### Token and element type

To create our token and element types we simply need to create two classes extending `IElementType`
and JetBrains recommends to put both of these in a `psi` package within your project.

The token type class, depicted in listing \ref{code:prologtokentype}, has a constructor with a string argument that calls the superclass constructor
with the string parameter and the instance of our custom language that we created at the beginning
of this tutorial. We also override the `toString` method and return the string returned by the super
class appended to `PrologTokenType`, this mainly helps when debugging the parser and lexer.

\begin{listing}[h]
\inputminted[firstline=7, lastline=16, breaklines, autogobble, fontsize=\footnotesize]{java}{code-source/ch/eif/intelliprolog/psi/PrologTokenType.java}
\caption{PrologTokenType class}
\label{code:prologtokentype}
\end{listing}

The element type class, depicted in listing \ref{code:prologelementtype} is very similar to the token type class, it has the same constructor and that
normally would be all but in our plugin we added two methods to help identify the element when doing
the syntax highlighting. These methods are:

+ `boolean isParenthesis(IElementType elementType)`, checks if the element passed as argument is a
  parenthesis, either a left parenthesis or right parenthesis.
+ `boolean isBracket(IElementType elementType)`, checks if the element passed as argument is a
  bracket, either a left bracket or right bracket.

These two methods are helper functions, and therefore not necessary in your own plugin.

\begin{listing}[h]
\inputminted[firstline=7, lastline=19, breaklines, autogobble, fontsize=\footnotesize]{java}{code-source/ch/eif/intelliprolog/psi/PrologElementType.java}
\caption{PrologElementType class}
\label{code:prologelementtype}
\end{listing}

### Grammar

We are now getting to the meat of the parser, defining our context-free grammar for our language.

A context-free grammar [@CFG], for people who haven't
attended a computation theory course at university, is a type of formal grammar, a way of describing
a language that has a set of strict rules.

In our case we write the context-free grammar using the Backus-Naur Form, which is a formal way of
describing a context-free grammar.

#### Grammar-kit parser options

To generate our parser definition for the IntelliJ Platform we are going to use the Grammar-Kit plugin [@GKP] we installed during the setup part of our project.

So that Grammar-Kit can produce the parser definition correctly, we need to define a couple of parts
not directly related to the grammar definition. These elements are:

+ The name of the parser class, in our plugin this is `PrologParser`.
+ The class the parser class extends, for the IntelliJ Platform this is `ASTWrapperPsiElement` [@ASTWE].
+ The PSI class prefix, in our plugin this is `Prolog`.
+ The PSI implementation class suffix, which is `Impl`.
+ The package where the generated PSI elements should be stored, for our plugin this is `ch.eif.intelliprolog.psi`.
+ The package where the generated implementation of the PSI elements should be stored, for our plugin
  this is `ch.eif.intelliprolog.psi.impl`.
+ The element type holder class, an interface containing references to all the types in our parser as
  well as a factory for creating these elements when they are encountered during the parsing
  process, in our own plugin this is `ch.eif.intelliprolog.psi.PrologTypes`.
+ The element type class, the class we created earlier.
+ The token type class, the class we created earlier.

The definition of these values are written between braces at the top of our grammar definition file,
the definition from our plugin is depicted in listing \ref{code:grammarkit-options}.

\begin{listing}[h]
\inputminted[firstline=1, lastline=14, breaklines, autogobble, fontsize=\footnotesize]{BNF}{code-source/ch/eif/intelliprolog/Prolog.bnf}
\caption{Grammar-Kit parser options}
\label{code:grammarkit-options}
\end{listing}

#### Grammar-kit grammar definition

The grammar definition for our plugin is taken from the logtalk3 intellij plugin [@lgt3plug] and adapted for our plugin.

The reason we used the grammar definition from the logtalk plugin is because logtalk as is discussed
in the prolog implementations comparison, logtalks syntax is a superset of GNU-Prolog and creating a
correct and efficient grammar definition is a long and difficult task that can take a lot of time.

We did try to write our own grammar definition based on a Prolog BNF grammar [@ProBNF]
found on GitHub. The problem with this BNF grammar is that it uses left recursion, which is not fully
supported in Grammar-Kit, so we fell back on the logtalk grammar definition.

The logtalk grammar definition most likely could be improved, but decided we would not try to during
this project and leave it for future improvements.

In this grammar definition we do not use some of the more advanced features of Grammar-Kit, if you
want more information about these features visit the GitHub repository [@GKP]-GH, we will not cover these more advanced features since they
are outside the scope of this project.

An extract of the final grammar definition for our Prolog plugin, can be found in listing \ref{code:bnfgrammar}.

\begin{listing}[h]
\inputminted[linenos, breaklines, autogobble, fontsize=\footnotesize, firstline=1, lastline=24]{BNF}{code-source/ch/eif/intelliprolog/Prolog.bnf}
\caption{Extract from the BNF grammar definition}
\label{code:bnfgrammar}
\end{listing}

#### Generation and testing of grammar

After we have created our grammar definition we need to generate the parser with PSI classes, thanks
to the Grammar-Kit plugin this is very easy. To generate the PSI classes we just have to select
`Generate Parser Code` from the context menu of our BNF file. After the generation do not forget to
mark the `gen` folder as `Generated Sources Root` using the using the context menu of the folder in
question.

If we want to test our grammar definition before generating it, this is also possible using the
Grammar-Kit plugin, just click on `Live Preview` in the context menu of the BNF file.
