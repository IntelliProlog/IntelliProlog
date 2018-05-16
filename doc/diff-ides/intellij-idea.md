## IntelliJ IDEA

The IntelliJ IDEA IDEs, the Community and Ultimate editions provide the most functionality out of
all the JetBrains IDEs since the IntelliJ Platform is the foundation of the IntelliJ IDEA Community
edition, they also include the `com.intellij.modules.java` module. The Android Studio IDE also
includes the `com.intellij.modules.java` module.

The Ultimate edition is based on the Community Edition and has extra proprietary modules from
`com.intellij.modules.ultimate` and the database modules in `com.intellij.modules.database`.

So if we develop a plugin that uses classes or interface from these modules, we will be restricting
our plugin to being able to be used in the IntelliJ IDEA, Ultimate edition if we use the
`com.intellij.modules.ultimate` and `com.intellij.modules.database`, and Android Studio IDEs.

The Ultimate edition allows us to install nearly all the available plugins for IntelliJ IDEs, JetBrains provides plugins that add the features of their other IDEs.
