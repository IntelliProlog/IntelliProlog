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

public class PrologConsoleRunner extends AbstractConsoleRunnerWithHistory<PrologConsole> {

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
        if (filePath == null || module == null) {
            return;
        }

        String workingDir = new File(filePath).getParent();
        String fileName = new File(filePath).getName();
        String consoleName = fileName + " Prolog Console";

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
            throw new CantRunException("Could not create process");
        }
    }

    private Process createLinuxProcess(Path interpreterPath) throws IOException {
        if (isInExternalWindow) {
            Path prologFile = Path.of(filePath);
            String[] command = {"xterm", "-e", interpreterPath.toString(),
                    "--query-goal", "consult('" + prologFile + "')"};
            ProcessBuilder pb = new ProcessBuilder(command);
            this.command = String.join(" ", pb.command());
            pb.directory(prologFile.getParent().toFile());
            return pb.start();
        } else {
            //Same as macOS :)
            return createMacProcess(interpreterPath);
        }
    }

    private Process createMacProcess(Path interpreterPath) throws IOException {
        Process p;
        BufferedWriter writer;
        command = "/bin/bash";

        String[] env = new String[System.getenv().size()];
        int i = 0;
        for (String key : System.getenv().keySet()) {
            env[i++] = key + "=" + System.getenv().get(key);
            if ("PATH".equals(key)) {
                env[i - 1] += ":" + interpreterPath.getParent().toString();
            }
        }

        Path prologFile = Path.of(filePath);
        if (isInExternalWindow) {

            String appleScript = "tell app \"Terminal\" to do script "
                    + "\""
                    + interpreterPath.toString() + " --query-goal "
                    + "\\\""
                    + "consult('" + prologFile + "')"
                    + "\\\" "
                    + "\"";

            String[] command = {"osascript", "-e", appleScript};

            ProcessBuilder pb = new ProcessBuilder(command);
            this.command = String.join(" ", pb.command());
            pb.directory(prologFile.getParent().toFile());
            p = pb.start();

        } else {
            p = Runtime.getRuntime().exec("/bin/bash", env); //Create a terminal instance
            writer = new BufferedWriter(new java.io.OutputStreamWriter(p.getOutputStream()));
            writer.write("cd " + Path.of(getWorkingDir())); //Go to the working directory
            writer.newLine();
            writer.write(interpreterPath.getFileName().toString()); //Launch the compiler
            writer.newLine();
            writer.write("consult('" + prologFile.getFileName() + "').");
            writer.newLine();
            writer.flush();
        }
        return p;
    }

    private Process createWindowsProcess(Path interpreterPath) throws IOException {
        Process p;
        BufferedWriter writer;
        command = "cmd.exe /min";
        Path fileName = Path.of(filePath).getFileName();
        if (isInExternalWindow) {
            p = Runtime.getRuntime().exec(command);
            writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            writer.write("cd /d " + Path.of(getWorkingDir())); //Go to the working directory
            writer.newLine();
            writer.write("set LINEDIT=gui=yes"); // let windows open a console
            writer.newLine();
            String query = " --query-goal \"consult('" + fileName + "')\"";
            writer.write("start " + interpreterPath.toString() + query); //Launch the compiler
            writer.newLine();
        } else {
            p = Runtime.getRuntime().exec(command);
            writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            writer.write("set LINEDIT=gui=no"); //Prevent windows from opening a console
            writer.newLine();
            writer.write("cd /d " + Path.of(getWorkingDir())); //Go to the working directory
            writer.newLine();
            writer.write(interpreterPath.toString()); //Launch the compiler
            writer.newLine();
            writer.write("consult('" + fileName + "').");
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
