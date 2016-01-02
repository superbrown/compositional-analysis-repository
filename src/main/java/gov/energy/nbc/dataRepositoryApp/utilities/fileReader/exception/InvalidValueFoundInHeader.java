package gov.energy.nbc.car.utilities.fileReader.exception;


public class InvalidValueFoundInHeader extends Throwable {

    public Integer columnNumber;
    public Object value;

    public InvalidValueFoundInHeader(Integer columnNumber, Object value) {
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
