package gov.energy.nbc.car;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class Settings implements ISettings {

    @Value("${mongoDb.host}")
    protected String mongoDbHost;

    @Value("${mongoDb.port}")
    protected String mongoDbPort;

    @Value("${mongoDb.databaseName}")
    protected String mongoDatabaseName;

    @Value("${rootDirectoryForDataFiles}")
    protected String rootDirectoryForDataFiles;

    public Settings() {

        init();
    }

    protected void init() {

//        mongoDbHost = "localhost";
//        mongoDbPort = 27017;
//        mongoDatabaseName = "car";
//        rootDirectoryForDataFiles = "C:/data/research-data/uploadedFiles";
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
    public String getMongoDbPort() {
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
    public void setMongoDbPort(String mongoDbPort) {
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
