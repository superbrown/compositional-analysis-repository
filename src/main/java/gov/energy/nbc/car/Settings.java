package gov.energy.nbc.car;

public class Settings implements ISettings {

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
        mongoDatabaseName = "car";
        rootDirectoryForDataFiles = "C:/data/research-data/uploadedFiles";
    }

    @Override
    public String getMongoDatabaseName() {
        return mongoDatabaseName;
    }

    @Override
    public String getMongoDbHost() {
        return mongoDbHost;
    }

    @Override
    public int getMongoDbPort() {
        return mongoDbPort;
    }

    @Override
    public void setMongoDatabaseName(String MONGO_DATABASE_NAME) {
        this.mongoDatabaseName = MONGO_DATABASE_NAME;
    }

    @Override
    public void setSetMongoDbHost(String MONGO_DB_HOST) {
        this.mongoDbHost = MONGO_DB_HOST;
    }

    @Override
    public void setMongoDbHost(String mongoDbHost) {
        this.mongoDbHost = mongoDbHost;
    }

    @Override
    public void setMondgoDbPort(int MONGO_DB_PORT) {
        this.mongoDbPort = MONGO_DB_PORT;
    }

    @Override
    public void setMongoDbPort(int mongoDbPort) {
        this.mongoDbPort = mongoDbPort;
    }

    @Override
    public String getRootDirectoryForDataFiles() {
        return rootDirectoryForDataFiles;
    }

    @Override
    public void setRootDirectoryForDataFiles(String rootDirectoryForDataFiles) {
        this.rootDirectoryForDataFiles = rootDirectoryForDataFiles;
    }
}
