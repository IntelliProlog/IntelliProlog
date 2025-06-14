package ch.heiafr.intelliprolog.repl;

import ch.heiafr.intelliprolog.sdk.PrologSdkType;
import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.console.ConsoleHistoryController;
import com.intellij.execution.console.ConsoleRootType;
import com.intellij.execution.console.ProcessBackedConsoleExecuteActionHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import java.util.regex.Pattern;
import java.nio.file.Files;

public class PrologConsoleRunner extends AbstractConsoleRunnerWithHistory<PrologConsole> {
    private static final String QUERY_FLAG = "--query-goal ";

    private final boolean isInExternalWindow;
    private final String filePath;
    private final Module module;
    private String command = "";

    public PrologConsoleRunner(@NotNull Module module, @NotNull String consoleTitle, @Nullable String workingDir, @Nullable String sourceFilePath, boolean externalWindow) {
        super(module.getProject(), consoleTitle, workingDir);
        this.module = module;
        isInExternalWindow = externalWindow;
        filePath = sourceFilePath;
    }

    public static void run(Module module, String filePath, boolean isInExternalWindow) {
        String workingDir;
        String consoleName = " Prolog Console";
        if(filePath == null) {  // ie the "Run Prolog REPL" menu command
            workingDir = ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();
        } else {
            workingDir = new File(filePath).getParent();
            String fileName = new File(filePath).getName();
            consoleName = fileName + consoleName;
        }

        PrologConsoleRunner runner = new PrologConsoleRunner(module, consoleName, workingDir, filePath, isInExternalWindow);
        try {
            runner.initAndRun();
            // runner.getProcessHandler();  // unused ?
        } catch (ExecutionException e) {
            ExecutionHelper.showErrors(module.getProject(), List.<Exception>of(e), consoleName, null);
        }
    }

    @Override
    protected PrologConsole createConsoleView() {
        return new PrologConsole(getProject(), "Prolog Console");
    }

    @Override
    protected @Nullable Process createProcess() throws ExecutionException {
        Path interpreterPath = getInterpreterPath(this.module);
        if (interpreterPath == null) {
            throw new CantRunException("No interpreter found. First, set an interpreter in the module settings.");
        }
        try {
            if (SystemInfo.isWindows) {
                return createWindowsProcess(interpreterPath);
            } else if (SystemInfo.isMac) {
                return createMacProcess(interpreterPath);
            } else if (SystemInfo.isLinux) {
                return createLinuxProcess(interpreterPath);
            } else {
                throw new CantRunException("Unsupported OS");
            }
        } catch (IOException e) {
            throw new CantRunException("Could not create process", e);
        }
    }

    private Process createLinuxProcess(Path interpreterPath) throws IOException {
        if (isInExternalWindow) {
            String queryGoal = "nl"; // stupid goal when filePath==null (ie "run REPL")
            Path prologFile = null;
            if(filePath != null) {
                prologFile = Path.of(filePath);
                queryGoal = "consult('" + prologFile + "')";
            }
            String[] command = assembleCommandForLinux(interpreterPath.toString(), queryGoal);
            ProcessBuilder pb = new ProcessBuilder(command);
            this.command = String.join(" ", pb.command());
            if(prologFile != null)
                pb.directory(prologFile.getParent().toFile());
            return pb.start();
        } else {
            // Same as macOS :)
            return createMacProcess(interpreterPath);
        }
    }

    private String[] assembleCommandForLinux(String prologInterpreter,
                                             String consultGoal) {
        String[][] commands = {
                {"x-terminal-emulator", "-e", prologInterpreter, QUERY_FLAG, consultGoal},
                {"gnome-terminal",      "-x", prologInterpreter, QUERY_FLAG, consultGoal},
                // xterm was universal some years ago...
                {"xterm",               "-e", prologInterpreter, QUERY_FLAG, consultGoal},
                {"konsole",             "-e", prologInterpreter, QUERY_FLAG, consultGoal},
                {"lxterminal",          "-e", prologInterpreter, QUERY_FLAG, consultGoal}
                // any other suggestions?
        };

        for(String[] c:commands) {
            String exec = c[0];
            boolean existsInPath = Stream.of(System.getenv("PATH")
                            .split(Pattern.quote(File.pathSeparator)))
                    .map(Paths::get)
                    .anyMatch(path -> Files.exists(path.resolve(exec)));
            if (existsInPath) return c;
        }
        // Let's bet on the old xterm...
        return commands[2];
    }

    private Process createMacProcess(Path interpreterPath) throws IOException {
        Process p;
        BufferedWriter writer;

        String[] env = new String[System.getenv().size()];
        int i = 0;
        for (String key : System.getenv().keySet()) {
            env[i++] = key + "=" + System.getenv().get(key);
            if ("PATH".equals(key)) {
                env[i - 1] += ":" + interpreterPath.getParent().toString();
            }
        }
        Path prologFile = null;
        String queryGoal = "nl"; // stupid goal when filePath==null (ie "run REPL")
        String changeDirectoryCommand = "";
        if(filePath != null) {
            prologFile = Path.of(filePath);
            queryGoal = "consult('" + prologFile + "')";
            String preferredWorkingDirectory = prologFile.getParent().toFile().toString();
            changeDirectoryCommand = "cd " + backslashDoubleQuoted(preferredWorkingDirectory) + " && ";
        }
        if (isInExternalWindow) {
            String gprologCommand = backslashDoubleQuoted(interpreterPath.toString())
                    + " --query-goal "
                    + backslashDoubleQuoted(queryGoal);
            String appleScript = "tell app \"Terminal\" to activate do script "
                    + doubleQuoted(changeDirectoryCommand + gprologCommand);
            // tell app "Terminal" to activate do script "cd \" … \" && \" … gprolog \" --query-goal \"consult(' … ')\""

            String[] myCommand = {"osascript", "-e", appleScript};
            ProcessBuilder pb = new ProcessBuilder(myCommand);
            this.command = String.join(" ", pb.command());
            // System.out.println("mac-command:" + this.command);  // remove once it's validated
            p = pb.start();
        } else {
            command = "/bin/bash";
            String[] myCommand = {command};
            p = Runtime.getRuntime().exec(myCommand, env); //Create a terminal instance
            writer = new BufferedWriter(new java.io.OutputStreamWriter(p.getOutputStream()));
            writer.write("cd " + Path.of(getWorkingDir())); //Go to the working directory
            writer.newLine();
            writer.write(interpreterPath.getFileName().toString()); //Launch the compiler
            writer.newLine();
            if(prologFile != null) {
                writer.write("consult('" + prologFile.getFileName() + "').");
            }
            writer.newLine();
            writer.flush();
        }
        return p;
    }

    static String doubleQuoted(String s) {
        return "\"" + s + "\"";
    }

    static String backslashDoubleQuoted(String s) {
        return "\\\"" + s + "\\\"";
    }

    private Process createWindowsProcess(Path interpreterPath) throws IOException {
        Process p;
        BufferedWriter writer;
        String[] myCommand = {"cmd.exe", "/min"};
        command = String.join(" ", myCommand);
        // String command1 = "powershell -WindowStyle Minimized";  // does not help with LINEDIT problem...
        Path prologFile = null;
        String queryGoal = "nl"; // stupid goal when filePath==null (ie "run REPL")
        if(filePath != null) {
            prologFile = Path.of(filePath).getFileName();
            queryGoal = "consult('" + prologFile + "')";
        }
        if (isInExternalWindow) {
            p = Runtime.getRuntime().exec(myCommand);
            writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            writer.write("cd /d " + Path.of(getWorkingDir())); //Go to the working directory
            writer.newLine();
            writer.write("set LINEDIT=gui=yes"); // let windows open a console
            writer.newLine();
            String query = " --query-goal \"" + queryGoal + "\"";
            writer.write("start " + interpreterPath.toString() + query); //Launch the compiler
            writer.newLine();
        } else {
            p = Runtime.getRuntime().exec(myCommand);
            writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            //writer.write("set LINEDIT=gui=no"); //Prevent windows from opening a console
            writer.write("set LINEDIT=no"); //Prevent windows from opening a console
            writer.newLine();
            writer.write("cd /d " + Path.of(getWorkingDir())); //Go to the working directory
            writer.newLine();
            writer.write(interpreterPath.toString()); //Launch the compiler
            writer.newLine();
            if(prologFile != null) {
                writer.write("consult('" + prologFile + "').");
            }
            writer.newLine();
        }

        writer.flush();

        // DO NOT CLOSE THE WRITER, IT WILL CLOSE THE PROCESS !

        return p;
    }

    @Override
    protected OSProcessHandler createProcessHandler(Process process) {
        return new PrologConsoleProcessHandler(process, command, getConsoleView());
    }

    @Override
    protected @NotNull ProcessBackedConsoleExecuteActionHandler createExecuteActionHandler() {
        ConsoleRootType rootType = new ConsoleRootType("gprolog", "gprolog") {
        };
        new ConsoleHistoryController(rootType, "", getConsoleView()).install();
        return new ProcessBackedConsoleExecuteActionHandler(getProcessHandler(), false);
    }


    /*********************
     *  STATIC HELPERS   *
     ********************/

    private static Path getInterpreterPath(Module m) {
        Sdk sdk = ProjectRootManager.getInstance(m.getProject()).getProjectSdk();
        if (sdk == null || !(sdk.getSdkType() instanceof PrologSdkType)) {
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(m);
            sdk = moduleRootManager.getSdk();
        }
        if (sdk == null || !(sdk.getSdkType() instanceof PrologSdkType) || sdk.getHomePath() == null) {
            return null;
        }
        return Paths.get(sdk.getHomePath());
    }
}
