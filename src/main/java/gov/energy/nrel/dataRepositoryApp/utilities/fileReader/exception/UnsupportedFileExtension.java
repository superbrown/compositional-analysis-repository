package gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception;

public class UnsupportedFileExtension extends Exception {

    private final String fileName;

    public UnsupportedFileExtension(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "UnsupportedFileExtension{" +
                "fileName='" + fileName + '\'' +
                '}';
    }
}
