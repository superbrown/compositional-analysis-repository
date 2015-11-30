package gov.energy.nbc.car.app;


import gov.energy.nbc.car.bo.IBusinessObjects;
import gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach.s_BusinessObjects;
import gov.energy.nbc.car.dao.IDataCategoryDAO;
import gov.energy.nbc.car.model.mongodb.common.Metadata;
import gov.energy.nbc.car.model.mongodb.document.DataCategoryDocument;
import gov.energy.nbc.car.settings.ISettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class AppSingleton {

    @Autowired
    private ISettings settings;

    protected IBusinessObjects businessObjects;

    public AppSingleton(ISettings settings, IBusinessObjects businessObjects) {
        this.settings = settings;
        setBuninsessObject(businessObjects);
    }

    @PostConstruct
    protected void init() {

        IBusinessObjects businessObjects = new s_BusinessObjects(settings);
        setBuninsessObject(businessObjects);
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

        IDataCategoryDAO dataCategoryDAO = businessObjects.getDataCategoryBO().getDataCategoryDAO();

        List<String> existingDataCategoryNames = dataCategoryDAO.getAllNames();

        for (String dataCategoryName : dataCategoryNames) {

            assureCategoryIsInTheDatabase(
                    dataCategoryDAO,
                    existingDataCategoryNames,
                    dataCategoryName);
        }
    }

    protected static void assureCategoryIsInTheDatabase(
            IDataCategoryDAO dataCategoryDAO,
            List<String> dataCategoryNames,
            String categoryName) {

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
