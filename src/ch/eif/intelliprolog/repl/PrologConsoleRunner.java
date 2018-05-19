package ch.eif.intelliprolog.repl;

import ch.eif.intelliprolog.sdk.PrologSdkType;
import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.console.ConsoleHistoryController;
import com.intellij.execution.console.ProcessBackedConsoleExecuteActionHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;

public class PrologConsoleRunner extends AbstractConsoleRunnerWithHistory<PrologConsole> {

    private static final String INTERPRETER_TITLE = "gprolog";

    private final String myType = "gprolog";
    private final Module module;
    private final Project project;
    private final String consoleTitle;
    private final String workingDir;
    private final String sourceFilePath;
    private final boolean withTrace;
    private GeneralCommandLine cmdLine;

    private PrologConsoleRunner(@NotNull Module module, @NotNull String consoleTitle, @Nullable String workingDir, @Nullable String sourceFilePath, boolean withTrace) {
        super(module.getProject(), consoleTitle, workingDir);

        this.module = module;
        this.project = module.getProject();
        this.consoleTitle = consoleTitle;
        this.workingDir = workingDir;
        this.sourceFilePath = sourceFilePath;
        this.withTrace = withTrace;
    }

    public static void run(@NotNull Module module, String sourceFilePath, boolean withTrace) {
        String srcRoot = ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();
        String path = srcRoot + File.separator + "src";
        PrologConsoleRunner runner = new PrologConsoleRunner(module, INTERPRETER_TITLE, path, sourceFilePath, withTrace);
        try {
            runner.initAndRun();
            runner.getProcessHandler();
        } catch (ExecutionException e) {
            ExecutionHelper.showErrors(module.getProject(), Arrays.<Exception>asList(e), INTERPRETER_TITLE, null);
        }
    }

    private static GeneralCommandLine createCommandLine(Module module, String workingDir, @Nullable String sourceFilePath, boolean withTrace) throws CantRunException {
        Sdk sdk = ProjectRootManager.getInstance(module.getProject()).getProjectSdk();
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

        if (sourceFilePath != null) {
            final ParametersList list = line.getParametersList();
            if (withTrace) {
                list.addParametersString("--entry-goal " + "trace");
            }
            list.addParametersString("--consult-file " + sourceFilePath);
        }

        return line;
    }

    @Override
    protected PrologConsole createConsoleView() {
        return new PrologConsole(project, consoleTitle);
    }

    @Nullable
    @Override
    protected Process createProcess() throws ExecutionException {
        cmdLine = createCommandLine(module, workingDir, this.sourceFilePath, this.withTrace);
        return cmdLine.createProcess();
    }

    @Override
    protected OSProcessHandler createProcessHandler(Process process) {
        return new PrologConsoleProcessHandler(process, cmdLine.getCommandLineString(), getConsoleView());
    }

    @NotNull
    @Override
    protected ProcessBackedConsoleExecuteActionHandler createExecuteActionHandler() {
        new ConsoleHistoryController(myType, "", getConsoleView()).install();
        return new ProcessBackedConsoleExecuteActionHandler(getProcessHandler(), false);
    }
}
