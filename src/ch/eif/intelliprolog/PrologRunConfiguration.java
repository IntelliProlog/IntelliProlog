package ch.eif.intelliprolog;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrologRunConfiguration extends RunConfigurationBase {

    private Project project;
    private VirtualFile sourceFile;
    private String goalToRun;
    private boolean enableTrace;

    PrologRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
        this.project = project;
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new PrologSettingsEditor(this, project);
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return null;
    }

    public void setSourceFile(VirtualFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void setGoalToRun(String goalToRun) {
        this.goalToRun = goalToRun;
    }

    public void setEnableTrace(boolean enableTrace) {
        this.enableTrace = enableTrace;
    }
}
