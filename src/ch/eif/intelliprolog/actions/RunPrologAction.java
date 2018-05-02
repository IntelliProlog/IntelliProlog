package ch.eif.intelliprolog.actions;

import ch.eif.intelliprolog.PrologFileType;
import ch.eif.intelliprolog.actions.run.RunCommandLineState;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;


public class RunPrologAction extends AnAction {

    private Runtime rt;
    private Process proc;
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Project project = e.getProject();
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (project != null && psiFile != null && editor != null) {

            int offset = editor.getCaretModel().getOffset();
            PsiElement elementAt = psiFile.findElementAt(offset);

            Messages.showMessageDialog(project, psiFile.toString(), "Selected Element", Messages.getInformationIcon());

        }
    }

    @Override
    public void update(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if ((psiFile == null || editor == null) && !(psiFile.getFileType() instanceof PrologFileType)) {
            e.getPresentation().setEnabled(false);
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        if (elementAt == null) {
            e.getPresentation().setEnabled(false);
        }
    }

    public String runMacOSProlog(ExecutionEnvironment env, PsiFile sourceFile) {

        RunCommandLineState command = new RunCommandLineState(env, "prolog", sourceFile.getVirtualFile().getPath(), null, false);
        return "";
    }
}
