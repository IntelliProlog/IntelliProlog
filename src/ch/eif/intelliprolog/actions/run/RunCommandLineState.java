package ch.eif.intelliprolog.actions.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RunCommandLineState extends CommandLineState {

    private final
    @NotNull
    String pathToSourceFile;

    private final
    @NotNull
    String executable;

    private final
    @Nullable
    String goalToRun;

    private final
    boolean isTrace;

    public RunCommandLineState(
            final @NotNull ExecutionEnvironment env,
            final @NotNull String executable,
            final @NotNull String sourceFile,
            final @Nullable String goalToRun,
            final boolean isTrace) {
        super(env);
        this.pathToSourceFile = sourceFile;
        this.goalToRun = goalToRun;
        this.isTrace = isTrace;
        this.executable = executable;
        final TextConsoleBuilder builder = getConsoleBuilder();
        if (builder instanceof TextConsoleBuilderImpl) {
            ((TextConsoleBuilderImpl) builder).setUsePredefinedMessageFilter(false);
        }
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath(this.executable);
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

