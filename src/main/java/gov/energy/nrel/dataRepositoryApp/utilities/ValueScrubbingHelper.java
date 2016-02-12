package gov.energy.nrel.dataRepositoryApp.utilities;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Mike Brown
 * Date: Jul 20, 2011
 */
public class ValueScrubbingHelper
{
    private static final String DEAFULT_ANTISAMY_POLICY_FILE_PATH = "antisamy-slashdot-1.3.xml";

    private AntiSamy antiSamy;
    private Policy antiSamyPolicy;

    public ValueScrubbingHelper(String antisamyPolicyFilePath) {

        init(antisamyPolicyFilePath);
    }

    public ValueScrubbingHelper() {

        init(DEAFULT_ANTISAMY_POLICY_FILE_PATH);
    }

    protected void init(String antisamyPolicyFilePath)
    {
        antiSamy = new AntiSamy();

        try
        {
            InputStream is = ValueScrubbingHelper.class.getClassLoader().getResourceAsStream(antisamyPolicyFilePath);
            antiSamyPolicy = Policy.getInstance(is);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public Object scrubValue(Object value)
    {
        Object scrubbedValue;

        if (value instanceof String)
        {
            scrubbedValue = scrubValue((String)value);
        }
        else
        {
            scrubbedValue = value;
        }

        return scrubbedValue;
    }

    /**
     * Scrub value to prevent cross-site scripting attacks
     *
     * @param value
     * @return
     */
    public String scrubValue(String value)
    {
        if (value == null)
        {
            return value;
        }

        try
        {
            return antiSamy.scan(value, antiSamyPolicy).getCleanHTML();
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

    /**
     * Scrub value to prevent cross-site scripting attacks
     *
     * @param values
     * @return
     */
    public List scrubValue(List values)
    {
        if (values == null)
        {
            return values;
        }

        for (int i = 0; i < values.size(); i++)
        {
            // I am commenting this out because I don't want to risk the possibility that there
            // might be recursive collections, putting the application into an endless loop.
//            Object scrubbedValue = scrubValue(values.get(i));

            Object value = values.get(i);
            Object scrubbedValue;

            if (value instanceof String)
            {
                scrubbedValue = scrubValue(value);
            }
            else
            {
                scrubbedValue = value;
            }

            values.set(i, scrubbedValue);
        }

        return values;
    }

    /**
     * Scrub value to prevent cross-site scripting attacks
     *
     * @param values
     * @return
     */
    public Set scrubValue(Set values)
    {
        if (values == null)
        {
            return values;
        }

        Set scrubbedValues = new HashSet();

        for (Object value : values)
        {
            // I am commenting this out because I don't want to risk the possibility that there
            // might be recursive collections, putting the application into an endless loop.
//            Object scrubbedValue = scrubValue(value);
//            scrubbedValues.add(scrubbedValue);

            Object scrubbedValue;

            if (value instanceof String)
            {
                scrubbedValue = scrubValue(value);
            }
            else
            {
                scrubbedValue = value;
            }

            scrubbedValues.add(scrubbedValue);
        }

        values.clear();
        values.addAll(scrubbedValues);

        return values;
    }

    /**
     * Scrub value to prevent cross-site scripting attacks
     *
     * @param values
     * @return
     */
    public Object[] scrubValue(Object[] values)
    {
        if (values == null)
        {
            return values;
        }

        for (int i = 0; i < values.length; i++)
        {
            // I am commenting this out because I don't want to risk the possibility that there
            // might be recursive collections, putting the application into an endless loop.
//            Object scrubbedValue = scrubValue(values[i]);

            Object value = values[i];
            Object scrubbedValue;

            if (value instanceof String)
            {
                scrubbedValue = scrubValue(value);
            }
            else
            {
                scrubbedValue = value;
            }

            values[i] = scrubbedValue;
        }

        return values;
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
