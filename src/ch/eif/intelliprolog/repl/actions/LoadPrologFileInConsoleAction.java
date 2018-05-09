package ch.eif.intelliprolog.repl.actions;

import ch.eif.intelliprolog.PrologIcons;
import ch.eif.intelliprolog.psi.PrologFile;
import ch.eif.intelliprolog.repl.PrologConsoleExecuteActionHandler;
import ch.eif.intelliprolog.repl.PrologConsoleProcessHandler;
import ch.eif.intelliprolog.repl.PrologREPLUtils;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;

import java.io.File;

public final class LoadPrologFileInConsoleAction extends PrologConsoleActionBase {

    public LoadPrologFileInConsoleAction() {
        getTemplatePresentation().setIcon(PrologIcons.FILE);
    }

    private static String getActionFile(AnActionEvent e) {
        Module module = PrologREPLUtils.getModule(e);
        if (module == null) {
            return null;
        }
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null || editor.getProject() == null) {
            return null;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
        if (psiFile == null || !(psiFile instanceof PrologFile)) {
            return null;
        }
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null || virtualFile instanceof LightVirtualFile) {
            return null;
        }
        return virtualFile.getPath();
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

        Document document = editor.getDocument();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile == null || !(psiFile instanceof PrologFile)) {
            return;
        }

        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null) {
            return;
        }

        String filePath = virtualFile.getPath();

        PsiDocumentManager.getInstance(project).commitAllDocuments();
        FileDocumentManager.getInstance().saveAllDocuments();

        String command = "consult('" + filePath.substring(0, filePath.length() - 3) + "').";
        PrologConsoleProcessHandler processHandler = (PrologConsoleProcessHandler) PrologREPLUtils.prepareCommand(project);

        LanguageConsoleImpl console = processHandler.getLanguageConsole();
        console.setInputText(command);
        CaretModel caretModel = editor.getCaretModel();
        caretModel.moveToOffset(command.length());

        new PrologConsoleExecuteActionHandler(project, processHandler).runExecuteAction(console, true);
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        String filePath = getActionFile(e);
        if (filePath == null) {
            presentation.setVisible(false);
        } else {
            File file = new File(filePath);
            presentation.setVisible(true);
            presentation.setText(String.format("Load \"%s\" in GNU Prolog Interpreter", file.getName()));
        }
    }
}
