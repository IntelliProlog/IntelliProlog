package ch.eif.intelliprolog.runner;

import ch.eif.intelliprolog.PrologFileType;
import com.intellij.ide.util.TreeFileChooser;
import com.intellij.ide.util.TreeFileChooserFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

class PrologRunConfigurable extends SettingsEditor<PrologRunConfiguration> {
    private JPanel myMainPanel;
    private JCheckBox isTrace;
    private TextFieldWithBrowseButton myFileField;
    private RawCommandLineEditor myGoalToRun;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    public PrologRunConfigurable(final Project project) {
        myFileField.getButton().addActionListener(e -> {
            TreeFileChooser fileChooser = TreeFileChooserFactory.getInstance(project).createFileChooser(
                    "Choose Prolog file to consult",
                    null,
                    PrologFileType.INSTANCE,
                    file -> true
            );

            fileChooser.showDialog();
            final PsiFile selectedFile = fileChooser.getSelectedFile();
            final VirtualFile virtualFile = selectedFile == null ? null : selectedFile.getVirtualFile();
            if (virtualFile != null) {
                final String path = FileUtil.toSystemDependentName(virtualFile.getPath());
                myFileField.setText(path);
            }
        });
    }

    @Override
    protected void resetEditorFrom(@NotNull PrologRunConfiguration s) {
        myFileField.setText(FileUtil.toSystemIndependentName(StringUtil.notNullize(s.getPathToSourceFile())));
        myGoalToRun.setText(StringUtil.notNullize(s.getGoalToRun()));
        isTrace.setSelected(s.getEnableTrace());
    }

    @Override
    protected void applyEditorTo(@NotNull PrologRunConfiguration s) {
        s.setPathToSourceFile(StringUtil.nullize(FileUtil.toSystemIndependentName(myFileField.getText()), true));
        s.setGoalToRun(StringUtil.nullize(myGoalToRun.getText(), true));
        s.setEnableTrace(isTrace.isSelected());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myMainPanel;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        myMainPanel = new JPanel();
        myMainPanel.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, 15));
        myMainPanel.setMaximumSize(new Dimension(500, 400));
        myMainPanel.setMinimumSize(new Dimension(20, 20));
        final JLabel label1 = new JLabel();
        label1.setText("Prolog File:");
        myMainPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        myFileField = new TextFieldWithBrowseButton();
        myMainPanel.add(myFileField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Goal To Run:");
        myMainPanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        myGoalToRun = new RawCommandLineEditor();
        myMainPanel.add(myGoalToRun, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        isTrace = new JCheckBox();
        isTrace.setText("Should Run With Trace?");
        myMainPanel.add(isTrace, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        myMainPanel.add(spacer1, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return myMainPanel;
    }
}