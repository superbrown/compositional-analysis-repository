package gov.energy.nbc.car;

public class Settings_forUnitTestPurposes extends Settings {

    public Settings_forUnitTestPurposes() {

        init();
    }

    protected void init() {

        super.init();
        setMongoDatabaseName("car_forUnitTestPurposes");
        setRootDirectoryForDataFiles("C:/projects/car/target/test-classes");
    }
}
