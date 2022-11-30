package ch.heiafr.intelliprolog.reference;

import ch.heiafr.intelliprolog.PrologFileType;
import ch.heiafr.intelliprolog.psi.PrologAtom;
import ch.heiafr.intelliprolog.psi.PrologCompound;
import ch.heiafr.intelliprolog.psi.PrologCompoundName;
import ch.heiafr.intelliprolog.psi.PrologSentence;
import ch.heiafr.intelliprolog.psi.impl.PrologPsiUtil;
import com.google.common.collect.Lists;
import com.intellij.find.findUsages.CustomUsageSearcher;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageInfo2UsageAdapter;
import com.intellij.usages.UsageInfoToUsageConverter;
import com.intellij.util.Processor;
import io.grpc.util.GracefulSwitchLoadBalancer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PrologCustomUsageSearcher extends CustomUsageSearcher {
    @Override
    public void processElementUsages(@NotNull PsiElement element, @NotNull Processor<? super Usage> processor, @NotNull FindUsagesOptions options) {

        Application app = ApplicationManager.getApplication(); // get the application

        /*
            NOTES
            -------------
            The search in the files is done in a separate thread because it can take a long time.
            We MUST use "app.runReadAction" to access the PSI tree. Otherwise, we will get an exception !
         */

        app.runReadAction(new FindPrologCompoundNameRunnable(element, processor, options)); //Start the search in a separate thread
    }


    /**
     * Runnable that searches for all usages of a PrologCompoundName
     */
    private static class FindPrologCompoundNameRunnable implements Runnable {
        private final PsiElement element;
        private final Processor<? super Usage> processor;
        private final FindUsagesOptions options;

        /**
         * Constructor
         *
         * @param elt       the element to search for
         * @param processor the processor to use
         * @param options   the options to use
         */
        public FindPrologCompoundNameRunnable(PsiElement elt, Processor<? super Usage> processor, FindUsagesOptions options) {
            element = elt;
            this.processor = processor;
            this.options = options;
        }

        @Override
        public void run() {


            //Find every file in the selected scope
            var filenames = FilenameIndex.getAllFilesByExt(element.getProject(), PrologFileType.INSTANCE.getDefaultExtension());

            GlobalSearchScope scope = findScope();

            //For each file, check if it contains the element
            for (VirtualFile file : filenames) {
                //Open file as a PsiFile
                PsiFile psiFile = PsiManager.getInstance(element.getProject()).findFile(file);
                if (psiFile == null) {
                    continue; //If the file is not a PsiFile, skip it
                }

                //Find sentence to compute arity
                PrologSentence sentence = PsiTreeUtil.getParentOfType(element, PrologSentence.class);

                int eltArity = ReferenceHelper.getArityFromClicked(element);

                if (eltArity > 0) {
                    //Arity > 0 => compound
                    //Find every PrologCompoundName in the file
                    PsiTreeUtil.findChildrenOfType(psiFile, PrologCompound.class).stream()
                            //If the compoundName is the same as the element
                            .filter(compound -> Objects.equals(ReferenceHelper.compoundNameFromCompound(compound), element.getText()))

                            //If the arity is the same as the element
                            .filter(compound -> eltArity == ReferenceHelper.getArity(compound))

                            //Create a Usage from the compoundName
                            .map(compound -> new UsageInfo2UsageAdapter(new UsageInfo(compound)))
                            .forEach(processor::process); //Process the Usage
                } else {
                    // No arity => Atom
                    PsiTreeUtil.findChildrenOfType(psiFile, PrologAtom.class).stream()
                            //If the compoundName is the same as the element
                            .filter(atom -> atom.getText().equals(element.getText()))
                            //Create a Usage from the compoundName
                            .map(atom -> new UsageInfo2UsageAdapter(new UsageInfo(atom)))
                            .forEach(processor::process); //Process the Usage
                }


            }
        }


        private GlobalSearchScope findScope() {
            Project p = element.getProject();
            Module m = ModuleUtil.findModuleForFile(element.getContainingFile().getVirtualFile(), p);
            if (m == null) {
                return GlobalSearchScope.allScope(p);
            }
            GlobalSearchScope[] scopes = new GlobalSearchScope[]{
                    GlobalSearchScope.allScope(element.getProject()),
                    GlobalSearchScope.projectScope(element.getProject()),
                    GlobalSearchScope.moduleScope(m),
                    GlobalSearchScope.fileScope(element.getContainingFile()),
                    GlobalSearchScope.EMPTY_SCOPE
            };

            //ALL OTHER SCOPES ARE NOT SUPPORTED YET
            for (GlobalSearchScope scope : scopes) {
                if (scope.toString().equals(options.searchScope.toString())) {
                    return scope;
                }
            }
            //If the scope is not supported, return the default scope
            return GlobalSearchScope.allScope(p);
        }
    }
}
