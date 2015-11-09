package gov.energy.nbc.car.businessObject;

import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.businessObject.dto.FileAsRawBytes;
import gov.energy.nbc.car.businessObject.dto.StoredFile;
import gov.energy.nbc.car.dao.mongodb.PhysicalFileDAO;
import gov.energy.nbc.car.dao.mongodb.exception.UnableToDeleteFile;
import gov.energy.nbc.car.fileReader.FileReader;
import org.springframework.web.multipart.MultipartFile;

public class PhysicalFileBO implements IPhysicalFileBO {

    protected PhysicalFileDAO physicalFileDAO;
    protected PhysicalFileDAO physicalFileDAO_FOR_UNIT_TESTING_PURPOSES;

    protected FileReader fileReader;

    public PhysicalFileBO(Settings settings,
                          Settings settings_forUnitTestingPurposes) {

        physicalFileDAO = new PhysicalFileDAO(settings);
        physicalFileDAO_FOR_UNIT_TESTING_PURPOSES = new PhysicalFileDAO(settings_forUnitTestingPurposes);

        fileReader = new FileReader();
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
    public PhysicalFileDAO getDataFileDAO(TestMode testMode) {

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
