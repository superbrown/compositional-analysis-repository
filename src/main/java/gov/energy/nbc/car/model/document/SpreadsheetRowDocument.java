package gov.energy.nbc.car.model.document;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import gov.energy.nbc.car.model.common.SpreadsheetRow;
import gov.energy.nbc.car.model.AbstractDocument;
import gov.energy.nbc.car.model.common.Metadata;
import org.bson.types.ObjectId;

public class SpreadsheetRowDocument extends AbstractDocument {

    public static final String ATTRIBUTE_KEY__SPREADSHEET_OBJECT_ID = "spreadsheetObjectId";
    public static final String ATTRIBUTE_KEY__SPREADSHEET_METADATA = "metadata";
    public static final String ATTRIBUTE_KEY__DATA = "data";

    public SpreadsheetRowDocument(ObjectId speadsheetObjectId, Metadata metadata, SpreadsheetRow spreadsheetRow) {

        init(speadsheetObjectId, metadata, spreadsheetRow);
    }

    public SpreadsheetRowDocument(Object object) {
        super(object);
    }

    public SpreadsheetRowDocument(String json) {
        super(json);
    }

    private void init(ObjectId speadsheetObjectId, Metadata metadata, SpreadsheetRow spreadsheetRow) {

        put(ATTRIBUTE_KEY__SPREADSHEET_OBJECT_ID, speadsheetObjectId);
        put(ATTRIBUTE_KEY__SPREADSHEET_METADATA, metadata);
        put(ATTRIBUTE_KEY__DATA, spreadsheetRow);
    }

    @Override
    public void init(String json) {

        BasicDBObject parsedJson = (BasicDBObject) JSON.parse(json);

        put(ATTRIBUTE_KEY__ID, parsedJson.getObjectId(ATTRIBUTE_KEY__ID));
        ObjectId spreadsheetObjectId = (ObjectId) parsedJson.get(ATTRIBUTE_KEY__SPREADSHEET_OBJECT_ID);
        Metadata metada = new Metadata(parsedJson.get(ATTRIBUTE_KEY__SPREADSHEET_METADATA));
        SpreadsheetRow spreadsheetRow = new SpreadsheetRow(parsedJson.get(ATTRIBUTE_KEY__DATA));

        init(spreadsheetObjectId, metada, spreadsheetRow);
    }
}
