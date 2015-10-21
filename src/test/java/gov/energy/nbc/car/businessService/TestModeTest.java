package gov.energy.nbc.car.businessService;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class TestModeTest {

	@Test
	public void testToTestMode() {

		assertTrue(TestMode.value(null) == TestMode.NOT_TEST_MODE);
		assertTrue(TestMode.value("") == TestMode.NOT_TEST_MODE);
		assertTrue(TestMode.value("   ") == TestMode.NOT_TEST_MODE);
		assertTrue(TestMode.value("unknown value") == TestMode.NOT_TEST_MODE);
		assertTrue(TestMode.value("true") == TestMode.TEST_MODE);
		assertTrue(TestMode.value("test") == TestMode.TEST_MODE);
		assertTrue(TestMode.value("testmode") == TestMode.TEST_MODE);
		assertTrue(TestMode.value("UNKNOWN VALUE") == TestMode.NOT_TEST_MODE);
		assertTrue(TestMode.value("TRUE") == TestMode.TEST_MODE);
		assertTrue(TestMode.value("TEST") == TestMode.TEST_MODE);
		assertTrue(TestMode.value("TESTMODE") == TestMode.TEST_MODE);
	}
}
