package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.bo.dto.FileAsRawBytes;
import gov.energy.nbc.car.bo.dto.StoredFile;
import gov.energy.nbc.car.dao.IPhysicalFileDAO;
import gov.energy.nbc.car.dao.mongodb.PhysicalFileDAO;
import gov.energy.nbc.car.dao.exception.UnableToDeleteFile;
import gov.energy.nbc.car.fileReader.DatasetReader_AllFileTypes;
import gov.energy.nbc.car.fileReader.IDatasetReader_AllFileTypes;
import org.springframework.web.multipart.MultipartFile;

public class PhysicalFileBO implements IPhysicalFileBO {

    protected IPhysicalFileDAO physicalFileDAO;
    protected IPhysicalFileDAO physicalFileDAO_FOR_UNIT_TESTING_PURPOSES;

    protected IDatasetReader_AllFileTypes generalFileReader;

    public PhysicalFileBO(Settings settings,
                          Settings settings_forUnitTestingPurposes) {

        physicalFileDAO = new PhysicalFileDAO(settings);
        physicalFileDAO_FOR_UNIT_TESTING_PURPOSES = new PhysicalFileDAO(settings_forUnitTestingPurposes);

        generalFileReader = new DatasetReader_AllFileTypes();
    }

    @Override
    public StoredFile saveFile(TestMode testMode, MultipartFile dataFile) {

        StoredFile theDataFileThatWasStored = null;
        try {
            theDataFileThatWasStored = getDataFileDAO(testMode).saveFile(dataFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return theDataFileThatWasStored;
    }

    @Override
    public StoredFile saveFile(TestMode testMode, FileAsRawBytes file) {

        StoredFile theDataFileThatWasStored = null;
        try {
            theDataFileThatWasStored = getDataFileDAO(testMode).saveFile(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return theDataFileThatWasStored;
    }


    @Override
    public IPhysicalFileDAO getDataFileDAO(TestMode testMode) {

        if (testMode == TestMode.NOT_TEST_MODE) {
            return physicalFileDAO;
        }
        else {
            return physicalFileDAO_FOR_UNIT_TESTING_PURPOSES;
        }
    }

    @Override
    public void deletFile(String storageLocation)
            throws UnableToDeleteFile {

        physicalFileDAO.deletFile(storageLocation);
    }
}
