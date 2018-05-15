## Runner Configuration

The runner configuration is the main part of this project, from the start the primary goal for this
plugin was to facilitate the launching of our source files instead of having to run the GNU-Prolog
REPL in a separate terminal or if on Windows in a different GUI than where we wrote our source code.

In the previous steps of our tutorial we went through how to setup an SDK and Module for our project,
in this step of our tutorial we will finally use those elements to implement our runner configuration.

A runner configuration is composed of several different elements, these elements being:

+ ConfigurationType
+ RunConfiguration
+ RunProfileState

