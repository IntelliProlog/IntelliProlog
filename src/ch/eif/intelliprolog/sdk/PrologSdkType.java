package ch.eif.intelliprolog.sdk;

import com.intellij.openapi.projectRoots.*;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrologSdkType extends SdkType {

    private static final String NAME = "DefaultPrologSdkType";
    private final SdkType myDelegate;
    private Sdk mySdk;

    public PrologSdkType(@NotNull String name) {
        super(name);
        this.myDelegate = PrologSdkTypeFactory.create();
    }

    @Nullable
    @Override
    public String suggestHomePath() {
        return myDelegate.suggestHomePath();
    }

    @Override
    public boolean isValidSdkHome(String path) {
        return myDelegate.isValidSdkHome(path);
    }

    @Override
    public String suggestSdkName(String currentSdkName, String sdkHome) {
        return myDelegate.suggestSdkName(currentSdkName, sdkHome);
    }

    @Nullable
    @Override
    public String getVersionString(String sdkHome) {
        return myDelegate.getVersionString(sdkHome);
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
        return null;
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return myDelegate.getPresentableName();
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {
        myDelegate.saveAdditionalData(additionalData, additional);
    }

    public Sdk getSdk() {
        return mySdk;
    }
}
