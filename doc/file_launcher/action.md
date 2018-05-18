## Action

The action system in IDEA allows plugins to add their own actions in menus and toolbars. An example of an action included in IDEA is the `File | Open File...` .

For our plugin we will define actions to launch the current file in the GNU-Prolog interpreter, this interpreter will run in the IDEA console window.

In this section we will go over the basics of an action as well as the actions we will implement in
our plugin.

### Basic workings of an action

An action is defined using a class that extends the abstract class `AnAction` provided by JetBrains.

The main method in the `AnAction` class is `void actionPerformed(AnActionEvent e)`, the other commonly
used method is `void update(AnActioNEvent e)`.

For the complete list of methods available in the `AnAction` class, checkout the source code [`AnAction.java`](https://upsource.jetbrains.com/idea-ce/file/idea-ce-32b2fa21845ae8598f946709d2aa98c005add383/platform/editor-ui-api/src/com/intellij/openapi/actionSystem/AnAction.java)

The actions are then registered in the `plugin.xml` file under the `actions` section.

#### void actionPerformed(AnActionEvent e)

This method is called when the action in the menu or toolbar is clicked. It is in this method that
your business logic for the action is launched.

#### void update(AnActionEvent e)

This method is called everytime that the view of the action in the menu or toolbar is updated, in the
case of the toolbar this is called twice a second, so it needs to be fast and should not perform any
file system actions. It is mainly used to update the presentation of the action, changing the text of
the action or disabling the action completely if the action would not be able to be executed.

#### Registering actions

To register actions in the plugin we have to add appropriate elements to the `plugin.xml` file, in the actions section.

The parent element is `action` with an id uniquely identifying the action, the class of the action and the text to display. Within the `action` element we can add other elements, these allow to define where the action should be located as well as mouse or keyboard shortcuts. More details are available in the [Actions section](https://www.jetbrains.org/intellij/sdk/docs/basics/action_system.html) of the IntelliJ Platform SDK DevGuide.

### Plugin actions

The actions we are going to implement in our plugin are the following:

+ `LoadPrologFileInConsoleAction`
+ `LoadPrologFileInConsoleWithTraceAction`
+ `RunPrologConsoleAction`

These actions will be available in the `Run` menu as well as the right-click context-menu of the file
in question.

Before looking at our actions we will take a look at the methods and classes needed by our actions.
We will call these our REPL helpers.

#### REPL Helpers

Our REPL helpers consist of 5 classes:

+ `PrologREPLUtils`
+ `PrologConsole`
+ `PrologConsoleProcessHandler`
+ `PrologConsoleRunner`

These classes are inspired by the classes from the [Haskell IDEA plugin](https://github.com/atsky/haskell-idea-plugin/), more specifically the [repl](https://github.com/atsky/haskell-idea-plugin/tree/master/plugin/src/org/jetbrains/haskell/repl) part of it.

##### PrologREPLUtils

The `PrologREPLUtils` class contains some static functions that we use in the other classes and to avoid repeating ourselves we have put them in here. We will not be going over them in detail since they are quite self-explanatory.

The functions are the following:

+ `PrologConsoleProcessHandler findRunningPrologConsole(Project project)`, function
  used to check if we already have a running instance of a PrologConsole and if
  we do return it so that we do not launch multiple instances.
+ `Module getModule(AnActionEvent e)`, function to get the current module from an action event.
+ `Module getModule(Project project)`, function to get the current module from a project.
+ `String getActionFile(AnActionEvent e)`, function that gets the path to the file we are currently
  viewing in our editor.

As well as an inner class `PrologConsoleMatcher`, which is used in the `findRunningPrologConsole` function to only get Prolog Consoles.

##### PrologConsole

This class represents a Prolog version of a basic IDEA LanguageConsole. We only extend the
[`LanguageConsoleImpl` class](https://upsource.jetbrains.com/idea-ce/file/idea-ce-dba03e40ff8fc26feb037493ca72af40c273dfa4/platform/lang-impl/src/com/intellij/execution/console/LanguageConsoleImpl.java)
provided by JetBrains.

This class sets up the view of the console window and actions that are available in the console window.
If you would want to customise the appearance of the console window, it would be in this class that
one would do it.

##### PrologConsoleProcessHandler

The `PrologConsoleProcessHandler` extends from the [`ColoredProcessHandler`](https://upsource.jetbrains.com/idea-ce/file/idea-ce-dba03e40ff8fc26feb037493ca72af40c273dfa4/platform/platform-impl/src/com/intellij/execution/process/KillableProcessHandler.java) class provided by JetBrains,
this class provides a process handler that supports ANSI coloring. The process handler is what controls
our console process, starting and killing it, and integrates with the actions associated with our
console view.

##### PrologConsoleRunner

The `PrologConsoleRunner` is the class responsible for setting up our Prolog console, it extends the `AbstractConsoleRunnerWithHistory` abstract class with a type parameter of `PrologConsole`.

The `AbstractConsoleRunnerWithHistory` class provides the basic functionality for running consoles.
It also launches an external process with line input and history handling.

We implement the abstract methods provided by the abstract class, these methods are:

+ `T createConsoleView()`, returns an instance of our `PrologConsole`.
+ `Process createProcess() throws ExecutionException`, creates a CommandLine and returns the
  process attached to the command line.
+ `OSProcessHandler createProcessHandler(final Process process)`, returns an instance of our
  `PrologConsoleProcessHandler`.
+ `ProcessBackedConsoleExecuteActionHandler createExecuteActionHandler()`, creates a new
  instance of a `ConsoleHistoryController` and returns a new `ProcessBackedConsoleExecuteActionHandler`.

We also create two static methods:

+ `PrologConsoleProcessHandler run(Module module, String sourceFilePath, boolean withTrace)`,
  this method creates an instance of `PrologConsoleRunner`, tries to initialise and run our console runner and return the process handler for the runner, depicted in listing \ref{code:run-consolerunner}.
+ `GeneralCommandLine createCommandLine(Module module, String workingDir, String sourceFilePath, boolean withTrace) throws CantRunException`,
  this method creates our commandline. We check that we have a PrologSDK configured, we then create a
  [`GeneralCommandLine`](https://upsource.jetbrains.com/idea-ce/file/idea-ce-dba03e40ff8fc26feb037493ca72af40c273dfa4/platform/platform-api/src/com/intellij/execution/configurations/GeneralCommandLine.java) and pass it our
  [`gprolog` commandline parameters](http://www.gprolog.org/manual/gprolog.html#sec8), which are:

  + `--entry-goal trace` if we are launching the console with a file in trace mode.
  + `--consult-file <path to file>`if we are launching the console with a file.

  If we are on a Windows system, we also have to set an [environment variable](http://www.gprolog.org/manual/gprolog.html#sec13) to make sure that
  `gprolog` is launched in text mode, the environment variable is `LINEDIT gui=no`. All of this is depicted in listing \ref{code:createcmdline-consolerunner}

\begin{listing}[h]
\inputminted[firstline=50, lastline=61, linenos, breaklines]{java}{code-source/ch/eif/intelliprolog/repl/PrologConsoleRunner.java}
\caption{run method}
\label{code:run-consolerunner}
\end{listing}

\begin{listing}[h]
\inputminted[firstline=87, lastline=111, linenos, breaklines]{java}{code-source/ch/eif/intelliprolog/repl/PrologConsoleRunner.java}
\caption{createCommandLine method}
\label{code:createcmdline-consolerunner}
\end{listing}

#### LoadPrologFileInConsoleAction

The `LoadPrologFileFileInConsoleAction` action launches our file that is currently open in the editor
within the `gprolog`REPL that is running inside a IDEA console window.

The action implements the two methods listed earlier, `actionPerformed` and `update`.

##### void update(AnActionEvent e)

In this method we check if a file is available to be run and if yes we set it to be visible with an
appropriate text, depicted in listing \ref{code:update-loadprolog}.

\begin{listing}[h]
\inputminted[firstline=46, lastline=56, linenos, breaklines]{java}{code-source/ch/eif/intelliprolog/repl/actions/LoadPrologFileInConsoleAction.java}
\caption{update method}
\label{code:update-loadprolog}
\end{listing}

##### void actionPerformed(AnActionEvent e)

In this method we first check if an editor and project are available, then retrieve the path to the
file we wish to run in the REPL, the whole method is depicted in listing \ref{code:actionperformed-loadprolog}.

The next step is making sure that the current state of the file is saved to the filesystem, using the
methods `commitAllDocuments()` and `saveAllDocuments()` from `PsiDocumentManager` and
`FileDocumentManager` respectively. This is necessary because IntelliJ IDEA uses a VirtualFileSystem,
that encapsulates most of the activities necessary for working with files. The reason they do this is
so that IDEA can add some extra features to the files, like tracking modifications and abstracting
the underlying implementation of the file system of the operating systems file system. More information
is available in the [IntelliJ Platform SDK DevGuide documentation](http://www.jetbrains.org/intellij/sdk/docs/basics/virtual_file_system.html)

After we are sure that the file has been correctly written to disk we can run our console using the
`run` method of `PrologConsoleRunner`, giving it the reference to our project, the path to the file and
if we want to run it with trace turned on, which in this case we don't so we pass it `false`.

\begin{listing}[h]
\inputminted[firstline=25, lastline=43, linenos, breaklines]{java}{code-source/ch/eif/intelliprolog/repl/actions/LoadPrologFileInConsoleAction.java}
\caption{actionPerformed method}
\label{code:actionperformed-loadprolog}
\end{listing}

#### LoadPrologFileInConsoleWithTraceAction

This action class is identical to `LoadPrologFileInConsoleAction` with one exception, we pass in `true`
when we run our console runner.

#### RunPrologConsoleAction

This action class is very similar to the previous actions but in this action we only check if a
module is available in the `update` method and in the `actionPerformed` method we simply run our
console runner, with a `null` as the path and `false` for the trace argument.

### Registering our actions

The actions need to be added to the actions section of the `plugin.xml` file,
the elements that need to be added are depicted in listing \ref{reg:actions}.

\begin{listing}[h]
\inputminted[breaklines, fontsize=\footnotesize,firstline=46, lastline=69]{xml}{code-resources/META-INF/plugin.xml}
\caption{Plugin actions registration}
\label{reg:actions}
\end{listing}
