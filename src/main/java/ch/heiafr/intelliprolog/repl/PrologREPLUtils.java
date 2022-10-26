package ch.heiafr.intelliprolog.repl;

import ch.heiafr.intelliprolog.psi.PrologFile;
import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.NotNullFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PrologREPLUtils {

    public static PrologConsoleProcessHandler findRunningPrologConsole(Project project) {
        Collection<RunContentDescriptor> descriptors = ExecutionHelper.findRunningConsole(project, new PrologConsoleMatcher());
        for (RunContentDescriptor descriptor : descriptors) {
            ProcessHandler handler = descriptor.getProcessHandler();
            if (handler instanceof PrologConsoleProcessHandler) {
                return (PrologConsoleProcessHandler) handler;
            }
        }
        return null;
    }

    public static Module getModule(AnActionEvent e) {
        Module module = e.getData(LangDataKeys.MODULE);
        if (module == null) {
            Project project = e.getData(CommonDataKeys.PROJECT);
            return getModule(project);
        } else {
            return module;
        }
    }

    public static Module getModule(Project project) {
        if (project == null) {
            return null;
        }
        Module[] modules = ModuleManager.getInstance(project).getModules();
        if (modules.length > 0) {
            return modules[0];
        }
        return null;
    }

    public static String getActionFile(AnActionEvent e) {
        Module module = getModule(e);
        if (module == null) {
            return null;
        }
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null || editor.getProject() == null) {
            return null;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
        if (!(psiFile instanceof PrologFile)) {
            return null;
        }
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null || virtualFile instanceof LightVirtualFile) {
            return null;
        }
        return virtualFile.getPath();
    }

    private static final class PrologConsoleMatcher implements NotNullFunction<RunContentDescriptor, Boolean> {

        @NotNull
        public Boolean fun(RunContentDescriptor descriptor) {
            return descriptor != null && (descriptor.getExecutionConsole() instanceof PrologConsole);
        }
    }
}
