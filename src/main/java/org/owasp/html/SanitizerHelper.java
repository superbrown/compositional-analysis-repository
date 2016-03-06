package org.owasp.html;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * The only reason for having this class is to be able to access Encoding.REPLACEMENTS which, unfortunately, was
 * declared package local.
 */
public class SanitizerHelper {


    public static final Map<String, String> ASCII_CHARACTERS_THE_SANITIZER_REMOVES = new HashMap<>();

    static {

        for (int i = 0; i < Encoding.REPLACEMENTS.length; i++) {

            String replacement = Encoding.REPLACEMENTS[i];

            if (StringUtils.isNotEmpty(replacement)) {

                String charAsString = "" + ((char) i);
                ASCII_CHARACTERS_THE_SANITIZER_REMOVES.put(replacement, charAsString);
            }
        }
    }
}
