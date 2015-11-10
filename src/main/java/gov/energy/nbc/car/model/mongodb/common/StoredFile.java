package gov.energy.nbc.car.model.mongodb.common;

import com.mongodb.BasicDBObject;
import gov.energy.nbc.car.model.mongodb.AbstractDocument;
import gov.energy.nbc.car.model.IStoredFile;
import org.bson.Document;

public class StoredFile extends AbstractDocument implements IStoredFile {

    public static final String ATTR_KEY__ORIGINAL_FILE_NAME = "originalFileName";
    public static final String ATTR_KEY__STORAGE_LOCATION = "storageLocation";

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

        initObjectId(document);

        init((String)document.get(ATTR_KEY__ORIGINAL_FILE_NAME),
             (String)document.get(ATTR_KEY__STORAGE_LOCATION));
    }

    public void init(String originalFileName, String storageLocation) {

        put(ATTR_KEY__ORIGINAL_FILE_NAME, originalFileName);
        put(ATTR_KEY__STORAGE_LOCATION, storageLocation);
    }

    @Override
    public String getOriginalFileName() {
        return (String) get(ATTR_KEY__ORIGINAL_FILE_NAME);
    }

    @Override
    public String getStorageLocation() {
        return (String) get(ATTR_KEY__STORAGE_LOCATION);
    }
}
