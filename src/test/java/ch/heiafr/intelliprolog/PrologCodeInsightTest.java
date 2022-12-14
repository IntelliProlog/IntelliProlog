package ch.heiafr.intelliprolog;

import com.intellij.codeInsight.generation.actions.CommentByBlockCommentAction;
import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction;
import com.intellij.openapi.editor.CaretAction;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.intellij.usageView.UsageInfo;
import org.junit.Test;

import java.util.Collection;

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

    @Test
    public void testFindUsagesCase1() {
        var usageInfos = myFixture.testFindUsagesUsingAction("FindUsageTestData1.pl", "FindUsageTestData_included.pl");
        assertEquals(3, usageInfos.size());
    }

    @Test
    public void testFindUsagesCase2() {
        var usageInfos = myFixture.testFindUsagesUsingAction("FindUsageTestData2.pl", "FindUsageTestData_included.pl");
        assertEquals(3, usageInfos.size());
    }

    @Test
    public void testFindUsagesCase3() {
        var usageInfos = myFixture.testFindUsagesUsingAction("FindUsageTestData3.pl", "FindUsageTestData_included.pl");
        assertEquals(3, usageInfos.size());
    }

    @Test
    public void testFindUsagesCase4() {
        var usageInfos = myFixture.testFindUsagesUsingAction("FindUsageTestData4.pl", "FindUsageTestData_included.pl");
        assertEquals(3, usageInfos.size());
    }

    public void testRenameRefactor() {
        myFixture.configureByFiles("RenameTestData1.pl", "RenameTestData2.pl", "RenameTestData3.pl");
        myFixture.renameElementAtCaretUsingHandler("fifo_new_renamed");
        myFixture.checkResultByFile("RenameTestData1.pl", "RenameTestData1Renamed.pl", false);
        myFixture.checkResultByFile("RenameTestData2.pl", "RenameTestData2Renamed.pl", false);
        myFixture.checkResultByFile("RenameTestData3.pl", "RenameTestData3Renamed.pl", false);
    }

}
