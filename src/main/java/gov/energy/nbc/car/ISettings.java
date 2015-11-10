package gov.energy.nbc.car;

/**
 * Created by mbrown on 11/8/2015.
 */
public interface ISettings {

    String getMongoDatabaseName();

    String getMongoDbHost();

    String getMongoDbPort();

    String getRootDirectoryForDataFiles();

    void setMongoDatabaseName(String MONGO_DATABASE_NAME);

    void setSetMongoDbHost(String MONGO_DB_HOST);

    void setMongoDbHost(String mongoDbHost);

    void setMongoDbPort(String MONGO_DB_PORT);

    void setRootDirectoryForDataFiles(String rootDirectoryForDataFiles);
}
