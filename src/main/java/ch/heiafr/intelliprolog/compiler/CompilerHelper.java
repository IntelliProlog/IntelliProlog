package ch.heiafr.intelliprolog.compiler;

import ch.heiafr.intelliprolog.sdk.PrologSdkType;
import com.intellij.execution.CantRunException;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.SystemInfo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CompilerHelper {


    public static Path getInterpreterPath(Module m) {
        Sdk sdk = ProjectRootManager.getInstance(m.getProject()).getProjectSdk();
        if (sdk == null || !(sdk.getSdkType() instanceof PrologSdkType)) {
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(m);
            sdk = moduleRootManager.getSdk();
        }
        if (sdk == null || !(sdk.getSdkType() instanceof PrologSdkType) || sdk.getHomePath() == null) {
            System.out.println("sdk is null");
            return null;
        }

        return Paths.get(sdk.getHomePath());
    }

    public static Process getProcess(Path compiler, Path filePath) throws IOException, CantRunException {
        Process p = null;
        if (SystemInfo.isWindows) {
            p = Runtime.getRuntime().exec("cmd.exe /min");
            BufferedWriter writer = new BufferedWriter(new java.io.OutputStreamWriter(p.getOutputStream()));
            writer.write("set LINEDIT=gui=no"); //Prevent windows from opening a console
            writer.newLine();
            writer.write(compiler.toString()); //Launch the compiler
            writer.newLine();
            writer.write("consult('" + filePath.toString().replace("\\", "/") + "').");
            writer.newLine();
            writer.flush();
            writer.close();
        } else if (SystemInfo.isMac || SystemInfo.isLinux) {
            String[] env = new String[System.getenv().size()];
            int i = 0;
            for (String key : System.getenv().keySet()) {
                env[i++] = key + "=" + System.getenv().get(key);
                if("PATH".equals(key)){
                    env[i-1] += ":" + compiler.getParent().toString();
                }
            }

            p = Runtime.getRuntime().exec("/bin/bash", env); //Create a terminal instance
            BufferedWriter writer = new BufferedWriter(new java.io.OutputStreamWriter(p.getOutputStream()));
            writer.write(compiler.getFileName().toString()); //Launch the compiler
            writer.newLine();
            writer.write("consult('" + filePath.toString().replace(" ", "\\ ") + "').");
            writer.newLine();
            writer.flush();
            writer.close();
        }

        return p;
    }


    public static String[] autoSplit(String l) {
        String[] result = l.split(":");
        if (result.length == 3) {
            return result;
        }
        //Take only 3 last values
        String[] last = new String[3];
        last[0] = result[result.length - 3];
        last[1] = result[result.length - 2];
        last[2] = result[result.length - 1];
        return last;
    }
}
