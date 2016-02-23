package gov.energy.nrel.dataRepositoryApp.utilities.fileReader;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public abstract class AbsDatasetReader {

    abstract boolean canReadFileWithExtension(String filename);

    public boolean containsData(List<Object> data) {

        for (Object dataElement : data) {

            if (dataElement instanceof String) {

                if (StringUtils.isNotBlank((String) dataElement)) {
                    return true;
                }
            }
            else if (dataElement != null) {
                return true;
            }
        }

        return false;
    }
}
