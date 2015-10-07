package gov.energy.nbc.spreadsheet.dao;

import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.spreadsheet.documents.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class SpreadsheetRowDAO extends DAO
{
    public SpreadsheetRowDAO() {

        super("spreadsheetRow", "spreadsheetRow");
    }

    public List<ObjectId> add(ObjectId spreadsheetObjectId, Spreadsheet spreadsheet) {

        List<ObjectId> objectIds = new ArrayList();

        Metadata metadata = spreadsheet.getMetadata();

        for (SpreadsheetRowData spreadsheetRowData : spreadsheet.getSpreadsheetRowDataList()) {

            SpreadsheetRow spreadsheetRow = new SpreadsheetRow(
                    spreadsheetObjectId,
                    metadata,
                    spreadsheetRowData);

            Document document = encloseInADocumentWithTypeKey(spreadsheetRow);

            ObjectId objectId = insert(document);
            objectIds.add(objectId);
        }

        return objectIds;
    }

    public DeleteResult deleteRowsAssociatedWithSpreadsheet(ObjectId objectId) {

        Document spreadsheetIdFilter = new Document();
        spreadsheetIdFilter.put(
                getAttributeKey() + "." + SpreadsheetRow.ATTRIBUTE_KEY__SPREADSHEET_OBJECT_ID,
                objectId);

        DeleteResult deleteResult = getCollection().deleteMany(spreadsheetIdFilter);

        return deleteResult;
    }

    @Override
    public DeleteResult delete(ObjectId objectId) {

        throw new RuntimeException(
                "This method should not be called because rows should not be deleted  independently of " +
                        "their spreasheet.");
    }
}
