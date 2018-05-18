# File Launcher

The file launcher functionality is the main part of this project, the reason being that the students
for which this plugin was created for, used to have to write their source code in a text editor or
an IDE and then having to switch over to a terminal or the graphical interface of GNU-Prolog on
Windows to run their code.

This situation was not very enjoyable though for the students and was also not very productive. So to
remediate to this situation the project was centered around the ability to be able to launch the
desired file directly from IntelliJ IDEA, without having to open a separate window or change to a
differenct window.

In this chapter we will go over the necessary parts necessary to enable this functionality in a plugin
for IntelliJ IDEs.

The sections are:

* Introduction: Setup the basics for recognising files of the desired language, in our case Prolog
* SDK: Creating an SDK to represent the execution of our source code
* Module: Creating the implementation of modules to allow separating of settings between different
  environments
* Actions: The implementation of launching the file using actions to launch a file

## Introduction

The basics for recognising files of the programming language that is targeted by the plugin we are
developing is creating classes that represent the language, these classes are:

* A class subclassing the Language class provided by Jetbrains
* A class subclassing the LanguageFileType class provided by Jetbrains
* A class subclassing the FileTypeFactory class provided by Jetbrains

If we want to provide icons for our plugin we can also create another class grouping all the icons
we desire to be present in our plugin.

### Language subclass

The `Language` subclass is used to define a language for our plugin and is used in a lot of
situations during the development of plugins for the Jetbrains IDEs.

For our project which is targeting the language GNU-Prolog, we will generalise to Prolog so we will call
the class `PrologLanguage`. The `Language` subclass is a singleton and therefore has a private
constructor and a static instance field as seen in listing \ref{code:prologlanguage}.

\begin{listing}[h]
\inputminted[breaklines, fontsize=\footnotesize]{java}{code-source/ch/eif/intelliprolog/PrologLanguage.java}
\caption{PrologLanguage class}
\label{code:prologlanguage}
\end{listing}

### LanguageFileType subclass

The `LanguageFileType` subclass is used to identify and describe a file that belongs to our language,
for these reasons it has a couple of methods that define basic information for our file type.

The information that we need to define is:

* The name to call our file type
* A description of our file type
* The default extension for our file type
* The icon to use for our file type, we will get to icons in a bit

There are a couple other methods that you can override, but these are not explicitly necessary for
our plugin, these can be found by looking at the source of [`LanguageFileType`](https://upsource.jetbrains.com/idea-ce/file/idea-ce-12c6e6cd02d57c0ab4fd314f62b4ecb94841a0fa/platform/core-api/src/com/intellij/openapi/fileTypes/LanguageFileType.java).

The example from our plugin is depicted in listing \ref{code:prologfiletype}.

\begin{listing}[h]
\inputminted[breaklines, fontsize=\footnotesize]{java}{code-source/ch/eif/intelliprolog/PrologFileType.java}
\caption{PrologFileType class}
\label{code:prologfiletype}
\end{listing}

### FileTypeFactory subclass

The `FileTypeFactory` subclass is used to register our new FileType with the IntelliJ IDE, which
provides a platform extension point named `com.intellij.fileTypeFactory`.

The only thing we need to do in the `FileTypeFactory` is override the method named `void createFileTypes(@NotNull FileTypeConsumer consumer)` which calls the `consume` method on the `FileTypeConsumer`and
pass it the instance of our FileType.

The example from our plugin is depicted in the listing \ref{code:prologfiletypefactory}.

\begin{listing}[h]
\inputminted[breaklines, fontsize=\footnotesize]{java}{code-source/ch/eif/intelliprolog/PrologFileTypeFactory.java}
\caption{PrologFileTypeFactory class}
\label{code:prologfiletypefactory}
\end{listing}

The registration of our FileTypeFactory is done in the `plugin.xml` file found in the
`resources/META-INF` folder of our project.

The line depicted in the listing \ref{reg:filetypefactory} needs to be inserted in the `extensions` part of the file:

\begin{listing}[h]
\inputminted[breaklines, fontsize=\footnotesize, firstline=33, lastline=33]{xml}{code-resources/META-INF/plugin.xml}
\caption{FileTypeFactory registration}
\label{reg:filetypefactory}
\end{listing}

### Icons class

We can create a class that is entirely responsible for loading and keeping references to the icons we wish to use in our plugin. This will be a standard Java class that only has static fields, each loading and keeping a reference to a particular icon.

An example for our plugin is depicted in listing \ref{code:prologicons}

\begin{listing}[h]
\inputminted[breaklines, fontsize=\footnotesize]{java}{code-source/ch/eif/intelliprolog/PrologIcons.java}
\caption{PrologIcons class}
\label{code:prologicons}
\end{listing}

This class can then be used in all our other classes that need references to icons.
