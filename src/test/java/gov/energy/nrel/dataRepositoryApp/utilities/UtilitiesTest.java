package gov.energy.nrel.dataRepositoryApp.utilities;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class UtilitiesTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testPutIfNotBlank() throws Exception {

        String name = "name";
        String value = "value";

        Document document = new Document();
        Utilities.putIfNotBlank(document, name, value);
        assertTrue(value.equals(document.get(name)));

        document = new Document();
        Utilities.putIfNotBlank(document, name, null);
        assertTrue(null == document.get(name));

        document = new Document();
        Utilities.putIfNotBlank(document, name, "");
        assertTrue(null == document.get(name));
    }

    @Test
    public void testToListOrNull() throws Exception {

        String element_1 = "element 1";
        String element_2 = "element 2";

        String[] strings = {element_1, element_2};

        List<String> result = Utilities.toListOrNull(strings);
        assertTrue(result != null);
        assertTrue(result.size() == 2);
        assertTrue(result.contains(element_1));
        assertTrue(result.contains(element_2));

        strings = new String[] {};

        result = Utilities.toListOrNull(strings);
        assertTrue(result == null);

        strings = null;

        result = Utilities.toListOrNull(strings);
        assertTrue(result == null);
    }
}
