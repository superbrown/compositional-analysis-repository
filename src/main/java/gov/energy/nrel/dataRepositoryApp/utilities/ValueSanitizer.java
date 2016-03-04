//package gov.energy.nrel.dataRepositoryApp.utilities;
//
//import org.owasp.validator.html.AntiSamy;
//import org.owasp.validator.html.Policy;
//import org.owasp.validator.html.PolicyException;
//import org.owasp.validator.html.ScanException;
//
//import java.io.InputStream;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
///**
// * Created by IntelliJ IDEA.
// * User: Mike Brown
// * Date: Jul 20, 2011
// */
//public class ValueSanitizer
//{
//    private static final String DEAFULT_ANTISAMY_POLICY_FILE_PATH = "antisamy-slashdot-1.3.xml";
//
//    private AntiSamy antiSamy;
//    private Policy antiSamyPolicy;
//
//    public ValueSanitizer(String antisamyPolicyFilePath) {
//
//        init(antisamyPolicyFilePath);
//    }
//
//    public ValueSanitizer() {
//
//        init(DEAFULT_ANTISAMY_POLICY_FILE_PATH);
//    }
//
//    protected void init(String antisamyPolicyFilePath)
//    {
//        antiSamy = new AntiSamy();
//
//        try
//        {
//            InputStream is = ValueSanitizer.class.getClassLoader().getResourceAsStream(antisamyPolicyFilePath);
//            antiSamyPolicy = Policy.getInstance(is);
//        }
//        catch (Exception e)
//        {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public Object sanitize(Object value)
//    {
//        Object sanitizedValue;
//
//        if (value instanceof String)
//        {
//            sanitizedValue = sanitize((String) value);
//        }
//        else
//        {
//            sanitizedValue = value;
//        }
//
//        return sanitizedValue;
//    }
//
//    /**
//     * Sanitize value to prevent cross-site scripting attacks
//     *
//     * @param value
//     * @return
//     */
//    public String sanitize(String value)
//    {
//        if (value == null)
//        {
//            return value;
//        }
//
//        try
//        {
//            String cleanHTML = antiSamy.scan(value, antiSamyPolicy).getCleanHTML();
//
//            // back out these substitutions (they aren't desirable for our purposes)
//            cleanHTML = cleanHTML.replaceAll("&amp;", "&");
//            cleanHTML = cleanHTML.replaceAll("&quot;", "\"");
//
//            return cleanHTML;
//        }
//        catch (PolicyException e)
//        {
//            throw new Error(e);
//        }
//        catch (ScanException e)
//        {
//            throw new Error(e);
//        }
//    }
//
//    public boolean isSanitary(String value) {
//
//        String sanitizedValue = sanitize(value);
//
//        // we trim this because sanitize() appears to trim trailing spaces
//        return (value.trim().equals(sanitizedValue.trim()));
//    }
//
//    /**
//     * Sanitize value to prevent cross-site scripting attacks
//     *
//     * @param values
//     * @return
//     */
//    public List sanitize(List values)
//    {
//        if (values == null)
//        {
//            return values;
//        }
//
//        for (int i = 0; i < values.size(); i++)
//        {
//            // I am commenting this out because I don't want to risk the possibility that there
//            // might be recursive collections, putting the application into an endless loop.
////            Object sanitizedValue = sanitizeValue(values.get(i));
//
//            Object value = values.get(i);
//            Object sanitizedValue;
//
//            if (value instanceof String)
//            {
//                sanitizedValue = sanitize(value);
//            }
//            else
//            {
//                sanitizedValue = value;
//            }
//
//            values.set(i, sanitizedValue);
//        }
//
//        return values;
//    }
//
//    /**
//     * Sanitize value to prevent cross-site scripting attacks
//     *
//     * @param values
//     * @return
//     */
//    public Set sanitize(Set values)
//    {
//        if (values == null)
//        {
//            return values;
//        }
//
//        Set sanitizedValues = new HashSet();
//
//        for (Object value : values)
//        {
//            // I am commenting this out because I don't want to risk the possibility that there
//            // might be recursive collections, putting the application into an endless loop.
////            Object sanitizedValue = sanitizeValue(value);
////            sanitizedValues.add(sanitizedValue);
//
//            Object sanitizedValue;
//
//            if (value instanceof String)
//            {
//                sanitizedValue = sanitize(value);
//            }
//            else
//            {
//                sanitizedValue = value;
//            }
//
//            sanitizedValues.add(sanitizedValue);
//        }
//
//        values.clear();
//        values.addAll(sanitizedValues);
//
//        return values;
//    }
//
//    /**
//     * Sanitize value to prevent cross-site scripting attacks
//     *
//     * @param values
//     * @return
//     */
//    public Object[] sanitize(Object[] values)
//    {
//        if (values == null)
//        {
//            return values;
//        }
//
//        for (int i = 0; i < values.length; i++)
//        {
//            // I am commenting this out because I don't want to risk the possibility that there
//            // might be recursive collections, putting the application into an endless loop.
////            Object sanitizedValue = sanitizeValue(values[i]);
//
//            Object value = values[i];
//            Object sanitizedValue;
//
//            if (value instanceof String)
//            {
//                sanitizedValue = sanitize(value);
//            }
//            else
//            {
//                sanitizedValue = value;
//            }
//
//            values[i] = sanitizedValue;
//        }
//
//        return values;
//    }
//
//    public AntiSamy getAntiSamy()
//    {
//        return antiSamy;
//    }
//
//    public Policy getAntiSamyPolicy()
//    {
//        return antiSamyPolicy;
//    }
//}
