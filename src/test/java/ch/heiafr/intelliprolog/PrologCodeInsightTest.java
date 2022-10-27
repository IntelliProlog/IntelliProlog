package ch.heiafr.intelliprolog;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;

public class PrologCodeInsightTest extends LightJavaCodeInsightFixtureTestCase {

    @Test
    public void testAnnotator(){
        myFixture.configureByText("test.pl", "a.\ntestCase <info descr=\"Binary operator\">:-</info> a.");
        myFixture.checkHighlighting(false, true, false, false);
    }

}
