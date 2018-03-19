package ch.eif.intelliprolog;

import ch.eif.intelliprolog.ui.PrologSettingsEditorForm;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

public class PrologSettingsEditor extends SettingsEditor<PrologRunConfiguration> {

    private PrologRunConfiguration config;
    private Project project;
    private Mediator mediator;

    PrologSettingsEditor(PrologRunConfiguration configuration, Project project) {
        this.config = configuration;
        this.project = project;
        this.mediator = new Mediator();
        this.mediator.project = project;
    }

    @Override
    protected void resetEditorFrom(@NotNull PrologRunConfiguration s) {
        mediator.resetEditorFrom(s);
    }

    @Override
    protected void applyEditorTo(@NotNull PrologRunConfiguration s) {
        mediator.applyEditorTo(s);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return new PrologSettingsEditorForm(mediator).getPanel();
    }

    public class Mediator {
        public PrologSettingsEditorForm form;
        public Project project;

        void applyEditorTo(PrologRunConfiguration s) {
            s.setEnableTrace(form.getTraceCheckBox());
            s.setGoalToRun(form.getInitialGoalField());
            File selectedFile = form.getSelectedFile();
            s.setSourceFile(LocalFileSystem.getInstance().findFileByIoFile(selectedFile));
        }

        void resetEditorFrom(PrologRunConfiguration s) {
            s.setEnableTrace(false);
            s.setGoalToRun("");
            s.setSourceFile(FileEditorManager.getInstance(project).getOpenFiles()[0]);
        }
    }
}
