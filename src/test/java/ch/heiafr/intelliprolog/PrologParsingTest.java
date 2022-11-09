package ch.heiafr.intelliprolog;

import ch.heiafr.intelliprolog.PrologParserDefinition;
import com.intellij.testFramework.ParsingTestCase;
import org.junit.Test;

public class PrologParsingTest extends ParsingTestCase {

  //The test file MUST BE named "ParsingTestData" and the extension MUST BE ".pl"
  public PrologParsingTest() {
    super("", "pl", new PrologParserDefinition());
  }

  @Test
  public void testParsingTestData() {
    doTest(true, true);
  }

  /**
   * @return path to test data file directory relative to root of this module.
   */
  @Override
  protected String getTestDataPath() {
    return "src/test/testData";
  }

  @Override
  protected boolean skipSpaces() {
    return false;
  }

  @Override
  protected boolean includeRanges() {
    return true;
  }

}
