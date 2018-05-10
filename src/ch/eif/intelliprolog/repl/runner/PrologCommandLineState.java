package ch.eif.intelliprolog.repl.runner;

import ch.eif.intelliprolog.repl.PrologConsole;
import ch.eif.intelliprolog.repl.PrologConsoleProcessHandler;
import ch.eif.intelliprolog.repl.PrologConsoleRunner;
import ch.eif.intelliprolog.sdk.PrologSdkType;
import com.intellij.execution.*;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

class PrologCommandLineState extends CommandLineState {

    private static final String INTERPRETER_TITLE = "GNU-Prolog";
    private final
    @NotNull
    PrologRunConfiguration config;

    private final
    ExecutionEnvironment env;

    private GeneralCommandLine cmdLine;

    private PrologConsole console;

    private PrologConsoleRunner runner;

    PrologCommandLineState(
            final @NotNull ExecutionEnvironment env,
            final @NotNull PrologRunConfiguration configuration) {
        super(env);
        this.env = env;
        this.config = configuration;

        console = new PrologConsole(env.getProject(), "GNU-Prolog");
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        /*Project project = env.getProject();
        final Module module = config.getModule();
        String srcRoot = ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();
        String path = srcRoot + File.separator + "src";
        final GeneralCommandLine line = createCommandLine(project, path);
        final Process process = line.createProcess();
        final String commandLineString = line.getCommandLineString();
        return new PrologConsoleProcessHandler(process, commandLineString, console);*/

        final Module module = config.getModule();
        String srcRoot = ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();
        String path = srcRoot + File.separator + "src";
        runner = new PrologConsoleRunner(module, INTERPRETER_TITLE, path, config.getPathToSourceFile());
        runner.initAndRun();
        return runner.getProcessHandler();
    }

    @NotNull
    @Override
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {


        final ProcessHandler processHandler = startProcess();
        final PrologConsole consoleView = this.runner.getConsoleView();

        return new DefaultExecutionResult(consoleView, processHandler, createActions(consoleView, processHandler, executor));
    }

    private GeneralCommandLine createCommandLine(Project project, String workingDir) throws CantRunException {
        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        VirtualFile homePath;
        if (sdk == null || !(sdk.getSdkType() instanceof PrologSdkType) || sdk.getHomePath() == null) {
            throw new CantRunException("Invalid SDK Home path set. Please set your SDK path correctly.");
        } else {
            homePath = sdk.getHomeDirectory();
        }
        GeneralCommandLine line = new GeneralCommandLine();
        if (SystemInfo.isWindows) {
            line.withEnvironment("LINEDIT", "gui=no");
        }
        line.setExePath(new File(homePath.getPath()).getAbsolutePath());
        line.withWorkDirectory(workingDir);
        final ParametersList list = line.getParametersList();
        list.addParametersString("--consult-file " + this.config.getPathToSourceFile());
        return line;
    }

}
