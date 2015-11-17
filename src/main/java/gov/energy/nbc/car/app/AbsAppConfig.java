package gov.energy.nbc.car.app;

import gov.energy.nbc.car.bo.IBusinessObjects;
import gov.energy.nbc.car.dao.IDataCategoryDAO;
import gov.energy.nbc.car.model.mongodb.common.Metadata;
import gov.energy.nbc.car.model.mongodb.document.DataCategoryDocument;
import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.settings.Settings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbsAppConfig {

    public static final String[] DEFAULT_SET_OF_DATA_CATEGORIES = new String[]{"Algea", "ATP3", "Biomas"};

    protected ISettings settings;

    protected IBusinessObjects businessObjects;
    protected IBusinessObjects businessObjects_forUseDuringTestMode;

    private Settings settings_testMode;

    public AbsAppConfig(ISettings settings) {

        this.settings = settings;
        init();
    }

    protected void init() {

        IBusinessObjects businessObjects = createBusinessObjects(settings);
        setBuninsessObject(businessObjects);

        businessObjects_forUseDuringTestMode = createBusinessObjects(getSettings_testMode());
        setBusinessObjects_forUseDuringTestMode(businessObjects);
    }


    protected void setBuninsessObject(IBusinessObjects businessObjects) {

        this.businessObjects = businessObjects;
        String[] defaultSetOfDataCategories = businessObjects.getSettings().getDefaultSetOfDataCategories();
        assureCategoriesAreInTheDatabase(businessObjects, defaultSetOfDataCategories);
    }

    protected void setBusinessObjects_forUseDuringTestMode(IBusinessObjects businessObjects) {

        this.businessObjects_forUseDuringTestMode = businessObjects;
        String[] defaultSetOfDataCategories = businessObjects_forUseDuringTestMode.getSettings().getDefaultSetOfDataCategories();
        assureCategoriesAreInTheDatabase(businessObjects_forUseDuringTestMode, defaultSetOfDataCategories);
    }

    private ISettings getSettings_testMode() {

        if (settings_testMode == null) {

            settings_testMode = new Settings(this.settings);
            // modify the database name
            settings_testMode.setMongoDatabaseName(settings_testMode.getMongoDatabaseName() + "_forUseDuringTestMode");
        }

        return settings_testMode;
    }

    public IBusinessObjects getBusinessObjects(TestMode testMode) {

        if (testMode == TestMode.NOT_TEST_MODE) {

            return businessObjects;
        }
        else {

            return businessObjects_forUseDuringTestMode;
        }
    }


    protected static void assureCategoriesAreInTheDatabase(IBusinessObjects businessObjects, String[] dataCategoryNames) {

        IDataCategoryDAO dataCategoryDAO = businessObjects.getDataCategoryBO().getDataCategoryDAO();

        List<String> existingDataCategoryNames = dataCategoryDAO.getAllNames();

        for (String dataCategoryName : dataCategoryNames) {

            assureCategoryIsInTheDatabase(
                    dataCategoryDAO,
                    existingDataCategoryNames,
                    dataCategoryName);
        }
    }

    protected static void assureCategoryIsInTheDatabase(IDataCategoryDAO dataCategoryDAO, List<String> dataCategoryNames, String categoryName) {

        if (dataCategoryNames.contains(categoryName) == false) {

            DataCategoryDocument dataCategoryDocument = new DataCategoryDocument();
            dataCategoryDocument.setName(categoryName);

            Set<String> columnNames = new HashSet<>();

            columnNames.add(Metadata.ATTR_KEY__SAMPLE_TYPE);
            columnNames.add(Metadata.ATTR_KEY__SUBMISSION_DATE);
            columnNames.add(Metadata.ATTR_KEY__SUBMITTER);
            columnNames.add(Metadata.ATTR_KEY__PROJECT_NAME);
            columnNames.add(Metadata.ATTR_KEY__CHARGE_NUMBER);
            columnNames.add(Metadata.ATTR_KEY__COMMENTS);

            dataCategoryDocument.setColumnNames(columnNames);

            dataCategoryDAO.add(dataCategoryDocument);
        }
    }

    protected abstract IBusinessObjects createBusinessObjects(ISettings settings);
}
