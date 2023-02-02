## Commenting

The ability of commenting source code is one of the most important any programming language must provide.
The reason why this is of such an importance is mainly for documenting the source code, but also for
the debugging process, it allows us to keep parts of the program in the file and not be executed.

Being able to select a portion of code and quickly comment it in or out, using the IDE, allows the
developer to be more productive.

Providing commenting capabilities in IDEA is very simple, we only need to provide the plugin an
implementation of the `Commenter` interface [@COMMENTER] and register it with our plugin.

### Commenter implementation

The `Commenter` interface defines 5 methods to be implemented, these are:

+ `String getLineCommentPrefix()`, returns the line comment string for the language we are commenting, in Prolog this is `%`.
+ `String getBlockCommentPrefix()`, returns the block comment prefix string for the language we are commenting, in Prolog this is `/*`.
+ `String getBlockCommentSuffix()`, returns the block comment suffix string for the language we are commenting, in Prolog this is `*/`.
+ `String getCommentedBlockCommentPrefix()`, returns the commented block comment prefix string for the language we are commenting, in Prolog this is `/**`.
+ `String getCommentedBlockCommentSuffix()`, returns the commented block comment suffix string for the language we are commenting, in Prolog this is `*/`.

### Register commenter

The `Commenter` needs to be added to the extensions section of the `plugin.xml` file,
the element that needs to be added is visible in listing \ref{reg:commenter}.

\begin{listing}[h]
\inputminted[breaklines, autogobble, fontsize=\footnotesize,firstline=38, lastline=38]{xml}{code-resources/META-INF/plugin.xml}
\caption{Commenter registration}
\label{reg:commenter}
\end{listing}
