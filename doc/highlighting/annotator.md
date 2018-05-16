## Annotator

The [`Annotator` interface](https://upsource.jetbrains.com/idea-ce/file/idea-ce-d00d8b4ae3ed33097972b8a4286b336bf4ffcfab/platform/analysis-api/src/com/intellij/lang/annotation/Annotator.java) in IntelliJ IDEA displays messages under PSI elements when we hover over them, giving
us more information about what they are. We can also highlight a region of text as a warning or error
and then optionally provide a fix for the error or suggest a better way of writing the code being
highlighted.

In our plugin we will implement some very basic annotations to demonstrate the `Annotator` interface,
we will add information annotations for:

+ Binary operators
+ Left operators
+ Functor keywords
+ Atom keywords

We will implement the `Annotator` interface in the `PrologAnnotator` and then register it with our plugin.

### PrologAnnotator

The `PrologAnnotator` class implements the `Annotator` interface and the only method that is necessary
to implement is the `void annotate(PsiElement element, AnnotationHolder holder)` method.

We will add 3 extra static methods, these are :

+ `boolean shouldAnnotate(PsiElement element)`, this method checks if the `PsiElement` is one of the
  elements we whish to annotate, to accomplish this we use some static methods that we will define in
  the `PrologPsiUtil` class we will create after this.
+ `void highlightTokens(PsiElement element, AnnotationHolder holder, PrologSyntaxHighlighter highlighter)`,
  this method gets a new `TextAttributesKey` array from the highlighter, creates the info annotation on the
  element with a message from the following method. The final part is setting the new `TextAttributesKey` values from the array. This method is depicted in listing \ref{code:highlightTokens}.
+ `String getMessage(PsiElement element)`, this method returns a string describing the element in question.

In the `annotate` method, depicted in the listing \ref{code:annotate}, from the `Annotator` interface we check if we should annotate the element
and if `true`, call the `highlightTokens` method

\begin{listing}[H]
\inputminted[breaklines, fontsize=\footnotesize,firstline=23, lastline=30]{java}{code-source/ch/eif/intelliprolog/editor/PrologAnnotator.java}
\caption{highlightTokens method}
\label{code:highlightTokens}
\end{listing}

\begin{listing}[H]
\inputminted[breaklines, fontsize=\footnotesize,firstline=47, lastline=51]{java}{code-source/ch/eif/intelliprolog/editor/PrologAnnotator.java}
\caption{annotate method}
\label{code:annotate}
\end{listing}

### Register annotator

The `Annotator` needs to be added to the extensions section of the `plugin.xml` file,
the element that needs to be added is visible in listing \ref{reg:annotator}.

\begin{listing}[H]
\inputminted[breaklines, fontsize=\footnotesize,firstline=38, lastline=38]{xml}{code-resources/META-INF/plugin.xml}
\caption{ColorSettingsPage registration}
\label{reg:annotator}
\end{listing}
