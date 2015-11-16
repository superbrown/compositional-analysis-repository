package gov.energy.nbc.car;

import gov.energy.nbc.car.bo.IBusinessObjects;
import gov.energy.nbc.car.bo.TestMode;
import gov.energy.nbc.car.dao.IDataCategoryDAO;
import gov.energy.nbc.car.model.mongodb.common.Metadata;
import gov.energy.nbc.car.model.mongodb.document.DataCategoryDocument;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Application {

    public static final String[] DEFAULT_SET_OF_DATA_CATEGORIES = new String[]{"Algea", "ATP3", "Biomas"};

    protected static IBusinessObjects businessObjects;

    public static IBusinessObjects getBusinessObjects() {
        return businessObjects;
    }

    public static void setBusinessObjects(IBusinessObjects businessObjects) {

        Application.businessObjects = businessObjects;

        String[] defaultSetOfDataCategories = businessObjects.getSettings().getDefaultSetOfDataCategories();
        assureCategoriesAreInTheDatabase(TestMode.NOT_TEST_MODE, defaultSetOfDataCategories);

        String[] defaultSetOfDataCategories_forUnitTestPurposes = businessObjects.getSettings_forUnitTestPurposes().getDefaultSetOfDataCategories();
        assureCategoriesAreInTheDatabase(TestMode.TEST_MODE, defaultSetOfDataCategories_forUnitTestPurposes);
    }

    protected static void assureCategoriesAreInTheDatabase(TestMode testMode, String[] dataCategoryNames) {

        IDataCategoryDAO dataCategoryDAO = businessObjects.getDataCategoryBO().getDataCategoryDAO(testMode);

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
}
