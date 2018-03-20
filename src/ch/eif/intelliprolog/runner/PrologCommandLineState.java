package ch.eif.intelliprolog.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PrologCommandLineState extends CommandLineState {

    private final
    @NotNull
    String pathToSourceFile;

    private final
    @Nullable
    String goalToRun;

    private final
    boolean isTrace;

    PrologCommandLineState(
            final @NotNull ExecutionEnvironment env,
            final @NotNull String sourceFile,
            final @Nullable String goalToRun,
            final boolean isTrace) {
        super(env);
        this.pathToSourceFile = sourceFile;
        this.goalToRun = goalToRun;
        this.isTrace = isTrace;
        final TextConsoleBuilder builder = getConsoleBuilder();
        if (builder instanceof TextConsoleBuilderImpl) {
            ((TextConsoleBuilderImpl) builder).setUsePredefinedMessageFilter(false);
        }
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        final Sdk sdk = ProjectRootManager.getInstance(getEnvironment().getProject()).getProjectSdk();
        if (sdk != null) {
            commandLine.setExePath(sdk.getHomePath());
        }
        if (isTrace) {
            commandLine.addParameter("--entry-goal \"trace\"");
        }
        if (goalToRun != null && !goalToRun.isEmpty()) {
            commandLine.addParameter("--query-goal \"" + goalToRun + "\"");
        }
        commandLine.addParameter("--consult-file " + pathToSourceFile);
        final OSProcessHandler processHandler = new OSProcessHandler(commandLine.createProcess(), commandLine.getCommandLineString());
        ProcessTerminatedListener.attach(processHandler, getEnvironment().getProject());
        return processHandler;
    }
}
