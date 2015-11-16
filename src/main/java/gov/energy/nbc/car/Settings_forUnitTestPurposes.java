package gov.energy.nbc.car;

public class Settings_forUnitTestPurposes extends Settings {

    public Settings_forUnitTestPurposes() {

        init();
    }

    protected void init() {

        super.init();

        setMongoDbHost("localhost");
        setMongoDbPort("27017");
        setMongoDatabaseName("car_forUnitTestPurposes");
        setRootDirectoryForUploadedDataFiles("C:/projects/car/target/test-classes");
        setDefaultSetOfDataCategories(new String[] {});
    }
}
