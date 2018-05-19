# Build instructions

This document describes how to build the IntelliProlog.

## Requirements

The requirements for building the plugin are:

+ IntelliJ IDEA Community or Ultimate edition
+ Plugin DevKit plugin for IntelliJ IDEA
+ Grammar-Kit plugin for IntelliJ IDEA
+ PsiViewer plugin for IntelliJ IDEA
+ JDK for Java 8

## Setting up IntelliJ IDEA

To be able to build the plugin the JDK for Java 8 and IntelliJ Platform SDK need to be configured in
IDEA, the steps to configure these can be found at [https://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/setting_up_environment.html](https://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/setting_up_environment.html).

## Importing project

Import the project using the `Open` button on the IDEA Welcome screen and choose the folder containing
the source of the plugin.

## Generating Lexer and Parser classes

Before being able to run or build the plugin, you need to generate the Parser and Lexer classes. This
is done by selecting `Generate Parser Code` in the context menu for the `Prolog.bnf` file for the Parser.
For the Lexer select `Run JFlex Generator` from the context menu of the `Prolog.flex`file.

## Running and debugging

If your IDEA is properly set up, you can click the `Run` or `Debug` buttons in the toolbar or the same
menu items in the `Run` menu. This will launch a new instance of IDEA with the plugin configured.

## Build the plugin for deployment

To build the plugin make sure that you can run the plugin using the `Run` button.

Building the plugin for deployment is achieved by selecting `Prepare Plugin Module 'IntelliProlog' For Deployment`
in the `Build` menu. This will create a jar in the source folder of the project, this can then be
provided to people to install locally or uploaded to the [JetBrains Plugins Repository](https://plugins.jetbrains.com/).
