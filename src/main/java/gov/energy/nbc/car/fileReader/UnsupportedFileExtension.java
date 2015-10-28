package gov.energy.nbc.car.fileReader;

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
