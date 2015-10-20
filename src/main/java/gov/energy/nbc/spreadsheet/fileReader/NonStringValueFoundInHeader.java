package gov.energy.nbc.spreadsheet.fileReader;


public class NonStringValueFoundInHeader extends Throwable {

    public Object value;

    public NonStringValueFoundInHeader(int columnNumber, Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "NonStringValueFoundInHeader{" +
                "value=" + value +
                '}';
    }
}
