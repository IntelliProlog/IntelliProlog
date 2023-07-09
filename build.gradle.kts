fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")

    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.7.20"

    // Gradle IntelliJ Plugin
    id("org.jetbrains.intellij") version "1.15.0" //Latest version
    id("org.jetbrains.grammarkit") version "2021.2.2"
}


group = properties("pluginGroup")
version = properties("pluginVersion")

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(properties("kotlinTargetVersion")))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

sourceSets {
    main {
        java {
            srcDirs("src/gen/java") // Generated sources
            runtimeClasspath += files("build/classes/java/main") // Generated resources
        }
    }
}

// Configure project's dependencies
repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
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
        //untilBuild.set(properties("pluginUntilBuild"))
    }


    generateLexer {
        // sourceFile.set(file("src/main/java/ch/heiafr/intelliprolog/Prolog.flex"))
        source.set("src/main/java/ch/heiafr/intelliprolog/Prolog.flex")
        targetDir.set("src/gen/java/ch/heiafr/intelliprolog/")
        targetClass.set("PrologLexer")
        skeleton.set(file("src/main/java/ch/heiafr/intelliprolog/Prolog.skeleton"))
        purgeOldFiles.set(true)
    }


    generateParser {
        try {
            val compiledFilesSources =
                files("build/classes/java/main/")
            classpath.from(compiledFilesSources)
        } catch (e: Exception) {
            // Ignore => no compiled files when running the task for the first time
        }
        //sourceFile.set(file("src/main/java/ch/heiafr/intelliprolog/Prolog.bnf"))
        source.set("src/main/java/ch/heiafr/intelliprolog/Prolog.bnf")
        targetRoot.set("src/gen/java/")
        pathToParser.set("PrologParser.java")
        pathToPsiRoot.set("psi")
        purgeOldFiles.set(false)
    }


    compileJava {
        dependsOn("compileKotlin")
    }

    runIde {
        autoReloadPlugins.set(true)
    }

    register("compileAndRegenerate") {
        dependsOn("compileJava")
        finalizedBy("generateParser")
    }

    register("initProject") {

        doFirst {
            generateParser.get().generateParser()
            generateLexer.get().generateLexer()
            println("Classes generated")
        }
        finalizedBy("compileAndRegenerate")
    }
}

tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.WARN
}





