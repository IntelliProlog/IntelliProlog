package ch.eif.intelliprolog.repl.runner;

import ch.eif.intelliprolog.repl.PrologConsoleExecuteActionHandler;
import ch.eif.intelliprolog.repl.PrologConsoleProcessHandler;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import static ch.eif.intelliprolog.repl.PrologREPLUtils.prepareCommand;

class PrologCommandLineState extends CommandLineState {

    private final
    @NotNull
    PrologRunConfiguration config;

    private final
    ExecutionEnvironment env;

    PrologCommandLineState(
            final @NotNull ExecutionEnvironment env,
            final @NotNull PrologRunConfiguration configuration) {
        super(env);
        this.env = env;
        this.config = configuration;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() {
        Project project = env.getProject();
        ProcessHandler processHandler = prepareCommand(project);
        ProcessTerminatedListener.attach(processHandler);
        return processHandler;
    }

    @Override
    @NotNull
    public ExecutionResult execute(@NotNull final Executor executor, @NotNull final ProgramRunner runner) {
        final PrologConsoleProcessHandler processHandler = (PrologConsoleProcessHandler) startProcess();
        LanguageConsoleImpl console = processHandler.getLanguageConsole();

        final String pathToSourceFile = config.getPathToSourceFile();

        String command = "consult('" + pathToSourceFile.substring(0, pathToSourceFile.length() - 3) + "').";

        console.setInputText(command);

        final Editor editor = console.getEditor();
        CaretModel caretModel = editor.getCaretModel();
        caretModel.moveToOffset(command.length());


        new PrologConsoleExecuteActionHandler(env.getProject(), processHandler).runExecuteAction(console, true);
        return new DefaultExecutionResult(console, processHandler);
        /*if (console != null) {
            console.attachToProcess(processHandler);
        }*/
        //return new DefaultExecutionResult(console, processHandler, createActions(console, processHandler, executor));
    }


}
