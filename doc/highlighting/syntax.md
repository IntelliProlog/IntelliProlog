## Syntax highlighting

Syntax highlighting as briefly explained in the introduction is achieved with the lexer and the tokens
that are generated when lexing the file. These tokens can be assigned a [`TextAttributesKey` class](https://upsource.jetbrains.com/idea-ce/file/idea-ce-5ecc06ee734c5a9c83a92cbf28c9dd3030293fc7/platform/core-api/src/com/intellij/openapi/editor/colors/TextAttributesKey.java), which contains the information of how the token should
be colored and represented.

To implement syntax highlighting we need to first define a `SyntaxHighlighter` and  a `SyntaxHighlighterFactory` we can then register the `SyntaxHighlighterFactory` with our plugin.

To provide users a way to define their own colours for the syntax highlighting, we need to implement
the [`ColorSettingsPage` interface] and register it with our plugin.

### PrologSyntaxHighlighter

To provide the `TextAttributesKey` classes we use a class that implements the [`SyntaxHighlighter` interface](https://upsource.jetbrains.com/idea-ce/file/idea-ce-32b2fa21845ae8598f946709d2aa98c005add383/platform/editor-ui-api/src/com/intellij/openapi/fileTypes/SyntaxHighlighter.java), we will use the [`SyntaxHighlighterBase` class](https://upsource.jetbrains.com/idea-ce/file/idea-ce-32b2fa21845ae8598f946709d2aa98c005add383/platform/editor-ui-api/src/com/intellij/openapi/fileTypes/SyntaxHighlighterBase.java) as the base class for
our own implementation.

Our `PrologSyntaxHighlighter` class will extend `SyntaxHighlighterBase` as described above, we will
implement the two methods defined by `SyntaxHighlighter`, these are:

+ `Lexer getHighlightingLexer()`, this method just returns the lexer used for highlighting the file,
  we return our `PrologLexerAdapter`.
+ `TextAttributesKey[] getTokenHighlights(IElementType tokenType)`, this method does the heavy lifting
  for the syntax highlighting, we have a long chain of `if/else if` that compares the type of the
  token and returns the `TextAttributesKey` array for the token type.

We also have a second `getTokenHighlights` method but it takes a `PsiElement` as argument and will be
used by the annotator we will implement further on in this section.

#### TextAttributesKey

IntelliJ provides a set of default `TextAttributesKey` values in the [`DefaultLanguageHighlighterColors` class](https://upsource.jetbrains.com/idea-ce/file/idea-ce-5a00c10a69088737a364efbc82a082207a598b45/platform/editor-ui-api/src/com/intellij/openapi/editor/DefaultLanguageHighlighterColors.java).

To create a `TextAttributesKey`, we need to call static `TextAttributesKey createTextAttributesKey(String externalName, TextAttributes defaultAttributes)` method and give it a unique identifier and `TextAttributes` arguments.

#### TextAttributesKey array

The reason we return an array of `TextAttributesKey` classes, is that we can layer them, we can have
one `TextAttributesKey` for the color, another for the size and a final one for the font weight.

In our plugin at the moment we always return a single element array. In our `PrologSyntaxHighlighter`,
we define constants for the `TextAttributesKey` classes, an example of the `TextAttributesKey` for
integers can be seen in listing \ref{code:integer_term} and the corresponding array in listing
\ref{code:integer_term_keys}.

\begin{listing}[H]
\inputminted[breaklines, fontsize=\footnotesize,firstline=32, lastline=33]{java}{code-source/ch/eif/intelliprolog/editor/PrologSyntaxHighlighter.java}
\caption{`TextAttributesKey` for integers}
\label{code:integer_term}
\end{listing}

\begin{listing}[H]
\inputminted[breaklines, fontsize=\footnotesize,firstline=91, lastline=91]{java}{code-source/ch/eif/intelliprolog/editor/PrologSyntaxHighlighter.java}
\caption{`TextAttributesKey` array for integers}
\label{code:integer_term_keys}
\end{listing}

### PrologSyntaxHighlighterFactory

The [`SyntaxHighlighterFactory` abstract class](https://upsource.jetbrains.com/idea-ce/file/idea-ce-dba03e40ff8fc26feb037493ca72af40c273dfa4/platform/editor-ui-api/src/com/intellij/openapi/fileTypes/SyntaxHighlighterFactory.java) provides the means of registering our `SyntaxHighlighter` class with our plugin.

To use it we simply extend it and implement it's abstract method `SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile)`, this method returns an instance of our `PrologSyntaxHighlighter`.

#### Registering the syntax highlighter

The `SyntaxHighlighterFactory` needs to be added to the extensions section of the `plugin.xml` file,
the element that needs to be added is visible in listing \ref{reg:syntaxhighlighterfactory}.

\begin{listing}[H]
\inputminted[breaklines, fontsize=\footnotesize,firstline=36, lastline=36]{xml}{code-resources/META-INF/plugin.xml}
\caption{SyntaxHighlighterFactory registration}
\label{reg:syntaxhighlighterfactory}
\end{listing}

### ColorSettingsPage

The `ColorSettingsPage` allows the users of the plugin to change the colours associated with the
elements of the file.

To implement the `ColorSettingsPage` interface we create a `PrologColorSettingsPage` class implementing
the methods defined in it, these methods are:

+ `Icon getIcon()`, this method is used to define what `Icon` should be displayed, we return the icon
  defined in our `PrologIcons` file.
+ `SyntaxHighlighter getHighlighter()`, this method is used to return the `SyntaxHighlighter` we
  created above.
+ `String getDemoText()`, this method is used to define a string of text to demonstrate the changes
  applied to the colours of the elements. Preferably this text should contain all the elements that
  are being highlighted. An example is visible in listing \ref{code:getdemotext}
+ `Map<String,TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap()`, this method is used
  if we define some additional elements not highlighted by the syntax highlighter in the demo text.
  In our plugin we do not use this so we return `null`.
+ `AttributesDescriptor[] getAttributeDescriptors()`, this method returns an array containing all the
  `AttributesDescriptor` instances, these contain a string with the name of the element as well as a reference to the corresponding `TextAttributesKey`. An example of this can be found in the listings \ref{code:DESCRIPTORS} and \ref{code:getAttributeDescriptors}.
+ `ColorDescriptor[] getColorDescriptors()`, this method returns an array of `ColorDescriptor` for
  defining fore and background colours, in our plugin we return `ColorDescriptor.EMPTY_ARRAY`.
+ `String getDisplayName()`, this method returns the name to display in the settings, we return `Prolog`.

\begin{listing}[H]
\inputminted[breaklines, fontsize=\footnotesize,firstline=46, lastline=55]{java}{code-source/ch/eif/intelliprolog/PrologColorSettingsPage.java}
\caption{getDemoText example}
\label{code:getdemotext}
\end{listing}

\begin{listing}[H]
\inputminted[breaklines, fontsize=\footnotesize,firstline=16, lastline=30]{java}{code-source/ch/eif/intelliprolog/PrologColorSettingsPage.java}
\caption{AttributesDescriptor array example}
\label{code:DESCRIPTORS}
\end{listing}

\begin{listing}[H]
\inputminted[breaklines, fontsize=\footnotesize,firstline=65, lastline=67]{java}{code-source/ch/eif/intelliprolog/PrologColorSettingsPage.java}
\caption{getAttributeDescriptors example}
\label{code:getAttributeDescriptors}
\end{listing}

#### Registering the color settings page

The `ColorSettingsPage` needs to be added to the extensions section of the `plugin.xml` file,
the element that needs to be added is visible in listing \ref{reg:prologcolorsettingspage}.

\begin{listing}[H]
\inputminted[breaklines, fontsize=\footnotesize,firstline=39, lastline=39]{xml}{code-resources/META-INF/plugin.xml}
\caption{ColorSettingsPage registration}
\label{reg:prologcolorsettingspage}
\end{listing}
