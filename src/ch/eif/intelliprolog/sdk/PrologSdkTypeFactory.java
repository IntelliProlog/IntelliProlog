package ch.eif.intelliprolog.sdk;

import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.util.SystemInfo;

class PrologSdkTypeFactory {
    static SdkType create() {
        if (SystemInfo.isMac) {
            return PrologMacOsSdkType.getInstance();
        }
        throw new UnsupportedOperationException("Unsupported platform");
    }
}
