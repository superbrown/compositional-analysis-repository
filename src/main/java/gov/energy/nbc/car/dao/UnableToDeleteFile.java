package gov.energy.nbc.car.dao;

public class UnableToDeleteFile extends Throwable {

    private final String file;

    public UnableToDeleteFile(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "UnableToDeleteFile{" +
                "file='" + file + '\'' +
                '}';
    }
}
