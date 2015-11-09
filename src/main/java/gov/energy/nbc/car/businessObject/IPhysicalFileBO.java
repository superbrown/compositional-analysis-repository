package gov.energy.nbc.car.businessObject;

import gov.energy.nbc.car.businessObject.dto.FileAsRawBytes;
import gov.energy.nbc.car.businessObject.dto.StoredFile;
import gov.energy.nbc.car.dao.mongodb.PhysicalFileDAO;
import gov.energy.nbc.car.dao.mongodb.exception.UnableToDeleteFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by mbrown on 11/8/2015.
 */
public interface IPhysicalFileBO {
    StoredFile saveFile(TestMode testMode, MultipartFile dataFile);

    StoredFile saveFile(TestMode testMode, FileAsRawBytes file);

    PhysicalFileDAO getDataFileDAO(TestMode testMode);

    void deletFile(String storageLocation)
            throws UnableToDeleteFile;
}
