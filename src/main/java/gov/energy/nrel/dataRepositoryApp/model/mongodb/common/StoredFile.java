package gov.energy.nrel.dataRepositoryApp.model.mongodb.common;

import com.mongodb.BasicDBObject;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.AbstractDocument;
import gov.energy.nrel.dataRepositoryApp.model.IStoredFile;
import org.bson.Document;

public class StoredFile extends AbstractDocument implements IStoredFile {

    public static final String MONGO_KEY__ORIGINAL_FILE_NAME = " originalFileName";
    public static final String MONGO_KEY__STORAGE_LOCATION = " storageLocation";

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

        init((String)document.get(MONGO_KEY__ORIGINAL_FILE_NAME),
             (String)document.get(MONGO_KEY__STORAGE_LOCATION));
    }

    public void init(String originalFileName, String storageLocation) {

        put(MONGO_KEY__ORIGINAL_FILE_NAME, originalFileName);
        put(MONGO_KEY__STORAGE_LOCATION, storageLocation);
    }

    @Override
    public String getOriginalFileName() {
        return (String) get(MONGO_KEY__ORIGINAL_FILE_NAME);
    }

    @Override
    public String getStorageLocation() {
        return (String) get(MONGO_KEY__STORAGE_LOCATION);
    }
}
