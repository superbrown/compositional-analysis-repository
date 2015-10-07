package gov.energy.nbc.spreadsheet.documents;

import org.bson.Document;
import org.bson.types.ObjectId;

public class SpreadsheetRow extends Document {

    public static final String ATTRIBUTE_KEY__SPREADSHEET_METADATA = "metadata";
    public static final String ATTRIBUTE_KEY__SPREADSHEET_OBJECT_ID = "spreadsheetObjectId";
    public static final String ATTRIBUTE_KEY__DATA = "data";

    public SpreadsheetRow(ObjectId speadsheetObjectId, Metadata metadata, SpreadsheetRowData spreadsheetRowData) {

        put(ATTRIBUTE_KEY__SPREADSHEET_OBJECT_ID, speadsheetObjectId);
        put(ATTRIBUTE_KEY__SPREADSHEET_METADATA, metadata);
        put(ATTRIBUTE_KEY__DATA, spreadsheetRowData);
    }
}
