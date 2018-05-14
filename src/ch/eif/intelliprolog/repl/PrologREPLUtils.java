package ch.eif.intelliprolog.repl;

import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
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

    public static ProcessHandler prepareCommand(Project project) {
        PrologConsoleProcessHandler processHandler = findRunningPrologConsole(project);

        if (processHandler == null || processHandler.isProcessTerminated()) {
            Module module = getModule(project);
            processHandler = PrologConsoleRunner.run(module);
            if (processHandler == null) {
                return null;
            }
        }

        return processHandler;
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

    private static final class PrologConsoleMatcher implements NotNullFunction<RunContentDescriptor, Boolean> {

        @NotNull
        public Boolean fun(RunContentDescriptor descriptor) {
            return descriptor != null && (descriptor.getExecutionConsole() instanceof PrologConsole);
        }
    }
}
