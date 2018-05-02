package ch.eif.intelliprolog.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public class PrologConsoleExecuteActionHandler {

    private final Project project;
    private ProcessHandler processHandler;

    public PrologConsoleExecuteActionHandler(Project project, ProcessHandler processHandler) {
        this.project = project;
        this.processHandler = processHandler;
    }

    public void runExecuteAction(final LanguageConsoleImpl console, boolean executeImmediately) {
        if (executeImmediately) {
            execute(console);
            return;
        }

        Editor editor = console.getCurrentEditor();
        Document document = editor.getDocument();
        final CaretModel caretModel = editor.getCaretModel();
        final int offset = caretModel.getOffset();
        String text = document.getText();

        if (!"".equals(text.substring(offset).trim())) {
            String before = text.substring(0, offset);
            String after = text.substring(offset);
            final int indent = 0;
            String spaces = StringUtil.repeatSymbol(' ', indent);
            final String newText = before + "\n" + spaces + after;

            new WriteCommandAction(project) {
                @Override
                protected void run(@NotNull Result result) throws Throwable {
                    console.setInputText(newText);
                    caretModel.moveToOffset(offset + indent + 1);
                }
            }.execute();

            return;
        }
        execute(console);
    }

    private void execute(LanguageConsoleImpl console) {
        Document document = console.getCurrentEditor().getDocument();
        String text = document.getText();
        TextRange range = new TextRange(0, document.getTextLength());

        console.getCurrentEditor().getSelectionModel().setSelection(range.getStartOffset(), range.getEndOffset());
        console.setInputText("");

        processLine(text);
    }

    private void processLine(String line) {
        OutputStream os = processHandler.getProcessInput();
        if (os != null) {
            byte[] bytes = (line + "\n").getBytes();
            try {
                os.write(bytes);
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
