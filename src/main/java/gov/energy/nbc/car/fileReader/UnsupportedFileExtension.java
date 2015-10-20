package gov.energy.nbc.car.fileReader;

public class UnsupportedFileExtension extends Exception {

    public UnsupportedFileExtension(String fileName) {

        super(fileName);
    }
}
