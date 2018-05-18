## Folding

Folding allows a developer to hide parts of the source code, to diminish the visual clutter, and allow him or her to focus only on the parts important at the moment.

There are normally different elements that can be folded, most often these elements are:

+ Elements surrounded by pairs of braces, brackets or parenthesis
+ Whole functions, methods or classes
+ Multiline/block comments

In this plugin we are going to provide the ability to fold:

+ Comments
+ Lists, which are surrounded by brackets
+ Sentences/Rules, the Prolog equivalent of functions

To provide folding JetBrains provides like for most of the features we have implemented an interface, the [`FoldingBuilder` interface](https://upsource.jetbrains.com/idea-ce/file/idea-ce-12c6e6cd02d57c0ab4fd314f62b4ecb94841a0fa/platform/core-api/src/com/intellij/lang/folding/FoldingBuilder.java),
that we need to implement and register with our plugin.

### FoldingBuilder

The `FoldingBuilder` interface defines 3 methods that need to be implemented, these are:

+ `FoldingDescriptor[] buildFoldRegions(ASTNode node, Document document)`, builds an array containing all the nodes that can be folded together starting from `node` argument, depicted in listing \ref{code:buildfoldregions}.
+ `String getPlaceholderText(ASTNode node)`, returns the text used to display instead of the folded region, depicted in listing \ref{code:getplaceholder}.
+ `boolean isCollapsedByDefault(ASTNode node)`, sets if a node should start of being folded, an example would be if we wanted all comments to be folded and only become visible when we want to see them, depicted in listing \ref{code:iscollapsedbydefault}.

\begin{listing}[H]
\inputminted[breaklines, fontsize=\footnotesize,firstline=42, lastline=47]{java}{code-source/ch/eif/intelliprolog/editor/PrologFoldingBuilder.java}
\caption{buildFoldRegions method}
\label{code:buildfoldregions}
\end{listing}

\begin{listing}[H]
\inputminted[breaklines, fontsize=\footnotesize,firstline=59, lastline=73]{java}{code-source/ch/eif/intelliprolog/editor/PrologFoldingBuilder.java}
\caption{getPlaceHolderText method}
\label{code:getplaceholder}
\end{listing}

\begin{listing}[H]
\inputminted[breaklines, fontsize=\footnotesize,firstline=75, lastline=78]{java}{code-source/ch/eif/intelliprolog/editor/PrologFoldingBuilder.java}
\caption{isCollapsedByDefault method}
\label{code:iscollapsedbydefault}
\end{listing}

### Register FoldingBuilder

The `FoldingBuilder` needs to be added to the extensions section of the `plugin.xml` file,
the element that needs to be added is visible in listing \ref{reg:foldingbuilder}.

\begin{listing}[h]
\inputminted[breaklines, fontsize=\footnotesize,firstline=43, lastline=43]{xml}{code-resources/META-INF/plugin.xml}
\caption{FoldingBuilder registration}
\label{reg:foldingbuilder}
\end{listing}
