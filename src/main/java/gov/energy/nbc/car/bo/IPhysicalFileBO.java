package gov.energy.nbc.car.bo;

import gov.energy.nbc.car.dao.IPhysicalFileDAO;
import gov.energy.nbc.car.dao.dto.FileAsRawBytes;
import gov.energy.nbc.car.dao.dto.StoredFile;
import gov.energy.nbc.car.dao.exception.UnableToDeleteFile;

import java.io.File;

public interface IPhysicalFileBO {

//    StoredFile saveFile(MultipartFile dataFile);

    StoredFile saveFile(FileAsRawBytes file);

    IPhysicalFileDAO getPyhsicalFileDAO();

    void deletFile(String storageLocation)
            throws UnableToDeleteFile;

    File getFile(String storageLocation);
}
