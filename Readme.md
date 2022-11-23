# IntelliProlog - A Prolog Plugin for IntelliJ Idea

## Local development
### Prerequisites
* IntelliJ Idea - Latest version (Ultimate is recommended)
  * Grammar Kit Plugin
  * DevKit Plugin
  * PSI Viewer Plugin
  
* JDK 11
* Gradle 7.3
* Git
* Prolog - GnuProlog

### Setup
1. Clone the repository
2. Open the project in IntelliJ Idea
3. Open the Gradle tab and run the `initProject` task
   * This will generate the parser and lexer files and compile source files
4. Open the `Run/Debug Configurations` and add a new `Gradle` configuration with the following settings:
   * Name: `IntelliProlog`
   * Gradle project: `intelli-prolog-2`
   * Run: `runIde`

### Running
1. Choose the `IntelliProlog` configuration and click the `Run` or `Debug` button
2. IntelliJ Idea should open with the plugin installed and ready to use
3. To test the plugin, open a Prolog file and try to use the plugin features


## Pipeline

### Informations
* The pipeline is configured to run on all branch. The release job is only triggered when a tag is pushed.
* The pipeline use a custom docker image to run the tests. The Dockerfile is located in the `docker` folder.
* The docker image is located in the [Private Docker Registry](https://gitlab.forge.hefr.ch/frederic.bapst/intelli-prolog-2/container_registry) of the project

### Usage of the pipeline
The pipeline is used to automatically build and test the plugin.
 - The test job will run the tests and upload the results as an artifact. It contains the test results as HTML.
 - The build job will build the plugin and upload the artifacts. It contains the plugin jar file.
 - The release job will create a new ZIP file containing the plugin jar file and the plugin XML file. It can be used to install the plugin manually.

