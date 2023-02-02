## Structure of project

The structure of the project will be as follows:

* Tutorial: How to create a custom language plugin
* Differences between the JetBrains IDEs
* Comparison of the different Prolog implementations
* Quick overview of different licenses for software

### Tutorial: How to create a custom language plugin

The tutorial is the main part of this report, it will go over the main aspects of the development of
a custom language plugin for the IntelliJ IDEA platform.

The features we'll be implementing are the following:

1. Launching the current file in the GNU Prolog interpreter
1. Parser and Lexer for GNU Prolog
1. Highlighting including syntax and errors
1. Code commenting and folding

Before we get to implementing the features, we will go over the steps for setting up a project needed
to develop our IntelliJ Platform plugin.

### Differences between the JetBrain IDEs

During the process of the project we discovered that there are differences between the different
JetBrains IDEs, mainly that the main IntelliJ IDE and the "smaller" IDEs, eg. Goland, PyCharm, WebStorm, etc.

We will go through some of these differences and the impact these have on the development of a
JetBrains IDE plugin.

### Comparison of the different Prolog implementations

In this section of the report we will compare a small sample of the different Prolog implementations
that are available. We will have a quick look at ISO Prolog and Edinburgh Prolog, the
interfacing between Prolog and other programming languages and some other features more specific to
some of the implementations.

### Quick overview of different licenses for software

In this section of the report we will do a quick overview of a couple of different licenses for
software, more specifically Open Source Software (OSS) licenses, the licenses that we will discuss
are the MIT license and Apache 2.0 license.
