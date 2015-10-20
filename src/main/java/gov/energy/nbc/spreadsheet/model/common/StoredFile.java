package gov.energy.nbc.spreadsheet.model.common;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import gov.energy.nbc.spreadsheet.model.AbstractBasicDBObject;

public class StoredFile extends AbstractBasicDBObject {

    public static final String ATTRIBUTE_KEY__ORIGINAL_UPLOADED_FILE_NAME = "originalUploadedFileName";
    public static final String ATTRIBUTE_KEY__FILE_NAME_USED_FOR_STORAGE = "fileNameUserForStorage";

    public StoredFile(Object object) {
        super(object);
    }

    public StoredFile(String json) {
        super(json);
    }

    public StoredFile(String originalUploadedFileName, String fileNameUserForStorage) {
        init(originalUploadedFileName, fileNameUserForStorage);
    }

    @Override
    public void init(String json) {

        BasicDBObject parsedJson = (BasicDBObject) JSON.parse(json);

        init((String)parsedJson.get(ATTRIBUTE_KEY__ORIGINAL_UPLOADED_FILE_NAME),
             (String)parsedJson.get(ATTRIBUTE_KEY__FILE_NAME_USED_FOR_STORAGE));
    }

    public void init(String originalUploadedFileName, String fileNameUserForStorage) {

        put(ATTRIBUTE_KEY__ORIGINAL_UPLOADED_FILE_NAME, originalUploadedFileName);
        put(ATTRIBUTE_KEY__FILE_NAME_USED_FOR_STORAGE, fileNameUserForStorage);
    }
}
