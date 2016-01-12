package gov.energy.nrel.dataRepositoryApp.bo;


import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;

import java.io.IOException;
import java.util.List;

public interface IUtilsBO extends IBO {

    List<String> getNamesOfSheetsWithinWorkbook(String fileName, FileAsRawBytes fileAsRawBytes)
            throws IOException, UnsupportedFileExtension;

    void dropDatabase();

    List<String> repopulateDatabaseUsingFilesStoredOnServer();
}
