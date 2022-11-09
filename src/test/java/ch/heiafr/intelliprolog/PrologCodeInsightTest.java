package ch.heiafr.intelliprolog;

import ch.heiafr.intelliprolog.psi.PrologFile;
import com.intellij.codeInsight.generation.actions.CommentByBlockCommentAction;
import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;

public class PrologCodeInsightTest extends LightJavaCodeInsightFixtureTestCase {


    /**
     * @return path to test data file directory relative to root of this module.
     */
    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }


    @Test
    public void testAnnotator() {
        // Set expected annotations
        myFixture.configureByFile("AnnotatorTestData.pl");
        myFixture.checkHighlighting(true, true, true, false);
    }


    @Test
    public void testLineCommenter() {
        myFixture.configureByText(PrologFileType.INSTANCE, "<caret> prolog_atom_name :- a.");
        CommentByLineCommentAction action = new CommentByLineCommentAction();
        action.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("% prolog_atom_name :- a.");
        action.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult(" prolog_atom_name :- a.");
    }

    @Test
    public void testMultilineCommenter() {
        myFixture.configureByText(PrologFileType.INSTANCE, "<caret> prolog_atom_name :- a.");
        CommentByBlockCommentAction action = new CommentByBlockCommentAction();
        action.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("/**/ prolog_atom_name :- a.");
        action.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult(" prolog_atom_name :- a.");
    }

}
