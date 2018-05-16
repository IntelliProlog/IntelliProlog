# Highlighting

Highlighting is used to visually distinguish distinct elements of source code to help programmers
identify errors and get a better overall view of the source code they are reading or writing.


## Introduction

Highlighting in IntelliJ is achieved through 3 parts, syntax highlighting, error highlighting and
annotation.

Within IntelliJ IDEA highlighting is provided by the lexer, parser and the [`Annotator`](https://upsource.jetbrains.com/idea-ce/file/idea-ce-d00d8b4ae3ed33097972b8a4286b336bf4ffcfab/platform/analysis-api/src/com/intellij/lang/annotation/Annotator.java) interface.
The quality of the highlighting is very dependent on the quality of the lexing and parsing, for this
reason we need to ensure that our lexing and parsing is done correctly. Even if the the lexing and
parsing are not perfect we can still have highlighting that is useful.

In this section we will see what is needed to perform syntax highlighting, basic error highlighting
and possible ways to improve it, and finally how to add annotations to our source code.

