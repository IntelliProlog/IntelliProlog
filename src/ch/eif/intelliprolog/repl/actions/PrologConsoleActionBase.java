package ch.eif.intelliprolog.repl.actions;

import ch.eif.intelliprolog.psi.PrologFile;
import ch.eif.intelliprolog.repl.PrologConsole;
import ch.eif.intelliprolog.repl.PrologConsoleProcessHandler;
import ch.eif.intelliprolog.repl.PrologREPLUtils;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;

abstract class PrologConsoleActionBase extends AnAction {

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

        PrologConsoleProcessHandler handler = PrologREPLUtils.findRunningPrologConsole(project);
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
