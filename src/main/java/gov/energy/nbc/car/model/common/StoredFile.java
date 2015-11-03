package gov.energy.nbc.car.model.common;

import com.mongodb.BasicDBObject;
import gov.energy.nbc.car.model.AbstractDocument;
import org.bson.Document;

public class StoredFile extends AbstractDocument {

    public static final String ATTRIBUTE_KEY__ORIGINAL_FILE_NAME = "originalFileName";
    public static final String ATTRIBUTE_KEY__STORAGE_LOCATION = "storageLocation";

    public StoredFile(Document document) {
        super(document);
    }

    public StoredFile(BasicDBObject document) {
        super(document);
    }

    public StoredFile(String json) {
        super(json);
    }

    public StoredFile(String originalFileName, String storageLocation) {
        init(originalFileName, storageLocation);
    }

    public void init(Document document) {

        if (document == null) {
            return;
        }

        init((String)document.get(ATTRIBUTE_KEY__ORIGINAL_FILE_NAME),
             (String)document.get(ATTRIBUTE_KEY__STORAGE_LOCATION));
    }

    public void init(String originalFileName, String storageLocation) {

        put(ATTRIBUTE_KEY__ORIGINAL_FILE_NAME, originalFileName);
        put(ATTRIBUTE_KEY__STORAGE_LOCATION, storageLocation);
    }

    public String getOriginalFileName() {
        return (String) get(ATTRIBUTE_KEY__ORIGINAL_FILE_NAME);
    }

    public String getStorageLocation() {
        return (String) get(ATTRIBUTE_KEY__STORAGE_LOCATION);
    }
}
