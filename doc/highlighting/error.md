## Error highlighting

Highlighting errors in IDEA is done by two different ways, these can be used at the same time.

The first way is highlighting lexer errors, these errors occur when the contents of the file do not
follow the rules defined by the lexer.

The second way happens during parsing, these errors occur when a sequence of tokens does not conform
to the rules defined in the grammar of the language. We will not go through how to implement this
type of highlighting.

### Highlighting lexer errors

The highlighting of lexer errors is achieved by specifying in the lexer definition a wildcard rule
using the `dot` character, which is only matched when no other rule matches and returns a token of
type `TokenType.BAD_CHARACTER`. In the syntax highlighter, when we encounter a
`TokenType.BAD_CHARACTER` in the `getTokenHighlights` method, we return the `TextAttributesKey` from
`HighlighterColors.BAD_CHARACTER`.

### Highlighting parser errors

The highlighting of parser errors is achieved, as far as I understand after looking through the [IntelliJ Platform SDK DevGuide](http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/syntax_highlighting_and_error_highlighting.html), the source files and other plugins that have implemented this feature, during the parsing in the `ParserClass` by calling the [`PsiBuilder.error()`](https://upsource.jetbrains.com/idea-ce/file/idea-ce-d00d8b4ae3ed33097972b8a4286b336bf4ffcfab/platform/core-api/src/com/intellij/lang/PsiBuilder.java?nav=8173:8178:focused&line=277&preview=false) when a sequence of tokens does not conform to the grammar of the language.
