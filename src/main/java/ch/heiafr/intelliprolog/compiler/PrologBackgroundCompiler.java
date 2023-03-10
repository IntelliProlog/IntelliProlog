package ch.heiafr.intelliprolog.compiler;

import com.intellij.execution.CantRunException;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class PrologBackgroundCompiler implements Runnable {
    private final Path filePath;
    private final Module module;
    private static List<CompilerResult> lastCompileResult = new ArrayList<>();

    private static final HashMap<String, HighlightSeverity> severityMap = new HashMap<>() {
        {
            put("error", HighlightSeverity.ERROR);
            put("syntax_error", HighlightSeverity.ERROR);
            put("warning", HighlightSeverity.WEAK_WARNING);
            put("info", HighlightSeverity.INFORMATION);
        }
    };


    private static synchronized void sendFeedback(List<CompilerResult> result) {
        lastCompileResult = result;
    }

    public static synchronized List<CompilerResult> lastFeedBack() {
        return lastCompileResult;
    }

    public static List<CompilerResult> compileAndFeedBack(PsiFile file, Module module) throws InterruptedException {
        compile(file, module);
        return lastFeedBack();
    }

    private PrologBackgroundCompiler(Path filePath, Module module) {
        this.filePath = filePath;
        this.module = module;
    }

    public static void compile(PsiFile file, Module module) throws InterruptedException {
        if(module == null) {
            return;
        }
        Path filePath = Paths.get(file.getVirtualFile().getPath());
        Thread thread = new Thread(new PrologBackgroundCompiler(filePath, module));
        thread.start();
        thread.join();
    }

    @Override
    public void run() {

        try {
            Path path = CompilerHelper.getInterpreterPath(module);

            if(path == null) {
                //Null if no sdk is configured
                sendFeedback(new ArrayList<>());
                return;
            }

            Process p = CompilerHelper.getProcess(path, filePath);

            if(p == null) {
                return;
            }


            //Print output
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            ArrayList<String> lines = new ArrayList<>();

            //Pattern that matches the output of the compiler (compiled or failed)
            Pattern compileEnd = Pattern.compile("(.*compiled, [0-9]+ lines read)|(compilation failed)");

            while ((line = reader.readLine()) != null) {
                lines.add(line);

                if(line.contains("uncaught exception")){
                    //General error !
                    sendFeedback(new ArrayList<>());
                    p.destroy();
                    return; //Exit the thread
                }
                if (compileEnd.matcher(line).find()) {
                    break;
                }
            }


            p.destroy(); //We don't need the process anymore

            List<CompilerResult> result = new ArrayList<>();


            //Process received lines
            // => Extract errors or warnings
            // ====> Export them as List<CompilerResult>
            for (var l : lines) {
                for (var key : severityMap.keySet()) {
                    if (l.contains(key + ":") && l.trim().length() > 0) {
                        String[] parts = CompilerHelper.autoSplit(l);
                        //Structure of parts: [line, warning/error, message]
                        int lineNb = Integer.parseInt(parts[0].split("-")[0]);
                        String message = parts[1] + ":" + parts[2];
                        result.add(new CompilerResult(lineNb, message, severityMap.get(key)));
                        break;
                    }
                }
            }

            sendFeedback(result);

        } catch (IOException | CantRunException e) {
            e.printStackTrace();
        }
    }
}
