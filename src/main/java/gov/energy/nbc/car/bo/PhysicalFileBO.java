package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.dao.IPhysicalFileDAO;
import gov.energy.nbc.car.dao.PhysicalFileDAO;
import gov.energy.nbc.car.dao.dto.FileAsRawBytes;
import gov.energy.nbc.car.dao.dto.StoredFile;
import gov.energy.nbc.car.dao.exception.UnableToDeleteFile;
import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.utilities.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nbc.car.utilities.fileReader.IDatasetReader_AllFileTypes;

import java.io.File;

public class PhysicalFileBO implements IPhysicalFileBO {

    protected IPhysicalFileDAO physicalFileDAO;

    protected IDatasetReader_AllFileTypes generalFileReader;

    public PhysicalFileBO(ISettings settings) {

        physicalFileDAO = new PhysicalFileDAO(settings);
        generalFileReader = new DatasetReader_AllFileTypes();
    }

//    @Override
//    public StoredFile saveFile(MultipartFile dataFile) {
//
//        StoredFile theDataFileThatWasStored = null;
//        try {
//            theDataFileThatWasStored = getDataFileDAO().saveFile(dataFile);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return theDataFileThatWasStored;
//    }

    @Override
    public StoredFile saveFile(FileAsRawBytes file) {

        StoredFile theDataFileThatWasStored = null;
        try {
            theDataFileThatWasStored = getPyhsicalFileDAO().saveFile(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return theDataFileThatWasStored;
    }

    @Override
    public void deletFile(String storageLocation)
            throws UnableToDeleteFile {

        getPyhsicalFileDAO().deletFile(storageLocation);
    }

    @Override
    public File getFile(String storageLocation) {

        return getPyhsicalFileDAO().getFile(storageLocation);
    }


    @Override
    public IPhysicalFileDAO getPyhsicalFileDAO() {
        return physicalFileDAO;
    }
}
