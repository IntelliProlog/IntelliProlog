package ch.eif.intelliprolog.repl.runner;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.PathUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

class PrologRunConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> {

    static final String PATH_TO_SOURCE_FILE = "sourceFile";
    static final String ENABLE_TRACE = "enableTrace";


    @Nullable
    private String pathToSourceFile = null;
    private boolean enableTrace = false;


    PrologRunConfiguration(String name, Project project, ConfigurationFactory factory) {
        super(name, new RunConfigurationModule(project), factory);
    }

    PrologRunConfiguration(Project project, ConfigurationFactory factory) {
        this("Prolog", project, factory);
    }

    @NotNull
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new PrologRunConfigurable(getProject());
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        PrologCommandLineState state = new PrologCommandLineState(environment, this);
        return state;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        JDOMExternalizer.write(element, PATH_TO_SOURCE_FILE, pathToSourceFile);
        JDOMExternalizer.write(element, ENABLE_TRACE, enableTrace);
        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
    }

    @Override
    public Collection<Module> getValidModules() {
        return Arrays.asList(ModuleManager.getInstance(getProject()).getModules());
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);
        pathToSourceFile = JDOMExternalizer.readString(element, PATH_TO_SOURCE_FILE);
        enableTrace = JDOMExternalizer.readBoolean(element, ENABLE_TRACE);
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
        setGeneratedName();
    }

    public boolean getEnableTrace() {
        return enableTrace;
    }

    public void setEnableTrace(boolean enableTrace) {
        this.enableTrace = enableTrace;
    }

}