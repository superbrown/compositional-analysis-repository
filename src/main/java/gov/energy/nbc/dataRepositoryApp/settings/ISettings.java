package gov.energy.nbc.car.settings;


public interface ISettings {

    String getMongoDatabaseName();

    String getMongoDbHost();

    String getMongoDbPort();

    String getRootDirectoryForUploadedDataFiles();

    void setMongoDatabaseName(String MONGO_DATABASE_NAME);

    void setSetMongoDbHost(String MONGO_DB_HOST);

    void setMongoDbHost(String mongoDbHost);

    void setMongoDbPort(String MONGO_DB_PORT);

    void setRootDirectoryForUploadedDataFiles(String rootDirectoryForUploadedDataFiles);

    String[] getDefaultSetOfDataCategories();

    void setDefaultSetOfDataCategories(String[] defaultSetOfDataCategories);

    boolean getPerformanceLoggingEnabled();

    void setPerformanceLoggingEnabled(Boolean performanceLoggingEnabled);
}
