package gov.energy.nbc.car;

import gov.energy.nbc.car.settings.Settings;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static junit.framework.TestCase.assertTrue;

public class SettingsTest {

    private Settings settings;

    @Before
    public void before() throws Exception {
        this.settings = new Settings();
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testSetAndGetMongoDatabaseName() throws Exception {

        String mongo_database_name = "database name";
        settings.setMongoDatabaseName(mongo_database_name);
        assertTrue(settings.getMongoDatabaseName().equals(mongo_database_name));
    }

    @Test
    public void testSetAndGetMongoDbHost() throws Exception {

        String mongo_database_host = "database host";
        settings.setSetMongoDbHost(mongo_database_host);
        assertTrue(settings.getMongoDbHost().equals(mongo_database_host));
    }

    @Test
    public void testSetAndGetMongoDbPort() throws Exception {

        String mongo_database_port = "123";
        settings.setMongoDbPort(mongo_database_port);
        assertTrue(settings.getMongoDbPort() == mongo_database_port);
    }
}
