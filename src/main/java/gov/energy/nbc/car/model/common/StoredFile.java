package gov.energy.nbc.car.model.common;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import gov.energy.nbc.car.model.AbstractBasicDBObject;

public class StoredFile extends AbstractBasicDBObject {

    public static final String ATTRIBUTE_KEY__ORIGINAL_FILE_NAME = "originalFileName";
    public static final String ATTRIBUTE_KEY__STORAGE_LOCATION = "storageLocation";

    public StoredFile(Object object) {
        super(object);
    }

    public StoredFile(String json) {
        super(json);
    }

    public StoredFile(String originalFileName, String storageLocation) {
        init(originalFileName, storageLocation);
    }

    @Override
    public void init(String json) {

        BasicDBObject parsedJson = (BasicDBObject) JSON.parse(json);

        init((String)parsedJson.get(ATTRIBUTE_KEY__ORIGINAL_FILE_NAME),
             (String)parsedJson.get(ATTRIBUTE_KEY__STORAGE_LOCATION));
    }

    public void init(String originalFileName, String storageLocation) {

        put(ATTRIBUTE_KEY__ORIGINAL_FILE_NAME, originalFileName);
        put(ATTRIBUTE_KEY__STORAGE_LOCATION, storageLocation);
    }
}
