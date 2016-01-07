package gov.energy.nrel.dataRepositoryApp.dao.mongodb.singleCellCollectionApproach;


import com.mongodb.client.result.DeleteResult;
import gov.energy.nrel.dataRepositoryApp.dao.ICellDAO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsDAO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.MongoFieldNameEncoder;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.dto.DeleteResults;
import gov.energy.nrel.dataRepositoryApp.model.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.IMetadata;
import gov.energy.nrel.dataRepositoryApp.model.IRow;
import gov.energy.nrel.dataRepositoryApp.model.IRowDocument;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.common.Metadata;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.document.CellDocument;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import gov.energy.nrel.dataRepositoryApp.settings.Settings;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class s_CellDAO extends AbsDAO implements ICellDAO {

    public static final String COLLECTION_NAME = "cell";

    public s_CellDAO(String collectionName, Settings settings) {
        super(collectionName, settings);
    }

    public s_CellDAO(ISettings settings) {
        super(COLLECTION_NAME, settings);
    }


    public void add(ObjectId datasetId, IDatasetDocument datasetDocument, ObjectId rowId, IRow row) {

        List<Document> cellDocuments = new ArrayList<>();

        for (String columnName : row.getColumnNames()) {

            Object value = row.getValue(columnName);

            // DESIGN NOTE: I don't know why we need to create a wrapper here, but if we don't, Mongo throws the
            // following exception:
            //
            // org.bson.codecs.configuration.CodecConfigurationException: Can't find a codec for class
            // gov.energy.nbc.dataset.model.document.CellDocument.

            cellDocuments.add(new Document(new CellDocument(rowId, columnName, value)));
        }

        IMetadata metadata = datasetDocument.getMetadata();

        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, Metadata.MONGO_KEY__DATA_CATEGORY, metadata.getDataCategory()));
        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, IDatasetDocument.DISPLAY_FIELD__SOURCE_UUID, datasetId));
        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, IRowDocument.DISPLAY_FIELD__ROW_UUID, rowId.toHexString()));
        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, Metadata.MONGO_KEY__SOURCE_DOCUMENT, metadata.getSourceDocument().getOriginalFileName()));
        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, Metadata.MONGO_KEY__SUB_DOCUMENT_NAME, metadata.getSubdocumentName()));
        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, Metadata.MONGO_KEY__SUBMISSION_DATE, metadata.getSubmissionDate()));
        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, Metadata.MONGO_KEY__SUBMITTER, metadata.getSubmitter()));
        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, Metadata.MONGO_KEY__PROJECT_NAME, metadata.getProjectName()));
        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, Metadata.MONGO_KEY__CHARGE_NUMBER, metadata.getChargeNumber()));
        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, Metadata.MONGO_KEY__COMMENTS, metadata.getComments()));

        addMany(cellDocuments);
    }

    protected Document toMongoFieldNameEncodedDocument(ObjectId rowId, String name, Object value) {

        return new Document(
                new CellDocument(
                        rowId,
                        MongoFieldNameEncoder.toMongoSafeFieldName(name),
                        value));
    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(Document document) {

        return new CellDocument(document);
    }

    public IDeleteResults deleteCellsAssociatedWithRow(ObjectId rowId) {

        DeleteResults allDeleteResults = new DeleteResults();

        Document rowIdFilter = new Document().
                append(CellDocument.MONGO_KEY__ROW_ID, rowId);

        DeleteResult deleteResult = getCollection().deleteMany(rowIdFilter);

        allDeleteResults.add(deleteResult);

        return allDeleteResults;
    }
}
