package gov.energy.nbc.spreadsheet.fileReader;

public class UnsupportedFileExtension extends Exception {

    public UnsupportedFileExtension(String fileName) {

        super(fileName);
    }
}
