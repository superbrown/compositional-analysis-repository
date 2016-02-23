package gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception;

public class NotAnExcelWorkbook extends Exception {

    public final String fileName;

    public NotAnExcelWorkbook(String fileName) {
        this.fileName = fileName;
    }
}
