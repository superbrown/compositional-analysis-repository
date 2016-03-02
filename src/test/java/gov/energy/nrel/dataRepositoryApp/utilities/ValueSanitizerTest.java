package gov.energy.nrel.dataRepositoryApp.utilities;

import org.junit.Assert;
import org.junit.Before;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: mxbro16
 * Date: Jul 20, 2011
 */
public class ValueSanitizerTest
{
    ValueSanitizer valueSanitizer;

    @Before
    public void setup() {

        valueSanitizer = new ValueSanitizer();
    }

    @org.junit.Test
    public void testSanitizeValueForNull()
    {
        Object sanitizedValue;

        sanitizedValue = valueSanitizer.sanitize((String) null);
        Assert.assertTrue(sanitizedValue == null);

        sanitizedValue = valueSanitizer.sanitize((List) null);
        Assert.assertTrue(sanitizedValue == null);

        sanitizedValue = valueSanitizer.sanitize((String[]) null);
        Assert.assertTrue(sanitizedValue == null);

        sanitizedValue = valueSanitizer.sanitize((Set) null);
        Assert.assertTrue(sanitizedValue == null);

        sanitizedValue = valueSanitizer.sanitize((Object) null);
        Assert.assertTrue(sanitizedValue == null);
    }

    @org.junit.Test
    public void testSanitizeValueForString()
    {
        String stringValue = "Mike";

        Object sanitizedValue;

        sanitizedValue = valueSanitizer.sanitize(stringValue);
        Assert.assertTrue(sanitizedValue != stringValue);
        Assert.assertTrue(sanitizedValue.equals(stringValue));
    }

    @org.junit.Test
    public void testSanitizeValueForGenericType()
    {
        Integer genericTypeValue = 1;

        Object sanitizedValue;

        sanitizedValue = valueSanitizer.sanitize(genericTypeValue);
        Assert.assertTrue(sanitizedValue == genericTypeValue);
    }

    @org.junit.Test
    public void testSanitizeValueForList()
    {
        String stringValue = "Mike";
        Integer genericTypeValue = 1;

        List listValue = new ArrayList<>();
        listValue.add(stringValue);
        listValue.add(genericTypeValue);

        Object sanitizedValue;

        sanitizedValue = valueSanitizer.sanitize(listValue);
        Assert.assertTrue(sanitizedValue instanceof List);
        List sanitizedValueList = (List)sanitizedValue;

        Assert.assertTrue(sanitizedValueList .size() == 2);

        Assert.assertTrue(sanitizedValueList.get(0) != stringValue);
        Assert.assertTrue(sanitizedValueList.get(0).equals(stringValue));

        Assert.assertTrue(sanitizedValueList.get(1) == genericTypeValue);
    }

    @org.junit.Test
    public void testSanitizeValueForArray()
    {
        String stringValue01 = "Mike";
        String stringValue02 = "Brown";
        Integer genericTypeValue = 1;

        Object[] arrayValue = new Object[3];
        arrayValue[0] = stringValue01;
        arrayValue[1] = stringValue02;
        arrayValue[2] = genericTypeValue;

        Object sanitizedValue;

        sanitizedValue = valueSanitizer.sanitize(arrayValue);
        Assert.assertTrue(sanitizedValue instanceof Object[]);
        Object[] sanitizedValueList = (Object[])sanitizedValue;

        Assert.assertTrue(sanitizedValueList.length == 3);

        Assert.assertTrue(sanitizedValueList[0] != stringValue01);
        Assert.assertTrue(sanitizedValueList[0].equals(stringValue01));

        Assert.assertTrue(sanitizedValueList[1] != stringValue02);
        Assert.assertTrue(sanitizedValueList[1].equals(stringValue02));

        Assert.assertTrue(sanitizedValueList[2] == genericTypeValue);
    }

    @org.junit.Test
    public void testSanitizeValueForStringArray()
    {
        String stringValue01 = "Mike";
        String stringValue02 = "Brown";

        String[] arrayValue = new String[2];
        arrayValue[0] = stringValue01;
        arrayValue[1] = stringValue02;

        Object sanitizedValue;

        sanitizedValue = valueSanitizer.sanitize(arrayValue);
        Assert.assertTrue(sanitizedValue instanceof String[]);
        String[] sanitizedValueArray = (String[])sanitizedValue;

        Assert.assertTrue(sanitizedValueArray.length == 2);

        Assert.assertTrue(sanitizedValueArray[0] != stringValue01);
        Assert.assertTrue(sanitizedValueArray[0].equals(stringValue01));

        Assert.assertTrue(sanitizedValueArray[1] != stringValue02);
        Assert.assertTrue(sanitizedValueArray[1].equals(stringValue02));
    }

    @org.junit.Test
    public void testSanitizeValueForSet()
    {
        String stringValue = "Mike";
        Integer genericTypeValue = 1;

        Set setValue = new HashSet();
        setValue.add(stringValue);
        setValue.add(genericTypeValue);

        Object sanitizedValue;

        sanitizedValue = valueSanitizer.sanitize(setValue);
        Assert.assertTrue(sanitizedValue instanceof Set);
        Set sanitizedValueSet = (Set)sanitizedValue;

        Assert.assertTrue(sanitizedValueSet.size() == 2);

        Assert.assertTrue(sanitizedValueSet.contains(stringValue));

        Assert.assertTrue(sanitizedValueSet.contains(genericTypeValue));
    }

    @org.junit.Test
    public void testSanitizeValueForArrayWithinAnArray()
    {
        String stringValue01 = "Mike01";
        String stringValue02 = "Brown01";

        String stringValue0102 = "Mike02";
        String stringValue0202 = "Brown02";
        Integer genericTypeValue02 = 1;


        Object[] arrayValue01 = new Object[2];
        arrayValue01[0] = stringValue01;
        arrayValue01[1] = stringValue02;


        Object[] arrayValue02 = new Object[4];
        arrayValue02[0] = stringValue0102;
        arrayValue02[1] = stringValue0202;
        arrayValue02[2] = genericTypeValue02;
        arrayValue02[3] = arrayValue01;

        Object sanitizedValue;

        sanitizedValue = valueSanitizer.sanitize(arrayValue02);
        Assert.assertTrue(sanitizedValue instanceof Object[]);
        Object[] sanitizedValueArray = (Object[])sanitizedValue;

        Assert.assertTrue(sanitizedValueArray.length == 4);

        Assert.assertTrue(sanitizedValueArray[0].equals(stringValue0102));
        Assert.assertTrue(sanitizedValueArray[1].equals(stringValue0202));
        Assert.assertTrue(sanitizedValueArray[2] == genericTypeValue02);

        Assert.assertTrue(sanitizedValueArray[3] == arrayValue01);

        Assert.assertTrue(((Object[])sanitizedValueArray[3]).length == 2);
        Assert.assertTrue(((Object[])sanitizedValueArray[3])[0].equals(stringValue01));
        Assert.assertTrue(((Object[])sanitizedValueArray[3])[1].equals(stringValue02));
    }

    @org.junit.Test
    public void testSanitizeValueForListWithinAList()
    {
        String stringValue01 = "Mike01";
        String stringValue02 = "Brown01";

        String stringValue0102 = "Mike02";
        String stringValue0202 = "Brown02";
        Integer genericTypeValue02 = 1;


        List arrayValue01 = new ArrayList<>();
        arrayValue01.add(stringValue01);
        arrayValue01.add(stringValue02);


        List arrayValue02 = new ArrayList<>();
        arrayValue02.add(stringValue0102);
        arrayValue02.add(stringValue0202);
        arrayValue02.add(genericTypeValue02);
        arrayValue02.add(arrayValue01);

        Object sanitizedValue;

        sanitizedValue = valueSanitizer.sanitize(arrayValue02);
        Assert.assertTrue(sanitizedValue instanceof List);
        List sanitizedValueArray = (List)sanitizedValue;

        Assert.assertTrue(sanitizedValueArray.size() == 4);

        Assert.assertTrue(sanitizedValueArray.get(0).equals(stringValue0102));
        Assert.assertTrue(sanitizedValueArray.get(1).equals(stringValue0202));
        Assert.assertTrue(sanitizedValueArray.get(2) == genericTypeValue02);

        Assert.assertTrue(sanitizedValueArray.get(3) == arrayValue01);

        Assert.assertTrue(((List)sanitizedValueArray.get(3)).size() == 2);
        Assert.assertTrue(((List)sanitizedValueArray.get(3)).get(0).equals(stringValue01));
        Assert.assertTrue(((List)sanitizedValueArray.get(3)).get(1).equals(stringValue02));
    }

}
