package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.dao.dto.FileAsRawBytes;
import gov.energy.nbc.car.dao.dto.StoredFile;
import gov.energy.nbc.car.dao.IPhysicalFileDAO;
import gov.energy.nbc.car.dao.exception.UnableToDeleteFile;
import org.springframework.web.multipart.MultipartFile;

public interface IPhysicalFileBO {

    StoredFile saveFile(TestMode testMode, MultipartFile dataFile);

    StoredFile saveFile(TestMode testMode, FileAsRawBytes file);

    IPhysicalFileDAO getDataFileDAO(TestMode testMode);

    void deletFile(String storageLocation)
            throws UnableToDeleteFile;
}
