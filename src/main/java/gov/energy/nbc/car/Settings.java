package gov.energy.nbc.car;

public class Settings {

    protected String mongoDbHost;
    protected int mongoDbPort;
    protected String mongoDatabaseName;
    protected String rootDirectoryForDataFiles;

    public Settings() {

        init();
    }

    protected void init() {

        mongoDbHost = "localhost";
        mongoDbPort = 27017;
        mongoDatabaseName = "researchDataRepository";
        rootDirectoryForDataFiles = "C:/data/research-data/uploadedFiles";
    }

    public String getMongoDatabaseName() {
        return mongoDatabaseName;
    }

    public String getMongoDbHost() {
        return mongoDbHost;
    }

    public int getMongoDbPort() {
        return mongoDbPort;
    }

    public void setMongoDatabaseName(String MONGO_DATABASE_NAME) {
        this.mongoDatabaseName = MONGO_DATABASE_NAME;
    }

    public void setSetMongoDbHost(String MONGO_DB_HOST) {
        this.mongoDbHost = MONGO_DB_HOST;
    }

    public void setMongoDbHost(String mongoDbHost) {
        this.mongoDbHost = mongoDbHost;
    }

    public void setMondgoDbPort(int MONGO_DB_PORT) {
        this.mongoDbPort = MONGO_DB_PORT;
    }

    public void setMongoDbPort(int mongoDbPort) {
        this.mongoDbPort = mongoDbPort;
    }

    public String getRootDirectoryForDataFiles() {
        return rootDirectoryForDataFiles;
    }

    public void setRootDirectoryForDataFiles(String rootDirectoryForDataFiles) {
        this.rootDirectoryForDataFiles = rootDirectoryForDataFiles;
    }
}
