package gov.energy.nbc.spreadsheet;

public class Settings {

    protected String MONGO_DB_HOST = "localhost";
    protected int MONGO_DB_PORT = 27017;
    protected String MONGO_DATABASE_NAME = "spreadsheetRepository";

    public String getMongoDatabaseName() {
        return MONGO_DATABASE_NAME;
    }

    public String getMongoDbHost() {
        return MONGO_DB_HOST;
    }

    public int getMongoDbPort() {
        return MONGO_DB_PORT;
    }
}
