package gov.energy.nbc.car.bo.mongodb;

import gov.energy.nbc.car.bo.IDatasetBO;
import gov.energy.nbc.car.bo.IPhysicalFileBO;
import gov.energy.nbc.car.bo.PhysicalFileBO;
import gov.energy.nbc.car.dao.IDatasetDAO;
import gov.energy.nbc.car.dao.mongodb.singleCellCollectionApproach.s_DatasetDAO;
import gov.energy.nbc.car.model.IDatasetDocument;
import gov.energy.nbc.car.settings.ISettings;

import java.io.File;

public abstract class AbsDatasetBO implements IDatasetBO {

    protected IDatasetDAO datasetDAO;
    protected IPhysicalFileBO physicalFileBO;

    public AbsDatasetBO(ISettings settings) {

        physicalFileBO = new PhysicalFileBO(settings);
        datasetDAO = new s_DatasetDAO(settings);
    }

    public IDatasetDAO getDatasetDAO() {
        return datasetDAO;
    }

    public File getOriginallyUploadedFile(String datasetId) {

        IDatasetDocument dataset = getDatasetDAO().getDataset(datasetId);
        String storageLocation = dataset.getMetadata().getUploadedFile().getStorageLocation();
        File originallyUploadedFile = physicalFileBO.getFile(storageLocation);
        return originallyUploadedFile;
    }
}
