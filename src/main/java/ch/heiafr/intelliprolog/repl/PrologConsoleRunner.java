package ch.heiafr.intelliprolog.repl;

import ch.heiafr.intelliprolog.sdk.PrologSdkType;
import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.Platform;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.console.ConsoleHistoryController;
import com.intellij.execution.console.ConsoleRootType;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrologConsoleRunner extends AbstractConsoleRunnerWithHistory<PrologConsole> {

    private static final String INTERPRETER_TITLE = "gprolog";

    private final String myType = "gprolog";
    // using "--query-goal" instead of "--consult-file" just to
    // have a visible trace of the consult goal
    private static final String queryFlag = "--query-goal ";
    private final Module module;
    private final Project project;
    private final String consoleTitle;
    private final String workingDir;
    private final String sourceFilePath;
    private final boolean withTrace;
    private final boolean withSeparateShellWindow;
    private GeneralCommandLine cmdLine;
    private String macOsHackStr = "unknown command...";

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
        // String srcRoot = ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();
        String workingDirPath;
        if (sourceFilePath == null) {
            workingDirPath = ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();
        } else {
            String parentDirName = new File(sourceFilePath).getParent();
            workingDirPath = parentDirName; // srcRoot + File.separator + "src";
        }
        PrologConsoleRunner runner = new PrologConsoleRunner(module, INTERPRETER_TITLE, workingDirPath, sourceFilePath, inExternalTerminal);
        try {
            runner.initAndRun();
            runner.getProcessHandler();
        } catch (ExecutionException e) {
            ExecutionHelper.showErrors(module.getProject(), List.<Exception>of(e), INTERPRETER_TITLE, null);
        }
    }

    private static GeneralCommandLine createCommandLine(Module module, String workingDir,
                                                        @Nullable String sourceFilePath,
                                                        boolean withTrace,
                                                        boolean withSeparateShellWindow) throws CantRunException {
        Sdk sdk = ProjectRootManager.getInstance(module.getProject()).getProjectSdk();
        if (sdk == null || !(sdk.getSdkType() instanceof PrologSdkType)) {
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
            sdk = moduleRootManager.getSdk();
        }
        if (sdk == null || !(sdk.getSdkType() instanceof PrologSdkType) || sdk.getHomePath() == null) {
            throw new CantRunException("Invalid SDK Home path set. Please set your SDK path correctly. " +
                    (sdk == null ? "null" : (sdk.getSdkType() + " " + sdk.getHomePath())));
        }
        VirtualFile homePath = sdk.getHomeDirectory();
        String prologInterpreter = new File(homePath.getPath()).getAbsolutePath();
        GeneralCommandLine gCmdLine = new GeneralCommandLine() {
            // Override and don't escape anything (use command+prms as is)
            protected List<String> prepareCommandLine(@NotNull String command,
                                                      @NotNull List<String> parameters,
                                                      @NotNull Platform platform) {
                List<String> res = new ArrayList<>();
                res.add(command);
                res.addAll(parameters);
                return res;
            }
        };
        final ParametersList lineParameters = gCmdLine.getParametersList();
        boolean separateShellWindow = withSeparateShellWindow;
//        if (SystemInfo.isWindows)
//            separateShellWindow = true; // gprolog interpreter inside console seems buggy on Windows...

        String[] cmd;
        if (separateShellWindow) {
            cmd = openShellPossibleCommand(prologInterpreter, sourceFilePath);
        } else {  // inside IDEA console
            if (SystemInfo.isWindows) {
                gCmdLine.withEnvironment("LINEDIT", "gui=no");
            }
            cmd = new String[]{prologInterpreter, queryFlag, consultGoal(sourceFilePath)};
            gCmdLine.setExePath(prologInterpreter);
        }
        gCmdLine.withWorkDirectory(workingDir);
        gCmdLine.setExePath(cmd[0]);
        for (int i = 1; i < cmd.length; i++) {
            lineParameters.addParametersString(cmd[i]);
        }
//        String after = gCmdLine.getPreparedCommandLine(Platform.UNIX);
//        String asString = gCmdLine.getCommandLineString();
//        if (withTrace) {
//            lineParameters.addParametersString("--query-goal " + "trace");
//        }
        return gCmdLine;
    }

    private static String consultGoal(String sourceFile) {
        if (sourceFile == null) return "write(no_source_file)";
        return SystemInfo.isWindows ? "\"consult('" + sourceFile + "')\"" : "consult('" + sourceFile + "')";
    }

    private static String[] openShellPossibleCommand(String prologInterpreter,
                                                     String sourceFilePath) {
        String consultGoal = consultGoal(sourceFilePath);
        // distinguish 3 platforms: Win, Mac, Linux
        if (SystemInfo.isWindows) {
            String quotedPl = "\"" + prologInterpreter + "\"";
            String windowTitle = "\"I love gprolog\"";  // CAUTION: must contain a space (sic!)
            // inescapableQuote is considered only for non-windows platforms!!
            //String protectedQuotedPl = GeneralCommandLine.inescapableQuote(quotedPl);
            //String protectedWindowTitle = GeneralCommandLine.inescapableQuote(windowTitle);
            return new String[]{"cmd", "/C", "start", windowTitle, quotedPl, queryFlag, consultGoal};
        }
        if (SystemInfo.isMac) {
            // BUGGY: after hours of tedious attempts, I'm still unable to produce a valid
            // command line. One of the nearest tries was such that the command
            // as displayed in the console works when copy-pasted in a terminal.
            // Despite having followed the code, I don't understand what's going on
            // inside IntelliJ's mechanism (GeneralCommandLine). How on earth
            // can it be the case that a single double-quote " in a parameter
            // leads finally to two ones "" ?!?!
            String appleScript0 = " \" tell app \\\"Terminal\\\" \" to do script "
                    + "\""
                    + prologInterpreter + " " + queryFlag
                    + "\\\""
                    + consultGoal
                    + "\\\" "
                    + "\"";
            String appleScript = " \" tell app \\\"Terminal\\\" \" "
                    + " to do script "
                    + "\""
                    + prologInterpreter + " " + queryFlag
//                    +   "\\\""
                    + consultGoal
//                    +   "\\\" "
                    + "\"";
//            String[][] commands = new String[][] {
//                    {"osascript", "-e", appleScript},

            // osascript -e 'tell application "Terminal"'
            //           -e 'do script "/xyz/gprolog  --consult-file \"/xyz/bulle.pl\" "'
            //           -e 'end tell'

            // osascript -e 'tell application Terminal'
            //           -e 'do script "gprolog.exe  --consult-file  \"c.pl\" " '
            //           -e 'end tell'

            //cmd /C echo osascript tell app Terminal to do script "gprolog.exe --query-goal \"consult('c.pl')\" "

            String osascript = "osascript";
            String consultFlag = " --consult-file ";
            String sep = " -e ";
            //String appleScript1 = "'tell app \" \bTerminal \b\" '" ;
            String appleScript1 = " \" tell app \\\"Terminal\\\" \"";
            //String appleScript1 = "'tell app \"Terminal\" ' ";
            String appleScript2 = "'do script "
                    + "\""
                    + prologInterpreter + " " + consultFlag + " "
                    + "\\\""
                    + sourceFilePath
                    + "\\\" "
                    + "\" '";
            String appleScript3 = "'end tell'";

            String[][] commands = new String[][]{
                    {osascript, appleScript},
                    {osascript, sep, appleScript1, sep, appleScript2, sep, appleScript3},
                    // "open -a Terminal gprolog..." doesn't fully work:
                    //  there's an "; exit" added somewhere, and I don't know why
                    //{"open", "-a", "Terminal", prologInterpreter, "--args", queryFlag, consultGoal},
                    {"xterm", "-e", prologInterpreter, queryFlag, consultGoal}
                    // MacOS: open -a Terminal.app scriptfile : probably doesn't work here
                    // MacOS: open -b com.apple.terminal test.sh : probably doesn't work here
            };

            // Let's bet on the script...
            return commands[0]; //8.5.d:1, 8.5.e:0
        } else {
            String appleScript8 = " \" tell app \\\"Terminal\\\" \" "
                    + " to do script "
                    + "\""
                    + prologInterpreter + " " + queryFlag
//                    +   "\\\""
                    + consultGoal
//                    +   "\\\" "
                    + "\"";
            String appleScript9 = " \" tell app \\\"Terminal\\\" \" "
                    + " to do script "
                    + "\""
                    + prologInterpreter + " " + queryFlag
                    + "\\\""
                    + consultGoal
                    + "\\\" "
                    + "\"";

            String consultFlag = " --consult-file ";
            String sep = " -e ";
            //String appleScript1 = "'tell app \" \bTerminal \b\" '" ;
            String appleScript1 = " \" tell app \\\"Terminal\\\" \"";
            //String appleScript1 = "'tell app \"Terminal\" ' ";
            String appleScript2 = "'do script "
                    + "\""
                    + prologInterpreter + " " + consultFlag + " "
                    + "\\\""
                    + sourceFilePath
                    + "\\\" "
                    + "\" '";
            String appleScript3 = "'end tell'";

            String script1 = "'tell app \" \bTerminal \b\" '";
            String script2 = "' tell app \" \b/Applications/Utilities/Terminal.app \b\" '"; //"p \" Terminal \" ";
            String script3 = " aa\"\" \\ \\\" \" \" bb ";
            // Linux-based, some options... Not easy to choose the one that won't fail!
            String[][] commands = {
                    {"gnome-terminal", "-x", prologInterpreter, queryFlag, consultGoal},
                    // xterm was universal some years ago...
                    {"xterm", "-e", prologInterpreter, queryFlag, consultGoal},
                    {"konsole", "-e", prologInterpreter, queryFlag, consultGoal},
                    {"lxterminal", "-e", prologInterpreter, queryFlag, consultGoal, appleScript8, appleScript9}
                    // any other suggestions?
            };

            for (String[] c : commands) {
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

    private Process macOsHack(Module module, String workingDir,
                              @Nullable String sourceFilePath) throws CantRunException {
        Sdk sdk = ProjectRootManager.getInstance(module.getProject()).getProjectSdk();
        VirtualFile homePath;
        if (sdk == null || !(sdk.getSdkType() instanceof PrologSdkType) || sdk.getHomePath() == null) {
            throw new CantRunException("Invalid SDK Home path set. Please set your SDK path correctly. " +
                    (sdk == null ? "null" : (sdk.getSdkType() + " " + sdk.getHomePath())));
        }
        homePath = sdk.getHomeDirectory();
        String prologInterpreter = new File(homePath.getPath()).getAbsolutePath();

        String appleScript = "tell app \"Terminal\" to do script "
                + "\""
                + prologInterpreter + " --query-goal "
                + "\\\""
                + consultGoal(sourceFilePath)
                + "\\\" "
                + "\"";

        String[] command = {"osascript", "-e", appleScript};

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            this.macOsHackStr = String.join(" ", pb.command());
            pb.directory(new File(workingDir));
            Process p = pb.start();
            if (p != null) return p;
        } catch (IOException e) {
            throw new CantRunException("IOException problem in macOsHack...", e);
        }
        throw new CantRunException("problem in macOsHack...");
    }

    @Override
    protected PrologConsole createConsoleView() {
        return new PrologConsole(project, consoleTitle);
    }

    @Nullable
    @Override
    protected Process createProcess() throws ExecutionException {
        if (SystemInfo.isMac && this.withSeparateShellWindow) {
            return macOsHack(module, workingDir, this.sourceFilePath);
        }
        cmdLine = createCommandLine(module, workingDir, this.sourceFilePath,
                this.withTrace, this.withSeparateShellWindow);
        return cmdLine.createProcess();
    }

    @Override
    protected OSProcessHandler createProcessHandler(Process process) {
        String cmdLineStr = (cmdLine != null) ? cmdLine.getCommandLineString() : this.macOsHackStr;
        return new PrologConsoleProcessHandler(process, cmdLineStr, getConsoleView());
    }

    @NotNull
    @Override
    protected ProcessBackedConsoleExecuteActionHandler createExecuteActionHandler() {
        ConsoleRootType rootType = new ConsoleRootType(myType, myType) {
        };
        new ConsoleHistoryController(rootType, "", getConsoleView()).install();
        return new ProcessBackedConsoleExecuteActionHandler(getProcessHandler(), false);
    }
}
