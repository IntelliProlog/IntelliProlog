package ch.eif.intelliprolog;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.pom.Navigatable;

public class TestAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        Navigatable navigatable = anActionEvent.getData(CommonDataKeys.NAVIGATABLE);
        if (project != null && navigatable != null) {
            Messages.showMessageDialog(project, navigatable.toString(), "Selected Element", Messages.getInformationIcon());
        }
    }
}
