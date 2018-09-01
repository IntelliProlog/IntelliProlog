package ch.eif.intelliprolog.repl.actions;

import ch.eif.intelliprolog.PrologIcons;
import ch.eif.intelliprolog.repl.PrologConsoleRunner;
import ch.eif.intelliprolog.repl.PrologREPLUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;

import java.io.File;

public final class LoadPrologFileInExternalTerminal extends AnAction {

    public LoadPrologFileInExternalTerminal() {
        getTemplatePresentation().setIcon(PrologIcons.FILE);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        Project project = editor.getProject();
        if (project == null) {
            return;
        }

        String filePath = PrologREPLUtils.getActionFile(e);

        PsiDocumentManager.getInstance(project).commitAllDocuments();
        FileDocumentManager.getInstance().saveAllDocuments();

        PrologConsoleRunner.run(PrologREPLUtils.getModule(project), filePath, true);
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        String filePath = PrologREPLUtils.getActionFile(e);
        if (filePath == null) {
            presentation.setVisible(false);
        } else {
            File file = new File(filePath);
            presentation.setVisible(true);
            presentation.setText(String.format("Load \"%s\" in gProlog within an external terminal", file.getName()));
        }
    }
}
