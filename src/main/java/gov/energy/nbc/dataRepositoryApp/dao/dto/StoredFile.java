package gov.energy.nbc.car.dao.dto;


public class StoredFile {

    public String originalFileName;
    public String storageLocation;

    public StoredFile(String originalFileName, String storageLocation) {
        this.originalFileName = originalFileName;
        this.storageLocation = storageLocation;
    }
}
