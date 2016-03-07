package test.gov.energy.nrel.dataRepositoryApp.utilities;

import gov.energy.nrel.dataRepositoryApp.utilities.valueSanitizer.ValueSanitizer_usingOwaspJavaHtmlSanitizer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;


public class ValueSanitizer_usingOwaspJavaHtmlSanitizerTest {

    private ValueSanitizer_usingOwaspJavaHtmlSanitizer sanitizer;

    @Before
    public void before() throws Exception {

        this.sanitizer = new ValueSanitizer_usingOwaspJavaHtmlSanitizer();

    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: init()
     */
    @Test
    public void testInit() throws Exception {

    }

    /**
     * Method: sanitize(String value)
     */
    @Test
    public void testSanitize() throws Exception {

        String stringToSanitize = "\"&'+<=>@`";

        String sanitizedString = sanitizer.sanitize(stringToSanitize);

        assertTrue(sanitizedString.equals(stringToSanitize));

        stringToSanitize =
                "<script>alert(\"Hello World 1\");</script>" +
                "Test " +
                "<script>alert(\"Hello World 2\");</script>" +
                "Case" +
                "<script>alert(\"Hello World 3\");</script>";

        sanitizedString = sanitizer.sanitize(stringToSanitize);

        assertTrue(sanitizedString.equals("Test Case"));
    }
}
