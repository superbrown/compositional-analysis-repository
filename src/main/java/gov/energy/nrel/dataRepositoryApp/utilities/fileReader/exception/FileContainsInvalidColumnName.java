package gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception;


public class FileContainsInvalidColumnName extends Exception {

    public String fileName;
    public Integer columnNumber;
    public Object columnName;

    public FileContainsInvalidColumnName(String fileName, Integer columnNumber, Object columnName) {
        this.fileName = fileName;
        this.columnNumber = columnNumber;
        this.columnName = columnName;
    }

    public FileContainsInvalidColumnName(int columnNumber, Object columnName) {

        this.columnNumber = columnNumber;
        this.columnName = columnName;
    }
}
