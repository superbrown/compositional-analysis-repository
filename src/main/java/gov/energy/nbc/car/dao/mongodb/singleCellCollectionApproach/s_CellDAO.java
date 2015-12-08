package gov.energy.nbc.car.dao.mongodb.singleCellCollectionApproach;


import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.car.dao.ICellDAO;
import gov.energy.nbc.car.dao.dto.IDeleteResults;
import gov.energy.nbc.car.dao.mongodb.DAO;
import gov.energy.nbc.car.dao.mongodb.MongoFieldNameEncoder;
import gov.energy.nbc.car.dao.mongodb.dto.DeleteResults;
import gov.energy.nbc.car.model.IMetadata;
import gov.energy.nbc.car.model.IRow;
import gov.energy.nbc.car.model.mongodb.document.CellDocument;
import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.settings.Settings;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class s_CellDAO extends DAO implements ICellDAO {

    public static final String COLLECTION_NAME = "cell";

    public s_CellDAO(String collectionName, Settings settings) {
        super(collectionName, settings);
    }

    public s_CellDAO(ISettings settings) {
        super(COLLECTION_NAME, settings);
    }


    public void add(ObjectId rowId, IMetadata metadata, IRow row) {

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

        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, metadata.ATTR_KEY__DATA_CATEGORY, metadata.getDataCategory()));
        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, metadata.ATTR_KEY__PROJECT_NAME, metadata.getProjectName()));
        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, metadata.ATTR_KEY__CHARGE_NUMBER, metadata.getChargeNumber()));
        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, metadata.ATTR_KEY__SUBMITTER, metadata.getSubmitter()));
        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, metadata.ATTR_KEY__SUBMISSION_DATE, metadata.getSubmissionDate()));
        cellDocuments.add(toMongoFieldNameEncodedDocument(rowId, metadata.ATTR_KEY__COMMENTS, metadata.getComments()));

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
                append(CellDocument.ATTR_KEY__ROW_ID, rowId);

        DeleteResult deleteResult = getCollection().deleteMany(rowIdFilter);

        allDeleteResults.add(deleteResult);

        return allDeleteResults;
    }
}
