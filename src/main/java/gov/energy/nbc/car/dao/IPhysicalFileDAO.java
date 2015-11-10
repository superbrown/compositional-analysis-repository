package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.dao.dto.FileAsRawBytes;
import gov.energy.nbc.car.dao.dto.StoredFile;
import gov.energy.nbc.car.dao.exception.CouldNoCreateDirectory;
import gov.energy.nbc.car.dao.exception.UnableToDeleteFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface IPhysicalFileDAO {

    StoredFile saveFile(MultipartFile multipartFile)
            throws CouldNoCreateDirectory, IOException;

    StoredFile saveFile(FileAsRawBytes file)
                    throws CouldNoCreateDirectory, IOException;

    void deletFile(String file)
                            throws UnableToDeleteFile;

    File getFile(String storageLocation);
}
