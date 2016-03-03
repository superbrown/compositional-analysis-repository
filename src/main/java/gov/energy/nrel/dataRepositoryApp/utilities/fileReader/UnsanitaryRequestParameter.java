package gov.energy.nrel.dataRepositoryApp.utilities.fileReader;

public class UnsanitaryRequestParameter extends Exception {

    public final String paramaterName;

    public UnsanitaryRequestParameter(String paramaterName) {
        this.paramaterName = paramaterName;
    }
}
