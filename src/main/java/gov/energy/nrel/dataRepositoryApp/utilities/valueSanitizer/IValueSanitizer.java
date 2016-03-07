package gov.energy.nrel.dataRepositoryApp.utilities.valueSanitizer;

import java.util.List;
import java.util.Set;

/**
 * Created by Mike on 3/7/2016.
 */
public interface IValueSanitizer
{
    String sanitize(String value);

    Object sanitize(Object value);

    boolean isSanitary(String value);

    List sanitize(List values);

    Set sanitize(Set values);

    Object[] sanitize(Object[] values);
}
