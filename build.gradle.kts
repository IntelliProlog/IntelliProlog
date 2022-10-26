fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // Gradle IntelliJ Plugin
    id("org.jetbrains.intellij") version "1.3.0"
    id("org.jetbrains.grammarkit") version "2021.2.2"
}
group = properties("pluginGroup")
version = properties("pluginVersion")

sourceSets{
    main {
        java {
            srcDir("src/gen/java") // Generated sources
        }
    }
    test {
        java {
            srcDir("src/gen/java") // Generated sources
        }
    }
}

// Configure project's dependencies
repositories {
    mavenCentral()
}
// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

// Configure Gradle Grammar-Kit Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-grammar-kit-plugin.html
grammarKit {
    jflexRelease.set("1.7.0-2")
}


tasks {
    // Set the JVM compatibility versions
    properties("javaVersion").let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
    }
    wrapper {
        gradleVersion = properties("gradleVersion")
    }


    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))
    }

    generateLexer(){
        source.set("src/main/java/ch/heiafr/intelliprolog/PrologLexer.flex")
        targetDir.set("src/gen/java/ch/heiafr/intelliprolog/")
        targetClass.set("PrologLexer")
    }

    generateParser(){
        source.set("src/main/java/ch/heiafr/intelliprolog/Prolog.bnf")
        targetRoot.set("src/gen/java/")
        pathToParser.set("PrologParser.java")
        pathToPsiRoot.set("psi")
        purgeOldFiles.set(true)

    }

    // Generate the Lexer and Parser BEFORE building the plugin
    build(){
        dependsOn(generateParser)
        dependsOn(generateLexer)
    }
}
