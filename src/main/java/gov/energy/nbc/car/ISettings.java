package gov.energy.nbc.car;

/**
 * Created by mbrown on 11/8/2015.
 */
public interface ISettings {
    String getMongoDatabaseName();

    String getMongoDbHost();

    int getMongoDbPort();

    void setMongoDatabaseName(String MONGO_DATABASE_NAME);

    void setSetMongoDbHost(String MONGO_DB_HOST);

    void setMongoDbHost(String mongoDbHost);

    void setMondgoDbPort(int MONGO_DB_PORT);

    void setMongoDbPort(int mongoDbPort);

    String getRootDirectoryForDataFiles();

    void setRootDirectoryForDataFiles(String rootDirectoryForDataFiles);
}
