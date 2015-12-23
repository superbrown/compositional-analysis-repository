package gov.energy.nbc.car.app;


import com.mongodb.MongoTimeoutException;
import gov.energy.nbc.car.bo.IBusinessObjects;
import gov.energy.nbc.car.bo.IDataCategoryBO;
import gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach.s_BusinessObjects;
import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@EnableAutoConfiguration
public class DataRepositoryApplication {

    @Autowired
    private ISettings settings;

    protected IBusinessObjects businessObjects;

    public DataRepositoryApplication() {
    }

    public DataRepositoryApplication(ISettings settings, IBusinessObjects businessObjects) {

        this.settings = settings;

        if (settings.getPerformanceLoggingEnabled() == true) {
            PerformanceLogger.enable();
        }
        else {
            PerformanceLogger.disable();
        }

        setBuninsessObject(businessObjects);
    }

    @PostConstruct
    protected void init() {

        IBusinessObjects businessObjects = null;

        try {
            businessObjects = new s_BusinessObjects(settings);
            setBuninsessObject(businessObjects);
        }
        catch (MongoTimeoutException e) {
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

    protected void setBuninsessObject(IBusinessObjects businessObjects) {

        this.businessObjects = businessObjects;
        String[] defaultSetOfDataCategories = businessObjects.getSettings().getDefaultSetOfDataCategories();
        assureCategoriesAreInTheDatabase(businessObjects, defaultSetOfDataCategories);
    }

    public IBusinessObjects getBusinessObjects() {

        return businessObjects;
    }


    protected static void assureCategoriesAreInTheDatabase(IBusinessObjects businessObjects, String[] dataCategoryNames) {

        IDataCategoryBO dataCategoryBO = businessObjects.getDataCategoryBO();

        List<String> existingDataCategoryNames = dataCategoryBO.getDataCategoryDAO().getAllNames();

        for (String dataCategoryName : dataCategoryNames) {

            assureCategoryIsInTheDatabase(
                    dataCategoryBO,
                    existingDataCategoryNames,
                    dataCategoryName);
        }
    }

    protected static void assureCategoryIsInTheDatabase(
            IDataCategoryBO dataCategoryDAO,
            List<String> dataCategoryNames,
            String categoryName) {

        if (dataCategoryNames.contains(categoryName) == false) {

            dataCategoryDAO.addDataCategory(categoryName);
        }
    }
}
