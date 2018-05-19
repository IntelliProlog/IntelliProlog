# Differences between the JetBrains IDEs

JetBrains provides a large choice of different IDEs, their main IDE being IntelliJ IDEA, they also
provide some other IDEs like PyCharm, WebStorm and GoLand.

## Introduction

In this section we will compare JetBrains IDEs, the differences in their architecture and how this
affects the development of plugins for them.

We will focus on IntelliJ IDEA [@IDEA] and PyCharm [@PyCharm]
since both of these provide Community editions and are therefore free and open source software,
which means we can inspect their source code, available on GitHub, IntelliJ IDEA GitHub repo [@IDEAC-GH] and PyCharm GitHub repo [@PyCharm]-GH.

The reason we are going through these differences is that during the process of this project, we saw
in some of the forums and the Plugin Compatibility with IntelliJ Platform Products page [@PCIPP] that not all JetBrains IDEs are created equal. All of them are based on the
`IntelliJ Platform`. Some of the IDEs have their own extra modules that are not present in the others.

The modules included in all the IDEs are:

+ `com.intellij.modules.platform`
+ `com.intellij.modules.lang`
+ `com.intellij.modules.vcs`
+ `com.intellij.modules.xml`
+ `com.intellij.modules.xdebugger`

So if we want to develop a plugin for all the JetBrains IDEs we should only use classes and interfaces
from these modules.

Next we will take a more in detail look at IntelliJ IDEA and PyCharm.
