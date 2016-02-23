package gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception;

public class FailedToExtractDataFromFile extends Exception {

    public final String fileName;

    public FailedToExtractDataFromFile(String fileName, Throwable e) {
        super(e);
        this.fileName = fileName;
    }
}
