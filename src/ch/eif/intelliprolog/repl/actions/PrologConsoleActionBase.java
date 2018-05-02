package ch.eif.intelliprolog.repl.actions;

import ch.eif.intelliprolog.psi.PrologFile;
import ch.eif.intelliprolog.repl.PrologConsole;
import ch.eif.intelliprolog.repl.PrologConsoleExecuteActionHandler;
import ch.eif.intelliprolog.repl.PrologConsoleProcessHandler;
import ch.eif.intelliprolog.repl.PrologConsoleRunner;
import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.NotNullFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

abstract class PrologConsoleActionBase extends AnAction {

    private static final class PrologConsoleMatcher implements NotNullFunction<RunContentDescriptor, Boolean> {

        @NotNull
        public Boolean fun(RunContentDescriptor descriptor) {
            return descriptor != null && (descriptor.getExecutionConsole() instanceof PrologConsole);
        }
    }

    private static PrologConsoleProcessHandler findRunningPrologConsole(Project project) {
        Collection<RunContentDescriptor> descriptors = ExecutionHelper.findRunningConsole(project, new PrologConsoleMatcher());
        for (RunContentDescriptor descriptor: descriptors) {
            ProcessHandler handler = descriptor.getProcessHandler();
            if (handler instanceof PrologConsoleProcessHandler) {
                return (PrologConsoleProcessHandler) handler;
            }
        }
        return null;
    }

    protected static void executeCommand(Project project, String command) {
        PrologConsoleProcessHandler processHandler = findRunningPrologConsole(project);

        if (processHandler == null || processHandler.isProcessTerminated()) {
            Module module = RunPrologConsoleAction.getModule(project);
            processHandler = PrologConsoleRunner.run(module);
            if (processHandler == null) {
                return;
            }
        }

        LanguageConsoleImpl console = processHandler.getLanguageConsole();
        console.setInputText(command);

        Editor editor = console.getCurrentEditor();
        CaretModel caretModel = editor.getCaretModel();
        caretModel.moveToOffset(command.length());

        new PrologConsoleExecuteActionHandler(project, processHandler).runExecuteAction(console, true);
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();

        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (editor == null) {
            presentation.setEnabled(false);
            return;
        }
        Project project = editor.getProject();
        if (project == null) {
            presentation.setEnabled(false);
            return;
        }

        Document document = editor.getDocument();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile == null || !(psiFile instanceof PrologFile)) {
            presentation.setEnabled(false);
            return;
        }

        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null || virtualFile instanceof LightVirtualFile) {
            presentation.setEnabled(false);
            return;
        }

        PrologConsoleProcessHandler handler = findRunningPrologConsole(project);
        if (handler == null) {
            presentation.setEnabled(false);
            return;
        }
        LanguageConsoleImpl console = handler.getLanguageConsole();
        if (!(console instanceof PrologConsole)) {
            presentation.setEnabled(false);
            return;
        }

        presentation.setEnabled(true);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {

    }
}
