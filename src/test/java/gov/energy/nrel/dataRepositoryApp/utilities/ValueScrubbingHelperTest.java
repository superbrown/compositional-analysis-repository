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
public class ValueScrubbingHelperTest
{
    ValueScrubbingHelper valueScrubbingHelper;

    @Before
    public void setup() {

        valueScrubbingHelper = new ValueScrubbingHelper();
    }

    @org.junit.Test
    public void testScrubValueForNull()
    {
        Object scrubbedValue;

        scrubbedValue = valueScrubbingHelper.scrubValue((String)null);
        Assert.assertTrue(scrubbedValue == null);

        scrubbedValue = valueScrubbingHelper.scrubValue((List)null);
        Assert.assertTrue(scrubbedValue == null);

        scrubbedValue = valueScrubbingHelper.scrubValue((String[])null);
        Assert.assertTrue(scrubbedValue == null);

        scrubbedValue = valueScrubbingHelper.scrubValue((Set)null);
        Assert.assertTrue(scrubbedValue == null);

        scrubbedValue = valueScrubbingHelper.scrubValue((Object)null);
        Assert.assertTrue(scrubbedValue == null);
    }

    @org.junit.Test
    public void testScrubValueForString()
    {
        String stringValue = "Mike";

        Object scrubbedValue;

        scrubbedValue = valueScrubbingHelper.scrubValue(stringValue);
        Assert.assertTrue(scrubbedValue != stringValue);
        Assert.assertTrue(scrubbedValue.equals(stringValue));
    }

    @org.junit.Test
    public void testScrubValueForGenericType()
    {
        Integer genericTypeValue = 1;

        Object scrubbedValue;

        scrubbedValue = valueScrubbingHelper.scrubValue(genericTypeValue);
        Assert.assertTrue(scrubbedValue == genericTypeValue);
    }

    @org.junit.Test
    public void testScrubValueForList()
    {
        String stringValue = "Mike";
        Integer genericTypeValue = 1;

        List listValue = new ArrayList<>();
        listValue.add(stringValue);
        listValue.add(genericTypeValue);

        Object scrubbedValue;

        scrubbedValue = valueScrubbingHelper.scrubValue(listValue);
        Assert.assertTrue(scrubbedValue instanceof List);
        List scrubbedValueList = (List)scrubbedValue;

        Assert.assertTrue(scrubbedValueList .size() == 2);

        Assert.assertTrue(scrubbedValueList.get(0) != stringValue);
        Assert.assertTrue(scrubbedValueList.get(0).equals(stringValue));

        Assert.assertTrue(scrubbedValueList.get(1) == genericTypeValue);
    }

    @org.junit.Test
    public void testScrubValueForArray()
    {
        String stringValue01 = "Mike";
        String stringValue02 = "Brown";
        Integer genericTypeValue = 1;

        Object[] arrayValue = new Object[3];
        arrayValue[0] = stringValue01;
        arrayValue[1] = stringValue02;
        arrayValue[2] = genericTypeValue;

        Object scrubbedValue;

        scrubbedValue = valueScrubbingHelper.scrubValue(arrayValue);
        Assert.assertTrue(scrubbedValue instanceof Object[]);
        Object[] scrubbedValueList = (Object[])scrubbedValue;

        Assert.assertTrue(scrubbedValueList.length == 3);

        Assert.assertTrue(scrubbedValueList[0] != stringValue01);
        Assert.assertTrue(scrubbedValueList[0].equals(stringValue01));

        Assert.assertTrue(scrubbedValueList[1] != stringValue02);
        Assert.assertTrue(scrubbedValueList[1].equals(stringValue02));

        Assert.assertTrue(scrubbedValueList[2] == genericTypeValue);
    }

    @org.junit.Test
    public void testScrubValueForStringArray()
    {
        String stringValue01 = "Mike";
        String stringValue02 = "Brown";

        String[] arrayValue = new String[2];
        arrayValue[0] = stringValue01;
        arrayValue[1] = stringValue02;

        Object scrubbedValue;

        scrubbedValue = valueScrubbingHelper.scrubValue(arrayValue);
        Assert.assertTrue(scrubbedValue instanceof String[]);
        String[] scrubbedValueArray = (String[])scrubbedValue;

        Assert.assertTrue(scrubbedValueArray.length == 2);

        Assert.assertTrue(scrubbedValueArray[0] != stringValue01);
        Assert.assertTrue(scrubbedValueArray[0].equals(stringValue01));

        Assert.assertTrue(scrubbedValueArray[1] != stringValue02);
        Assert.assertTrue(scrubbedValueArray[1].equals(stringValue02));
    }

    @org.junit.Test
    public void testScrubValueForSet()
    {
        String stringValue = "Mike";
        Integer genericTypeValue = 1;

        Set setValue = new HashSet();
        setValue.add(stringValue);
        setValue.add(genericTypeValue);

        Object scrubbedValue;

        scrubbedValue = valueScrubbingHelper.scrubValue(setValue);
        Assert.assertTrue(scrubbedValue instanceof Set);
        Set scrubbedValueSet = (Set)scrubbedValue;

        Assert.assertTrue(scrubbedValueSet.size() == 2);

        Assert.assertTrue(scrubbedValueSet.contains(stringValue));

        Assert.assertTrue(scrubbedValueSet.contains(genericTypeValue));
    }

    @org.junit.Test
    public void testScrubValueForArrayWithinAnArray()
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

        Object scrubbedValue;

        scrubbedValue = valueScrubbingHelper.scrubValue(arrayValue02);
        Assert.assertTrue(scrubbedValue instanceof Object[]);
        Object[] scrubbedValueArray = (Object[])scrubbedValue;

        Assert.assertTrue(scrubbedValueArray.length == 4);

        Assert.assertTrue(scrubbedValueArray[0].equals(stringValue0102));
        Assert.assertTrue(scrubbedValueArray[1].equals(stringValue0202));
        Assert.assertTrue(scrubbedValueArray[2] == genericTypeValue02);

        Assert.assertTrue(scrubbedValueArray[3] == arrayValue01);

        Assert.assertTrue(((Object[])scrubbedValueArray[3]).length == 2);
        Assert.assertTrue(((Object[])scrubbedValueArray[3])[0].equals(stringValue01));
        Assert.assertTrue(((Object[])scrubbedValueArray[3])[1].equals(stringValue02));
    }

    @org.junit.Test
    public void testScrubValueForListWithinAList()
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

        Object scrubbedValue;

        scrubbedValue = valueScrubbingHelper.scrubValue(arrayValue02);
        Assert.assertTrue(scrubbedValue instanceof List);
        List scrubbedValueArray = (List)scrubbedValue;

        Assert.assertTrue(scrubbedValueArray.size() == 4);

        Assert.assertTrue(scrubbedValueArray.get(0).equals(stringValue0102));
        Assert.assertTrue(scrubbedValueArray.get(1).equals(stringValue0202));
        Assert.assertTrue(scrubbedValueArray.get(2) == genericTypeValue02);

        Assert.assertTrue(scrubbedValueArray.get(3) == arrayValue01);

        Assert.assertTrue(((List)scrubbedValueArray.get(3)).size() == 2);
        Assert.assertTrue(((List)scrubbedValueArray.get(3)).get(0).equals(stringValue01));
        Assert.assertTrue(((List)scrubbedValueArray.get(3)).get(1).equals(stringValue02));
    }

}
