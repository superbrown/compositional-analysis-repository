package gov.energy.nrel.dataRepositoryApp.dao.dto;


import gov.energy.nrel.dataRepositoryApp.model.common.IStoredFile;

public class StoredFile implements IStoredFile {

    public String originalFileName;
    public String storageLocation;

    public StoredFile(String originalFileName, String storageLocation) {
        this.originalFileName = originalFileName;
        this.storageLocation = storageLocation;
    }

    @Override
    public String getOriginalFileName() {
        return originalFileName;
    }

    @Override
    public String getStorageLocation() {
        return storageLocation;
    }
}
