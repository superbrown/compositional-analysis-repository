package gov.energy.nbc.car.dao;

import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.model.common.Data;
import gov.energy.nbc.car.model.document.SampleTypeDocument;
import gov.energy.nbc.car.model.document.SpreadsheetDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Set;

public class SpreadsheetDocumentDAO extends DAO
{
    public static final String ATTRIBUTE_KEY__COLLECTION_NAME = "spreadsheet";
    protected SpreadsheetRowDocumentDAO spreadsheetRowDocumentDAO;
    protected SampleTypeDocumentDAO sampleTypeDocumentDAO;

    public SpreadsheetDocumentDAO(Settings settings) {

        super(ATTRIBUTE_KEY__COLLECTION_NAME, settings);

        spreadsheetRowDocumentDAO = new SpreadsheetRowDocumentDAO(settings);
        sampleTypeDocumentDAO = new SampleTypeDocumentDAO(settings);
    }

    public SpreadsheetDocument getSpreadsheet(String id) {

        SpreadsheetDocument metadata = (SpreadsheetDocument) queryForOneWithId(id);
        return metadata;
    }

    public ObjectId add(SpreadsheetDocument spreadsheetDocument, Data data) {

        ObjectId objectId = insert(spreadsheetDocument);
        spreadsheetRowDocumentDAO.add(objectId, spreadsheetDocument, data);

        makeSureCollectionIsIndexedForAllColumns(data);

        String sampleType = spreadsheetDocument.getSampleType();
        Set columnNames = data.getColumnNames();

        associateColumnNamesToTheSampleType(sampleType, columnNames);

        return objectId;
    }

    protected void associateColumnNamesToTheSampleType(String sampleType, Set columnNames) {

        SampleTypeDocument sampleTypeDocument = sampleTypeDocumentDAO.getByName(sampleType);

        if (sampleTypeDocument == null) {

            sampleTypeDocument = new SampleTypeDocument();
            sampleTypeDocument.setSampleType(sampleType);
            ObjectId objectId = sampleTypeDocumentDAO.add(sampleTypeDocument);
            sampleTypeDocument = sampleTypeDocumentDAO.get(objectId);
        }

        Set<String> columnNamesFromTheDatabase = sampleTypeDocument.getColumnNames();
        columnNamesFromTheDatabase.addAll(columnNames);

        Document dataToBeUpdated = new Document().
                append(SampleTypeDocument.ATTRIBUTE_KEY__COLUMN_NAMES, columnNamesFromTheDatabase);

        sampleTypeDocumentDAO.updateOne(sampleTypeDocument.getObjectId(), dataToBeUpdated);
    }

    @Override
    public DeleteResults delete(ObjectId objectId) {

        DeleteResults deleteResults = super.delete(objectId);

        if (deleteResults.wasAcknowledged() == false) {
            return deleteResults;
        }

        DeleteResults deleteResultsForRows = spreadsheetRowDocumentDAO.deleteRowsAssociatedWithSpreadsheet(objectId);
        deleteResults.addAll(deleteResultsForRows);

        return deleteResults;
    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(Document document) {

        return new SpreadsheetDocument(document);
    }

    @Override
    public DeleteResult removeAllDataFromCollection() {

        this.getAll();

        DeleteResult deleteResult = super.removeAllDataFromCollection();
        if (deleteResult.wasAcknowledged() == false) {
            return deleteResult;
        }

        // This makes sure all data from the spreadsheet rows collection are also deleted.
        return spreadsheetRowDocumentDAO.removeAllDataFromCollection();
    }

    public SpreadsheetRowDocumentDAO getSpreadsheetRowDocumentDAO() {
        return spreadsheetRowDocumentDAO;
    }

    public SampleTypeDocumentDAO getSampleTypeDocumentDAO() {
        return sampleTypeDocumentDAO;
    }

    private void makeSureCollectionIsIndexedForAllColumns(Data data) {
//        SpreadsheetRow firstRow = data.get(0);
//        spreadsheetRowDocumentDAO.getCollection().createIndex(firstRow);
    }
}
