package ch.heiafr.intelliprolog.editor;

import ch.heiafr.intelliprolog.compiler.CompilerResult;
import ch.heiafr.intelliprolog.compiler.PrologBackgroundCompiler;
import com.intellij.lang.annotation.AnnotationBuilder;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PrologExternalAnnotator extends ExternalAnnotator<List<CompilerResult>, List<CompilerResult>> {

    @Override
    public @Nullable List<CompilerResult> collectInformation(@NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {

        try {
            return PrologBackgroundCompiler.compileAndFeedBack(file, ModuleUtil.findModuleForFile(file));
        } catch (InterruptedException e) {
            e.printStackTrace();
            return PrologBackgroundCompiler.lastFeedBack();
        }
    }

    @Override
    public @Nullable List<CompilerResult> doAnnotate(List<CompilerResult> collectedInfo) {
        return collectedInfo; // no need to do anything, just return the result
    }

    @Override
    public void apply(@NotNull PsiFile file, List<CompilerResult> annotationResult, @NotNull AnnotationHolder holder) {

        for (CompilerResult res : annotationResult) {
            var range = lineToOffset(file, res.getLine());
            AnnotationBuilder ab = holder.newAnnotation(res.getSeverity(), res.getMessage());
            ab = ab.range(new TextRange(range.getFirst(), range.getSecond()));
            ab.create();
        }
    }


    private static Pair<Integer, Integer> lineToOffset(PsiFile file, int line) {

        int offset = 0;
        int lastOffset = 0;
        for (int i = 0; i < line; i++) {
            lastOffset = offset;
            offset = file.getText().indexOf('\n', offset) + 1;
        }

        if(offset < lastOffset){
            offset = file.getTextLength();
        }


        return new Pair<>(lastOffset, Math.max(offset - 1, lastOffset));
    }

}
