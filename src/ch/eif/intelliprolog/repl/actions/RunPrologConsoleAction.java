package ch.eif.intelliprolog.repl.actions;

import ch.eif.intelliprolog.PrologIcons;
import ch.eif.intelliprolog.repl.PrologConsoleRunner;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;

public final class RunPrologConsoleAction extends AnAction implements DumbAware {

    public RunPrologConsoleAction() {
        getTemplatePresentation().setIcon(PrologIcons.FILE);
    }

    @Override
    public void update(AnActionEvent e) {
        Module m = getModule(e);
        Presentation presentation = e.getPresentation();
        if (m == null) {
            presentation.setEnabled(false);
            return;
        }
        presentation.setEnabled(true);
        super.update(e);
    }

    static Module getModule(AnActionEvent e) {
        Module module = e.getData(LangDataKeys.MODULE);
        if (module == null) {
            Project project = e.getData(CommonDataKeys.PROJECT);
            return getModule(project);
        } else {
            return module;
        }
    }

    static Module getModule(Project project) {
        if (project == null) {
            return null;
        }
        Module[] modules = ModuleManager.getInstance(project).getModules();
        if (modules.length > 0) {
            return modules[0];
        }
        return null;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Module module = getModule(e);
        PrologConsoleRunner.run(module);
    }
}
