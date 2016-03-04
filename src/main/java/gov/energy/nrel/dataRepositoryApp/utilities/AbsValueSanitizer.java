package gov.energy.nrel.dataRepositoryApp.utilities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class AbsValueSanitizer {

    public abstract String sanitize(String value);

    public Object sanitize(Object value)
    {
        Object sanitizedValue;

        if (value instanceof String)
        {
            sanitizedValue = sanitize((String) value);
        }
        else
        {
            sanitizedValue = value;
        }

        return sanitizedValue;
    }

    public boolean isSanitary(String value) {

        String sanitizedValue = sanitize(value);

        // we trim this because sanitize() appears to trim trailing spaces
        return (value.trim().equals(sanitizedValue.trim()));
    }

    /**
     * Sanitize value to prevent cross-site scripting attacks
     *
     * @param values
     * @return
     */
    public List sanitize(List values)
    {
        if (values == null)
        {
            return values;
        }

        for (int i = 0; i < values.size(); i++)
        {
            // I am commenting this out because I don't want to risk the possibility that there
            // might be recursive collections, putting the application into an endless loop.
//            Object sanitizedValue = sanitizeValue(values.get(i));

            Object value = values.get(i);
            Object sanitizedValue;

            if (value instanceof String)
            {
                sanitizedValue = sanitize(value);
            }
            else
            {
                sanitizedValue = value;
            }

            values.set(i, sanitizedValue);
        }

        return values;
    }

    /**
     * Sanitize value to prevent cross-site scripting attacks
     *
     * @param values
     * @return
     */
    public Set sanitize(Set values)
    {
        if (values == null)
        {
            return values;
        }

        Set sanitizedValues = new HashSet();

        for (Object value : values)
        {
            // I am commenting this out because I don't want to risk the possibility that there
            // might be recursive collections, putting the application into an endless loop.
//            Object sanitizedValue = sanitizeValue(value);
//            sanitizedValues.add(sanitizedValue);

            Object sanitizedValue;

            if (value instanceof String)
            {
                sanitizedValue = sanitize(value);
            }
            else
            {
                sanitizedValue = value;
            }

            sanitizedValues.add(sanitizedValue);
        }

        values.clear();
        values.addAll(sanitizedValues);

        return values;
    }

    /**
     * Sanitize value to prevent cross-site scripting attacks
     *
     * @param values
     * @return
     */
    public Object[] sanitize(Object[] values)
    {
        if (values == null)
        {
            return values;
        }

        for (int i = 0; i < values.length; i++)
        {
            // I am commenting this out because I don't want to risk the possibility that there
            // might be recursive collections, putting the application into an endless loop.
//            Object sanitizedValue = sanitizeValue(values[i]);

            Object value = values[i];
            Object sanitizedValue;

            if (value instanceof String)
            {
                sanitizedValue = sanitize(value);
            }
            else
            {
                sanitizedValue = value;
            }

            values[i] = sanitizedValue;
        }

        return values;
    }
}
