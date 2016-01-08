package gov.energy.nrel.dataRepositoryApp;

import com.mongodb.MongoTimeoutException;
import gov.energy.nrel.dataRepositoryApp.bo.IBusinessObjects;
import gov.energy.nrel.dataRepositoryApp.bo.IDataCategoryBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.DataCategoryAlreadyExists;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.singleCellSchemaApproach.s_BusinessObjects;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import gov.energy.nrel.dataRepositoryApp.utilities.PerformanceLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * The Data Repository Application was originally designed and coded by Mike Brown (mike.public@superbrown.com)
 * October through December 2015 while on contract at NREL.  Feel free to reach out to him for any assistance.  (Include
 * "Data Repository Application" in the subject line.)
 */

// "@Component" indicates to Spring that the class is a "component".  Such classes are considered as candidates for
// auto-detection when using annotation-based configuration and classpath scanning.
//
// Reference: http://docs.spring.io/spring-framework/docs/2.5.x/api/org/springframework/stereotype/Component.html
//
// "@EnableAutoConfiguration" enable auto-configuration of the Spring Application Context, attempting to guess and
// configure beans that you are likely to need. Auto-configuration classes are usually applied based on your classpath
// and what beans you have defined. For example, If you have tomcat-embedded.jar on your classpath you are likely to
// want a TomcatEmbeddedServletContainerFactory (unless you have defined your own EmbeddedServletContainerFactory bean).
//
// Auto-configuration tries to be as intelligent as possible and will back-away as you define more of your own
// configuration. You can always manually exclude() any configuration that you never want to apply (use excludeName() if
// you don't have access to them). You can also exclude them via the spring.autoconfigure.exclude property. Auto-
// configuration is always applied after user-defined beans have been registered.
//
// The package of the class that is annotated with @EnableAutoConfiguration has specific significance and is often used
// as a 'default'. For example, it will be used when scanning for @Entity classes. It is generally recommended that you
// place @EnableAutoConfiguration in a root package so that all sub-packages and classes can be searched.
//
// Auto-configuration classes are regular Spring Configuration beans.  They are located using the SpringFactoriesLoader
// mechanism (keyed against this class). Generally auto-configuration beans are @Conditional beans (most often using
// @ConditionalOnClass and @ConditionalOnMissingBean annotations).
//
// Reference: http://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/EnableAutoConfiguration.html

@Component
@EnableAutoConfiguration
public class DataRepositoryApplication extends SpringApplication {

    @Autowired
    private ISettings settings;

    protected IBusinessObjects businessObjects;

    // Spring will call this constructor at app startup by virtue of this being annotated as a Component.
    public DataRepositoryApplication() {
    }

    // Spring will call this after it calls the concstrutor.
    @PostConstruct
    protected void init() {

        try {
            // This line is very significant. It determines what business objects the app will use.
            //
            // During development I experimented with a couple of different approaches to the DAO layer, and changing
            // what class was instantiated here determined which one would be used.

            IBusinessObjects businessObjects = new s_BusinessObjects(this);
            setBusinessObjects(businessObjects);
        }
        catch (MongoTimeoutException e) {
            // This is written like this so it stands out in the log.
            throw new RuntimeException("\n" +
                    "\n" +
                    "T H E    M O N G O   D A T A B A S E   I S   N O T   R E S P O N D I N G .\n" +
                    "\n" +
                    "M A K E   S U R E   I T   I S   R U N N I N G .\n",
                    e);
        }
    }

    public ISettings getSettings() {
        return settings;
    }

    public void setBusinessObjects(IBusinessObjects businessObjects) {

        this.businessObjects = businessObjects;
        String[] defaultSetOfDataCategories = businessObjects.getSettings().getDefaultSetOfDataCategories();
        assureCategoriesAreInTheDatabase(businessObjects, defaultSetOfDataCategories);
    }

    public IBusinessObjects getBusinessObjects() {

        return businessObjects;
    }


    protected static void assureCategoriesAreInTheDatabase(
            IBusinessObjects businessObjects, String[] dataCategoryNames) {

        IDataCategoryBO dataCategoryBO = businessObjects.getDataCategoryBO();

        for (String dataCategoryName : dataCategoryNames) {

            assureDataCategoryIsInTheDatabase(
                    dataCategoryBO,
                    dataCategoryName);
        }
    }

    protected static void assureDataCategoryIsInTheDatabase(
            IDataCategoryBO dataCategoryDAO,
            String categoryName) {

        try {
            dataCategoryDAO.addDataCategory(categoryName);
        }
        catch (DataCategoryAlreadyExists e) {
            // that's fine
        }
    }

    // This constructor is used by unit tests.
    public DataRepositoryApplication(ISettings settings) {

        this.settings = settings;

        if (settings.getPerformanceLoggingEnabled() == true) {
            PerformanceLogger.enable();
        }
        else {
            PerformanceLogger.disable();
        }
    }
}
