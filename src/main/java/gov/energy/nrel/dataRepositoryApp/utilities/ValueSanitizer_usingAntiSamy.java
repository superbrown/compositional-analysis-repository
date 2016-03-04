package gov.energy.nrel.dataRepositoryApp.utilities;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Mike Brown
 * Date: Jul 20, 2011
 */
public class ValueSanitizer_usingAntiSamy extends AbsValueSanitizer
{
    private static final String DEAFULT_ANTISAMY_POLICY_FILE_PATH = "antisamy-slashdot-1.3.xml";

    private AntiSamy antiSamy;
    private Policy antiSamyPolicy;

    public ValueSanitizer_usingAntiSamy(String antisamyPolicyFilePath) {

        init(antisamyPolicyFilePath);
    }

    public ValueSanitizer_usingAntiSamy() {

        init(DEAFULT_ANTISAMY_POLICY_FILE_PATH);
    }

    protected void init(String antisamyPolicyFilePath)
    {
        antiSamy = new AntiSamy();

        try
        {
            InputStream is = ValueSanitizer_usingAntiSamy.class.getClassLoader().getResourceAsStream(antisamyPolicyFilePath);
            antiSamyPolicy = Policy.getInstance(is);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sanitize value to prevent cross-site scripting attacks
     *
     * @param value
     * @return
     */
    public String sanitize(String value)
    {
        if (value == null)
        {
            return value;
        }

        try
        {
            String cleanHTML = antiSamy.scan(value, antiSamyPolicy).getCleanHTML();

            // back out these substitutions (they aren't desirable for our purposes)
            cleanHTML = cleanHTML.replaceAll("&amp;", "&");
            cleanHTML = cleanHTML.replaceAll("&quot;", "\"");

            return cleanHTML;
        }
        catch (PolicyException e)
        {
            throw new Error(e);
        }
        catch (ScanException e)
        {
            throw new Error(e);
        }
    }

    public boolean isSanitary(String value) {

        String sanitizedValue = sanitize(value);

        // we trim this because sanitize() appears to trim trailing spaces
        return (value.trim().equals(sanitizedValue.trim()));
    }

    public AntiSamy getAntiSamy()
    {
        return antiSamy;
    }

    public Policy getAntiSamyPolicy()
    {
        return antiSamyPolicy;
    }
}
