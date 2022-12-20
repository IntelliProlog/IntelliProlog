package ch.heiafr.intelliprolog.compiler;

import ch.heiafr.intelliprolog.sdk.PrologSdkType;
import com.intellij.execution.CantRunException;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.SystemInfo;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CompilerHelper {


    public static String commandFromFilePath(Path file, Module m){

        if(m == null){
            return null;
        }

        boolean isWindows = SystemInfo.isWindows;
        boolean isMac = SystemInfo.isMac;
        boolean isLinux = SystemInfo.isLinux;

        Path sdkPath = getInterpreterPath(m);
        if(sdkPath == null){
            return null;
        }

        String sdkString = sdkPath.toString();

        String goal = "consult('" + file.toString() + "')";

        if(isMac){
            //Escape all spaces in the path
            sdkString = sdkString.replaceAll(" ", "\\ ");
            goal = goal.replaceAll(" ", "\\ ");
        }


        String cmd = sdkString + " --query-goal " + goal;

        return cmd;
    }



    private static Path getInterpreterPath(Module m){
        Sdk sdk = ProjectRootManager.getInstance(m.getProject()).getProjectSdk();
        if (sdk == null || !(sdk.getSdkType() instanceof PrologSdkType)) {
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(m);
            sdk = moduleRootManager.getSdk();
        }
        if (sdk == null || !(sdk.getSdkType() instanceof PrologSdkType) || sdk.getHomePath() == null) {
            return null;
        }

        return Paths.get(sdk.getHomePath());
    }
}
