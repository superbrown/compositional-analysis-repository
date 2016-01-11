package gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception;


public class FileContainsInvalidColumnName extends Throwable {

    public Integer columnNumber;
    public Object value;

    public FileContainsInvalidColumnName(Integer columnNumber, Object value) {
        this.columnNumber = columnNumber;
        this.value = value;
    }

    @Override
    public String toString() {
        return "NonStringValueFoundInHeader{" +
                "columnNumber=" + columnNumber +
                ", value=" + value +
                '}';
    }
}
