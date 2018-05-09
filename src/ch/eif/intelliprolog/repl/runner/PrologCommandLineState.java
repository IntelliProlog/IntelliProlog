package ch.eif.intelliprolog.repl.runner;

import ch.eif.intelliprolog.repl.PrologConsole;
import ch.eif.intelliprolog.repl.PrologConsoleProcessHandler;
import ch.eif.intelliprolog.sdk.PrologSdkType;
import com.intellij.execution.*;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
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

    private final
    @NotNull
    PrologRunConfiguration config;

    private final
    ExecutionEnvironment env;

    private TextConsoleBuilder myConsole;

    private GeneralCommandLine cmdLine;

    private PrologConsole console;

    PrologCommandLineState(
            final @NotNull ExecutionEnvironment env,
            final @NotNull PrologRunConfiguration configuration) {
        super(env);
        this.env = env;
        this.config = configuration;

        myConsole = TextConsoleBuilderFactory.getInstance().createBuilder(env.getProject());
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        Project project = env.getProject();
        final Module module = config.getModule();
        String srcRoot = ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();
        String path = srcRoot + File.separator + "src";
        cmdLine = createCommandLine(project, path);
        console = new PrologConsole(project, "GNU-Prolog");
        ProcessHandler processHandler = new PrologConsoleProcessHandler(cmdLine.createProcess(), cmdLine.getCommandLineString(), console);
        ProcessTerminatedListener.attach(processHandler);
        return processHandler;
    }

    @Override
    @NotNull
    public ExecutionResult execute(@NotNull final Executor executor, @NotNull final ProgramRunner runner) throws ExecutionException {
        final ProcessHandler processHandler = startProcess();

        final String pathToSourceFile = config.getPathToSourceFile();

        String command = "consult('" + pathToSourceFile.substring(0, pathToSourceFile.length() - 3) + "').";

        console.setInputText(command);

        console.attachToProcess(processHandler);
        final Editor editor = console.getEditor();
        CaretModel caretModel = editor.getCaretModel();
        caretModel.moveToOffset(command.length());

        execute(processHandler, console);
        return new DefaultExecutionResult(console, processHandler, createActions(console, processHandler, executor));
    }

    private void execute(ProcessHandler handler, LanguageConsoleImpl console) {
        Document document = console.getCurrentEditor().getDocument();
        String text = document.getText();
        TextRange range = new TextRange(0, document.getTextLength());

        console.getCurrentEditor().getSelectionModel().setSelection(range.getStartOffset(), range.getEndOffset());
        console.setInputText("");

        processLine(handler, text);
    }

    private void processLine(ProcessHandler processHandler, String line) {
        OutputStream os = processHandler.getProcessInput();
        if (os != null) {
            byte[] bytes = (line + "\n").getBytes();
            try {
                os.write(bytes);
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        return line;
    }

}
