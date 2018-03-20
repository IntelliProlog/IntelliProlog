package ch.eif.intelliprolog.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.LocatableConfigurationBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.PathUtil;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PrologRunConfiguration extends LocatableConfigurationBase {

    @Nullable
    private String pathToSourceFile = null;

    @Nullable
    private String goalToRun = null;

    private boolean enableTrace = false;

    PrologRunConfiguration(Project project, PrologRunConfigurationType configurationType) {
        super(project, configurationType.getConfigurationFactories()[0], "Prolog");
    }

    @NotNull
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new PrologRunConfigurable(getProject());
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        final String filePath = getPathToSourceFile();
        if (StringUtil.isEmpty(filePath)) {
            throw new ExecutionException("Empty file path");
        }
        return new PrologCommandLineState(environment, getPathToSourceFile(), getGoalToRun(), getEnableTrace());
    }

    @Override
    public void checkConfiguration() {
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        XmlSerializer.serializeInto(this, element);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        XmlSerializer.deserializeInto(this, element);
    }

    @Nullable
    @Override
    public String suggestedName() {
        return pathToSourceFile == null ? "Prolog File" : PathUtil.getFileName(pathToSourceFile);
    }

    @Nullable
    public Module getModule() {
        try {
            return findModule();
        } catch (RuntimeConfigurationException e) {
            return null;
        }
    }

    @Nullable
    private Module findModule() throws RuntimeConfigurationException {
        if (pathToSourceFile == null) {
            return null;
        }
        final String fileUrl = VfsUtilCore.pathToUrl(com.intellij.openapi.util.io.FileUtil.toSystemIndependentName(pathToSourceFile));
        final VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(fileUrl);
        if (file == null) {
            throw new RuntimeConfigurationException("Can't find module for " + pathToSourceFile);
        }
        return ModuleUtilCore.findModuleForFile(file, getProject());
    }

    @Nullable
    public String getPathToSourceFile() {
        return pathToSourceFile;
    }

    public void setPathToSourceFile(@Nullable String pathToSourceFile) {
        this.pathToSourceFile = pathToSourceFile;
    }

    @Nullable
    public String getGoalToRun() {
        return goalToRun;
    }

    public void setGoalToRun(@Nullable String goalToRun) {
        this.goalToRun = goalToRun;
    }

    public boolean getEnableTrace() {
        return enableTrace;
    }

    public void setEnableTrace(boolean enableTrace) {
        this.enableTrace = enableTrace;
    }
}
