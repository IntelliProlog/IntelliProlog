package ch.eif.intelliprolog.sdk;

import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

class PrologMacOsSdkType extends SdkType {

    private static final String DEFAULT_NAME = "PrologMacOsSdk";
    private static final String INTERPRETER = "gprolog";
    private static final String STANDARD_LOCATION = "/usr/local/bin";
    private static final String LATEST_VERSION = "1.4.4";
    private static final PrologMacOsSdkType INSTANCE = new PrologMacOsSdkType();

    private PrologMacOsSdkType() {
        super(DEFAULT_NAME);
    }

    public PrologMacOsSdkType(@NotNull String name) {
        super(name);
    }

    static PrologMacOsSdkType getInstance() {
        return INSTANCE;
    }

    @Nullable
    private static VirtualFile root(File file, final String relativePath) {
        file = new File(file.getAbsolutePath() + File.separator + relativePath.replace('/', File.separatorChar));
        if (!file.exists() || !file.isDirectory()) return null;
        final String path = file.getAbsolutePath().replace(File.separatorChar, '/');
        return LocalFileSystem.getInstance().findFileByPath(path);
    }

    @Nullable
    @Override
    public String suggestHomePath() {
        final File[] applications = new File(STANDARD_LOCATION).listFiles();
        if (applications != null) {
            for (File application : applications) {
                if (application.getName().toLowerCase().contains(INTERPRETER)) {
                    return application.getAbsolutePath();
                }
            }
        }
        return STANDARD_LOCATION;
    }

    @Override
    public boolean isValidSdkHome(String path) {
        return new File(path).getName().toLowerCase().contains(INTERPRETER);
    }

    @Override
    public String suggestSdkName(String currentSdkName, String sdkHome) {
        return INTERPRETER;
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
        return null;
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return INTERPRETER;
    }

    @Nullable
    @Override
    public String getVersionString(String sdkHome) {
        return LATEST_VERSION;
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {

    }

    @Override
    public void setupSdkPaths(@NotNull Sdk sdk) {
        SdkModificator modificator = sdk.getSdkModificator();
        modificator.setVersionString(getVersionString(STANDARD_LOCATION));
        modificator.setHomePath(STANDARD_LOCATION);
        modificator.setName(INTERPRETER);
        modificator.commitChanges();
    }
}
