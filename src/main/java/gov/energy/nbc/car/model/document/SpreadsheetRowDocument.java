package gov.energy.nbc.car.model.document;

import gov.energy.nbc.car.model.AbstractDocument;
import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.common.SpreadsheetRow;
import org.bson.Document;
import org.bson.types.ObjectId;

public class SpreadsheetRowDocument extends AbstractDocument {

    public static final String ATTRIBUTE_KEY__SPREADSHEET_OBJECT_ID = "spreadsheetObjectId";
    public static final String ATTRIBUTE_KEY__SPREADSHEET_METADATA = "metadata";
    public static final String ATTRIBUTE_KEY__DATA = "data";

    public SpreadsheetRowDocument(ObjectId speadsheetObjectId, Metadata metadata, SpreadsheetRow spreadsheetRow) {

        init(speadsheetObjectId, metadata, spreadsheetRow);
    }

    public SpreadsheetRowDocument(Document object) {
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

    protected void init(Document document) {

        if (document == null) {
            return;
        }

        initializeId(document);

        ObjectId spreadsheetObjectId = (ObjectId) document.get(ATTRIBUTE_KEY__SPREADSHEET_OBJECT_ID);
        Metadata metada = new Metadata((Document) document.get(ATTRIBUTE_KEY__SPREADSHEET_METADATA));
        SpreadsheetRow spreadsheetRow = new SpreadsheetRow(document.get(ATTRIBUTE_KEY__DATA));

        init(spreadsheetObjectId, metada, spreadsheetRow);
    }
}
