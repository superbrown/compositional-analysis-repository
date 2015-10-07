package gov.energy.nbc.spreadsheet.dao;

import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.spreadsheet.documents.Spreadsheet;
import org.bson.Document;
import org.bson.types.ObjectId;

public class SpreadsheetDAO extends DAO
{
    protected SpreadsheetRowDAO spreadsheetRowDAO = new SpreadsheetRowDAO();

    public SpreadsheetDAO() {

        super("spreadsheet", "spreadsheet");
    }

    public ObjectId add(Spreadsheet spreadsheet) {

        Document document = encloseInADocumentWithTypeKey(spreadsheet);

        ObjectId objectId = insert(document);
        spreadsheetRowDAO.add(objectId, spreadsheet);

        return objectId;
    }

    @Override
    public DeleteResult delete(ObjectId objectId) {

        DeleteResult deleteResult = super.delete(objectId);
        if (deleteResult.wasAcknowledged() == false) {
            return deleteResult;
        }

        return spreadsheetRowDAO.deleteRowsAssociatedWithSpreadsheet(objectId);
    }

    @Override
    public DeleteResult deleteAll() {

        DeleteResult deleteResult = super.deleteAll();
        if (deleteResult.wasAcknowledged() == false) {
            return deleteResult;
        }

        // This makes sure all spreadsheet rows are also deleted.
        return spreadsheetRowDAO.deleteAll();
    }

    public SpreadsheetRowDAO getSpreadsheetRowDAO() {
        return spreadsheetRowDAO;
    }
}
