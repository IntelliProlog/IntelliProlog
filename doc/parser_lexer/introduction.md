# Parser and Lexer

The parser and lexer are key to implementing the next features since they will allow us and IntelliJ
IDEA to understand and breakdown our files.

## Introduction

Parsers and lexers are a fundamental part of any programming language compiler since they decompose
a source file into it's component parts and then produces an Abstract Syntax Tree. The Abstract
Syntax Tree defines the structure of the program and can then be used to understand what it is
supposed to do, it is for this reason that parsers and lexers are fundamental when creating a
programming language.

In JetBrains IDEs, parsers and lexers are used so that the IDE can understand the source file it is
editing and provide features that programmers have become very used to and sometimes even rely on.

A non-exhaustive list of some of these features is:

+ Syntax Highlighting
+ Code completion, suggesting variables or methods, etc.
+ Finding usages
+ Refactoring

These features are only possible since the IDE understands our source code, or more precisely the
structure of it, and therefore knows what each character and word represents in the specific
language the parser and lexer were designed for.

In this section we are going to look at how to create a parser and lexer for the IntelliJ Platform.

### Program Structure Interface (PSI)

The `Program Structure Interface` is the layer provided by the IntelliJ Platform and is used for
parsing files and representing the structure of these files.

The PSI is composed of multiple PSI elements in a tree hierarchy, a PSI tree, just like a Abstract
Syntax Tree, it is this PSI tree and its elements that enable some of the most useful features listed
above.

More information about the [`Program Structure Interface`](http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi.html) is available in the [IntelliJ Platform SDK DevGuide](http://www.jetbrains.org/intellij/sdk/docs/welcome.html)
