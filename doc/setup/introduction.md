# Project Setup

This chapter goes over the necessary setup needed for developing a plugin for the IntelliJ platform.
We will also go over the different plugins that will be used during the development of our Prolog plugin.

This chapter is divided into 3 main sections, these being:

1. Introduction
1. SDK setup
1. Project initialisation

## Introduction

In this section we will go over the requirements needed for the development of IntelliJ plugins.

The most basic requirement needed to develop a plugin for the JetBrains platform is Java, the programming language
that is used to develop and run all IntelliJ IDEs.

The other requirements are the following:

* IntelliJ IDEA Ultimate or Community Edition

* IntelliJ IDEA Community Edition source code

* Plugin DevKit

* Grammar-Kit plugin

* PsiViewer plugin

### IntelliJ IDEA Ultimate or Community Edition

IntelliJ IDEA is needed for the development of our plugin, and two versions exist:

* Ultimate Edition is a commercial closed-source IDE developed by JetBrains and costs €149 the 1st year, but has a 30
  day trial. Students can get a free license with their student email address. The Ultimate Edition allows the use of any
  plugin developed for a JetBrains IDE as well as including other advanced features.
* Community Edition is an Apache 2.0 licensed open-sourced IDE developed by JetBrains. The Community Edition can only
  use plugins developed specifically for it.

The two different editions can be downloaded from the [IntelliJ IDEA website](https://www.jetbrains.com/idea/).

### IntelliJ IDEA Community Edition source code

The IntelliJ IDEA Community Edition source code is not essential but very useful during the development of plugins for
the JetBrains IDEs since it allows easier debugging and inspection of the source code that is the basis of the plugin.

The source code can be checked out from the IntelliJ IDEA Community Edition repository on
[GitHub](https://github.com/JetBrains/intellij-community), using either IntelliJ IDEA or from the command line.

### Plugin Devkit

The Plugin DevKit is an IntelliJ plugin developed by JetBrains, and adds support for developing IntelliJ plugins using
the IntelliJ IDEA build system. It also adds an easy way of building and launching our plugin in a separate instance of
IntelliJ IDEA.

This plugin is bundled with all IntelliJ IDEA IDEs, it just needs to be enabled in the IDEs settings.

### Grammar-Kit plugin

The Grammar-Kit plugin is developed by Greg Shrago and recommended by JetBrains, it helps write and visualize BNF
grammar definitions as well JFlex lexer definitions. It is also capable of generating parser/ElementTypes/PSI classes
and running the JFlex generator. Both of those capabilities are essential for developing a custom language plugin.

This plugin can be installed through the plugin interface in IntelliJ IDEA.

### PsiViewer plugin

The PsiViewer plugin is a Program Structure Interface (PSI) tree viewer, developed by a group of people and recommended
by JetBrains. It allows us to see the tree of ElementTypes defined in our Parser and Lexer defined with the Grammar-Kit
plugin.

This plugin can be installed through the plugin interface in IntelliJ IDEA.
