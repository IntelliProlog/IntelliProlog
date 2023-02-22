package ch.heiafr.intelliprolog.repl.actions;

import ch.heiafr.intelliprolog.PrologIcons;
import ch.heiafr.intelliprolog.repl.PrologConsoleRunner;
import ch.heiafr.intelliprolog.repl.PrologREPLUtils;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public final class RunPrologConsoleAction extends AnAction implements DumbAware {

    public RunPrologConsoleAction() {
        getTemplatePresentation().setIcon(PrologIcons.FILE);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Module m = PrologREPLUtils.getModule(e);
        Presentation presentation = e.getPresentation();
        if (m == null) {
            presentation.setEnabled(false);
            return;
        }
        presentation.setEnabled(true);
        super.update(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Module module = PrologREPLUtils.getModule(e);
        PrologConsoleRunner.run(module, null, false);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
