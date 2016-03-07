package gov.energy.nrel.dataRepositoryApp.utilities.valueSanitizer;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import java.util.regex.Pattern;

import static org.owasp.html.SanitizerHelper.ASCII_CHARACTERS_THE_SANITIZER_REMOVES;


public class ValueSanitizer_usingOwaspJavaHtmlSanitizer extends AbsValueSanitizer {

    public ValueSanitizer_usingOwaspJavaHtmlSanitizer() {

        init();
    }

    protected void init()
    {
    }

    /**
     * Sanitize value to prevent cross-site scripting attacks
     *
     * @param value
     * @return
     */
    @Override
    public String sanitize(String value)
    {
        if (value == null)
        {
            return value;
        }

        // I'm doing this because the HtmlPolicyBuilder class is not thread safe.

        PolicyFactory policyDefinition = new HtmlPolicyBuilder()
                .allowStandardUrlProtocols()
                        // Allow title="..." on any element.
                .allowAttributes("title").globally()
                        // Allow href="..." on <a> elements.
                .allowAttributes("href").onElements("a")
                        // Defeat link spammers.
                .requireRelNofollowOnLinks()
                        // Allow lang= with an alphabetic value on any element.
                .allowAttributes("lang").matching(Pattern.compile("[a-zA-Z]{2,20}"))
                .globally()
                        // The align attribute on <p> elements can have any value below.
                .allowAttributes("align")
                .matching(true, "center", "left", "right", "justify", "char")
                .onElements("p")
                        // These elements are allowed.
                .allowElements(
                        "a", "p", "div", "i", "b", "em", "blockquote", "tt", "strong",
                        "br", "ul", "ol", "li")
                        // Custom slashdot tags.
                        // These could be rewritten in the sanitizer using an ElementPolicy.
                .allowElements("quote", "ecode")
                .toFactory();

        String sanitizedValue = policyDefinition.sanitize(value);
        sanitizedValue = backOutSubstitutionsThatWeDoNotDesire(sanitizedValue);

        return sanitizedValue;
    }

    private String backOutSubstitutionsThatWeDoNotDesire(String sanitizedValue) {

        // back out these substitutions (they aren't desirable for our purposes)

        for (String substitutionString : ASCII_CHARACTERS_THE_SANITIZER_REMOVES.keySet()){

            String character = ASCII_CHARACTERS_THE_SANITIZER_REMOVES.get(substitutionString);
            sanitizedValue = sanitizedValue.replaceAll(substitutionString, character);
        }

        return sanitizedValue;
    }

}
