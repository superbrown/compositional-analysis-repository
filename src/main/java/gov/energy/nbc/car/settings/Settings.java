package gov.energy.nbc.car.settings;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

@Configuration
@Component
@ComponentScan(basePackages = { "gov.energy.nbc.car.*" })
@AutoConfigureBefore
public class Settings implements ISettings {

    // DESIGN NOTE: These are all values in the application.properties file.

    @Value("${mongoDb.host}")
    protected String mongoDbHost;

    @Value("${mongoDb.port}")
    protected String mongoDbPort;

    @Value("${mongoDb.databaseName}")
    protected String mongoDatabaseName;

    @Value("${rootDirectoryForDataFiles}")
    protected String rootDirectoryForUploadedDataFiles;

    @Value("${defaultSetOfDataCategories}")
    private String[] defaultSetOfDataCategories;

    @Value("${performanceLoggingEnabled}")
    private boolean performanceLoggingEnabled;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public Settings() {

        init();
    }

    protected void init() {

//        mongoDbHost = "localhost";
//        mongoDbPort = "27017";
//        mongoDatabaseName = "data-repository-app";
//        rootDirectoryForDataFiles = "C:/data/data-repository-app/uploadedFiles";
//        defaultSetOfDataCategories = new String[] {};
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
    public void setMongoDatabaseName(String mongoDatabaseName) {
        this.mongoDatabaseName = mongoDatabaseName;
    }

    @Override
    public void setSetMongoDbHost(String mongoDbHost) {
        this.mongoDbHost = mongoDbHost;
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
    public String getRootDirectoryForUploadedDataFiles() {
        return rootDirectoryForUploadedDataFiles;
    }

    @Override
    public void setRootDirectoryForUploadedDataFiles(String rootDirectoryForUploadedDataFiles) {
        this.rootDirectoryForUploadedDataFiles = rootDirectoryForUploadedDataFiles;
    }

    @Override
    public String[] getDefaultSetOfDataCategories() {
        return defaultSetOfDataCategories;
    }

    @Override
    public void setDefaultSetOfDataCategories(String[] defaultSetOfDataCategories) {
        this.defaultSetOfDataCategories = defaultSetOfDataCategories;
    }

    @Override
    public boolean getPerformanceLoggingEnabled() {
        return this.performanceLoggingEnabled;
    }

    @Override
    public void setPerformanceLoggingEnabled(Boolean performanceLoggingEnabled) {
        this.performanceLoggingEnabled = performanceLoggingEnabled;
    }
}
