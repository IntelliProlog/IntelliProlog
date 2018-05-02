package ch.eif.intelliprolog.repl;

import ch.eif.intelliprolog.sdk.PrologSdkType;
import ch.eif.intelliprolog.util.PrologUtil;
import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.console.ConsoleHistoryController;
import com.intellij.execution.console.ProcessBackedConsoleExecuteActionHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;

public class PrologConsoleRunner extends AbstractConsoleRunnerWithHistory<PrologConsole> {

    static final String INTERPRETER_TITLE = "gprolog";

    private final String myType = "gprolog";
    private final Module module;
    private final Project project;
    private final String consoleTitle;
    private final String workingDir;
    private GeneralCommandLine cmdLine;

    private PrologConsoleRunner(@NotNull Module module, @NotNull String consoleTitle, @Nullable String workingDir) {
        super(module.getProject(), consoleTitle, workingDir);

        this.module = module;
        this.project = module.getProject();
        this.consoleTitle = consoleTitle;
        this.workingDir = workingDir;
    }

    public static PrologConsoleProcessHandler run(@NotNull Module module) {
        String srcRoot = ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();
        String path = srcRoot + File.separator + "src";
        PrologConsoleRunner runner = new PrologConsoleRunner(module, INTERPRETER_TITLE, path);
        try {
            runner.initAndRun();
            return (PrologConsoleProcessHandler) runner.getProcessHandler();
        } catch (ExecutionException e) {
            ExecutionHelper.showErrors(module.getProject(), Arrays.<Exception>asList(e), INTERPRETER_TITLE, null);
            return null;
        }
    }

    @Override
    protected PrologConsole createConsoleView() {
        return new PrologConsole(project, consoleTitle);
    }

    @Nullable
    @Override
    protected Process createProcess() throws ExecutionException {
        cmdLine = createCommandLine(module, workingDir);
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

    private static GeneralCommandLine createCommandLine(Module module, String workingDir) throws CantRunException {
        Sdk sdk = ProjectRootManager.getInstance(module.getProject()).getProjectSdk();
        VirtualFile homePath;
        if (sdk == null || !(sdk.getSdkType() instanceof PrologSdkType) || sdk.getHomePath() == null) {
            throw new CantRunException("Invalid SDK Home path set. Please set your SDK path correctly.");
        } else {
            homePath = sdk.getHomeDirectory();
        }
        GeneralCommandLine line = new GeneralCommandLine();
        //line.setExePath(PrologUtil.getCommandPath(homePath, "gprolog"));
        line.setExePath(new File(homePath.getPath()).getAbsolutePath());
        line.withWorkDirectory(workingDir);

        return line;
    }
}
