# Usage

## Installing plugin

The plugin can be installed two ways, depending on if you have the plugin jar file.

### Installing using jar file

If you have been provided with the jar file for the plugin, you can use the `Plugin` menu in the IDEA
settings window. Click on `Install Plugin From Disk…` and select the jar file. Restart IDEA and the
plugin is now ready to be used.

### Installing from Plugins Repository

If you have not been provided with the jar file for the plugin or prefer to install the plugin from
Jetbrains Plugin Repository, you can use the `Plugin` menu in the IDEA
settings window. Click on `Browse Repositories…` and search for `IntelliProlog`, when found just click the `Install` button. Restart IDEA and the
plugin is now ready to be used.

## Creating a Prolog project

To create a new project for Prolog, simply click on `Create New Project` on the IDEA Welcome screen. Select the Prolog option from the project types, click `Next`, and then give your new project a name and define the location where it should created, and finally click `Finish. You now have a Prolog project.

### Setting the Prolog SDK (interpreter)

After your project is created, open the `Project Structure` window, select the `Project` menu option. Once your on the `Project` page, click on the `New` button and select the `GNU-Prolog` option. The file chooser dialog will open at the default installation location on your system for `gprolog`. If you installed GNU Prolog in a different location, navigate to that location. Select the `gprolog` executable and finish by clicking the `Ok` button.

If GNU-Prolog is not automatically selected in the dropdown menu, click the dropdown menu and select GNU-Prolog.

## Using the plugin

### Creating a file

Create a file by using the `New` menu and select the `File` option, name it as you like, just make sure to add the `.pl` extension otherwise IDEA will not recognise it as a Prolog File.

### Launching Prolog files

To launch a Prolog file, right click in the editor with a Prolog file open, there are 3 options to choose from, the options are:

+ `Load <filename> in GNU Prolog interpreter`, this will launch the `gprolog` interpreter and load the selected file.
+ `Load <filename> in GNU Prolog interpreter with trace`, this will launch the `gprolog` interpreter and load the selected file with trace enabled.
+ `Run Prolog REPL`, this will launch the `gprolog` interpreter without loading any file.

All of the options set the working directory of the interpreter to the directory where the file is located.

### Change the syntax highlighting colours

The plugin provides basic syntax highlighting and allows you to change the colours that are used.

To change the colours, open the IDEA Settings, and navigate to the `Prolog` option in `Editor/Color Scheme`. It is possible to change font colour, font weight and fore and background colours to your own liking.
