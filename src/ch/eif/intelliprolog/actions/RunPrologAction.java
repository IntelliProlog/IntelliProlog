package ch.eif.intelliprolog.actions;

import ch.eif.intelliprolog.PrologFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

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

            String result = runMacOSProlog();

            Messages.showMessageDialog(project, result, "Result of File", Messages.getInformationIcon());
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

    public String runMacOSProlog() {

        StringBuffer sb = new StringBuffer();

        try {
            rt = Runtime.getRuntime();
            proc = rt.exec("gprolog --query-goal append([a,b],[c,d],X)");

            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            PrintWriter writer = new PrintWriter(proc.getOutputStream());

            writer.print("halt.");
            writer.flush();
            writer.close();

            String line = null;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                sb.append(line);
                sb.append("\n");
            }

            int exitVal = proc.waitFor();
            System.out.println("Exited with error code " + exitVal);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
