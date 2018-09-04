package ch.heiafr.intelliprolog.util;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

class PrologUtil {

    public static String getCommandPath(VirtualFile gprologHome, String executable) {
        if (gprologHome == null)
            return null;
        VirtualFile virBin = gprologHome.findChild("bin");
        if (virBin == null)
            return null;
        return new File(virBin.getPath(), executable).getAbsolutePath();
    }
}
