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
        Process p;
        BufferedWriter writer;

        if (SystemInfo.isWindows) {
            p = Runtime.getRuntime().exec("cmd.exe /min");
            writer = new BufferedWriter(new java.io.OutputStreamWriter(p.getOutputStream()));
            writer.write("set LINEDIT=gui=no"); //Prevent windows from opening a console
            writer.newLine();
            writer.write(compiler.toString()); //Launch the compiler
            writer.newLine();
            writer.flush(); //Flush the stream
        } else {
            p = Runtime.getRuntime().exec(compiler.toString());
            writer = new BufferedWriter(new java.io.OutputStreamWriter(p.getOutputStream()));
        }

        writer = new BufferedWriter(new java.io.OutputStreamWriter(p.getOutputStream()));
        String normalizedFilePath = filePath.toString().replace("\\", "/"); //Mandatory for windows
        String goal = "consult('" + normalizedFilePath + "').";
        writer.write(goal);
        writer.newLine();
        writer.flush();
        writer.close();
        return p;
    }


    public static String[] autoSplit(String l) {

        //TODO: Que les 3 derniers !

        if(SystemInfo.isWindows){
            //Windows => C:\ interferes with the regex pattern ":"
            // => Remove 3 first char of the line
            l = l.substring(3);
        }

        return l.split(":", 4);
    }
}
