package ch.heiafr.intelliprolog.repl;

import ch.heiafr.intelliprolog.sdk.PrologSdkType;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PrologConsoleRunner extends AbstractConsoleRunnerWithHistory<PrologConsole> {

    private static final String INTERPRETER_TITLE = "gprolog";

    private final String myType = "gprolog";
    private final Module module;
    private final Project project;
    private final String consoleTitle;
    private final String workingDir;
    private final String sourceFilePath;
    private final boolean withTrace;
    private final boolean withSeparateShellWindow;
    private GeneralCommandLine cmdLine;

    private PrologConsoleRunner(@NotNull Module module, @NotNull String consoleTitle, @Nullable String workingDir, @Nullable String sourceFilePath, boolean inExternalTerminal) {
        super(module.getProject(), consoleTitle, workingDir);

        this.module = module;
        this.project = module.getProject();
        this.consoleTitle = consoleTitle;
        this.workingDir = workingDir;
        this.sourceFilePath = sourceFilePath;
        this.withTrace = false;
        this.withSeparateShellWindow = inExternalTerminal;
    }

    public static void run(@NotNull Module module, String sourceFilePath, boolean inExternalTerminal) {
        String srcRoot = ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();
        String path = srcRoot + File.separator + "src";
        PrologConsoleRunner runner = new PrologConsoleRunner(module, INTERPRETER_TITLE, path, sourceFilePath, inExternalTerminal);
        try {
            runner.initAndRun();
            runner.getProcessHandler();
        } catch (ExecutionException e) {
            ExecutionHelper.showErrors(module.getProject(), Arrays.<Exception>asList(e), INTERPRETER_TITLE, null);
        }
    }

    private static GeneralCommandLine createCommandLine(Module module, String workingDir,
                                                        @Nullable String sourceFilePath,
                                                        boolean withTrace,
                                                        boolean withSeparateShellWindow) throws CantRunException {
        Sdk sdk = ProjectRootManager.getInstance(module.getProject()).getProjectSdk();
        VirtualFile homePath;
        if (sdk == null || !(sdk.getSdkType() instanceof PrologSdkType) || sdk.getHomePath() == null) {
            throw new CantRunException("Invalid SDK Home path set. Please set your SDK path correctly. " +
                    (sdk==null ?  "null" :  (sdk.getSdkType()+" "+sdk.getHomePath()) ));
        }
        homePath = sdk.getHomeDirectory();
        String prologInterpreter = new File(homePath.getPath()).getAbsolutePath();
        GeneralCommandLine line = new GeneralCommandLine();
        final ParametersList lineParameters = line.getParametersList();
        boolean separateShellWindow = withSeparateShellWindow;
//        if (SystemInfo.isWindows)
//            separateShellWindow = true; // gprolog interpreter inside console seems buggy on Windows...

        if (separateShellWindow) {
            if (SystemInfo.isWindows) {
                line.setExePath("cmd");
                lineParameters.addParametersString("/C");
                lineParameters.addParametersString("start");
                lineParameters.addParametersString(prologInterpreter);
            } else {
                String[] cmd = openShellPossibleCommand(prologInterpreter, consultGoal(sourceFilePath));
                line.setExePath(cmd[0]);
                for(int i=1; i<cmd.length; i++) {
                    lineParameters.addParametersString(cmd[i]);
                }
            }
        } else {  // inside IDEA console
            if (SystemInfo.isWindows) {
                line.withEnvironment("LINEDIT", "gui=no");
            }
            line.setExePath(prologInterpreter);
        }
        line.withWorkDirectory(workingDir);

        if (sourceFilePath != null) {
            // using "--query-goal" instead of "--consult-file" just to
            // have a visible trace of the consult goal
            lineParameters.addParametersString("--query-goal " + consultGoal(sourceFilePath));
//            lineParameters.addParametersString("--consult-file " + sourceFilePath);
            if (withTrace) {
//                lineParameters.addParametersString("--entry-goal " + "trace");
                lineParameters.addParametersString("--query-goal " + "trace");
            }
        }

        return line;
    }

    private static String consultGoal(String sourceFile) {
        return "consult('"+sourceFile+"')";
    }

    private static String[] openShellPossibleCommand(String prologInterpreter,
                                                     String consultGoal) {
        String queryFlag = "--query-goal ";
        // Some options... Not easy to choose the one that won't fail!
        String[][] commands = {
                {"gnome-terminal", "-x", prologInterpreter, queryFlag, consultGoal},
                // xterm was universal some years ago...
                {"xterm", "-e", prologInterpreter, queryFlag, consultGoal},
                {"konsole", "-e", prologInterpreter, queryFlag, consultGoal},
                {"lxterminal", "-e", prologInterpreter, queryFlag, consultGoal}
                // any other suggestions?
        };
        if (SystemInfo.isMac) {
            String appleScript = "tell app \"Terminal\" to do script "
                    + "\""
                    +   prologInterpreter +" --query-goal "
                    +   "\\\""
                    +     consultGoal
                    +   "\\\" "
                    + "\"";

            commands = new String[][] {
                    {"osascript", "-e", appleScript},
                    {"xterm", "-e", prologInterpreter, queryFlag, consultGoal}
                    // MacOS: open -a Terminal.app scriptfile : probably doesn't work here
                    // MacOS: open -b com.apple.terminal test.sh : probably doesn't work here
            };
            // Let's bet on the script...
            return commands[0];
        } else {
            for(String[] c:commands) {
                String exec = c[0];
                boolean existsInPath = Stream.of(System.getenv("PATH")
                        .split(Pattern.quote(File.pathSeparator)))
                        .map(Paths::get)
                        .anyMatch(path -> Files.exists(path.resolve(exec)));
                if (existsInPath) return c;
            }
            // Let's bet on the old xterm...
            return commands[1];
        }
    }

    @Override
    protected PrologConsole createConsoleView() {
        return new PrologConsole(project, consoleTitle);
    }

    @Nullable
    @Override
    protected Process createProcess() throws ExecutionException {
        cmdLine = createCommandLine(module, workingDir, this.sourceFilePath,
                this.withTrace, this.withSeparateShellWindow);
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
