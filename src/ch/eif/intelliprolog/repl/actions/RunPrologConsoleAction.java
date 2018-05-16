package ch.eif.intelliprolog.repl.actions;

import ch.eif.intelliprolog.PrologIcons;
import ch.eif.intelliprolog.repl.PrologConsoleRunner;
import ch.eif.intelliprolog.repl.PrologREPLUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAware;

public final class RunPrologConsoleAction extends AnAction implements DumbAware {

    public RunPrologConsoleAction() {
        getTemplatePresentation().setIcon(PrologIcons.FILE);
    }

    @Override
    public void update(AnActionEvent e) {
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
    public void actionPerformed(AnActionEvent e) {
        Module module = PrologREPLUtils.getModule(e);
        PrologConsoleRunner.run(module, null, false);
    }
}
