package gov.energy.nrel.dataRepositoryApp.utilities.fileReader;

public class UnsanitaryData extends Exception {

    public String sanitizedValue;
    public int rowNumber;
    public int columnNumber;

    public UnsanitaryData(String sanitizedValue) {
        this.sanitizedValue = sanitizedValue;
    }
}
