package gov.energy.nbc.car.dao.mongodb.singleCellSchemaApproach;

import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.dao.ICellDAO;
import gov.energy.nbc.car.dao.IRowDAO;
import gov.energy.nbc.car.dao.dto.IDeleteResults;
import gov.energy.nbc.car.dao.dto.RowSearchCriteria;
import gov.energy.nbc.car.dao.dto.SearchCriterion;
import gov.energy.nbc.car.dao.mongodb.DAO;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.mongodb.IMongodbDAO;
import gov.energy.nbc.car.dao.mongodb.MongoFieldNameEncoder;
import gov.energy.nbc.car.dao.mongodb.dto.DeleteResults;
import gov.energy.nbc.car.model.*;
import gov.energy.nbc.car.model.mongodb.document.CellDocument;
import gov.energy.nbc.car.model.mongodb.document.RowDocument;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

public class s_RowDAO extends DAO implements IRowDAO {

    public static final String COLLECTION_NAME = "row";

    protected ICellDAO cellDAO;

    protected Logger log = Logger.getLogger(this.getClass());

    public s_RowDAO(Settings settings) {

        super(COLLECTION_NAME, settings);

        cellDAO = new s_CellDAO(settings);
        makeSureTableColumnsIRelyUponAreIndexed();
    }

    public IRowDocument get(String id) {

        return (IRowDocument) getOneWithId(id);
    }

    public List<ObjectId> add(ObjectId datasetId, IDatasetDocument datasetDocument, IRowCollection data) {

        List<ObjectId> rowIds = new ArrayList();

        IMetadata metadata = datasetDocument.getMetadata();

        for (IRow row : data.getRows()) {

            IRowDocument rowDocument = new RowDocument(
                    datasetId,
                    metadata,
                    row);

            PerformanceLogger performanceLogger = new PerformanceLogger(log, "insert(rowDocument)");
            ObjectId rowId = add(rowDocument);
            performanceLogger.done();

            rowIds.add(rowId);

            cellDAO.add(rowId, row);
        }

        return rowIds;
    }

    public IDeleteResults deleteRowsAssociatedWithDataset(ObjectId datasetId) {

        DeleteResults allDeleteResults = new DeleteResults();

        Document datasetIdFilter = new Document().
                append(RowDocument.ATTR_KEY__DATASET_ID, datasetId);

        Bson projection = fields(include(RowDocument.ATTR_KEY__ID));

        List<Document> rowsAssociatedWithDataset = this.query(datasetIdFilter, projection);

        for (Document document : rowsAssociatedWithDataset) {

            ObjectId rowId = (ObjectId) document.get(RowDocument.ATTR_KEY__ID);

            // delete all cells associated with the row
            IDeleteResults deleteResults = cellDAO.deleteCellsAssociatedWithRow(rowId);
            allDeleteResults.add((DeleteResult) deleteResults);

            // delete the row
            Document idFilter = createIdFilter(rowId);
            DeleteResult deleteResult = getCollection().deleteOne(idFilter);

            allDeleteResults.add(deleteResult);
        }

        return allDeleteResults;
    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(Document document) {

        return new RowDocument(document);
    }

    @Override
    public IDeleteResults delete(ObjectId objectId) {

        throw new RuntimeException(
                "This method should not be called because rows should not be deleted independently of " +
                        "their spreasheet.");
    }

    public List<Document> query(String query, String projection) {

        Bson bson_query = (Bson)DAOUtilities.parse(query);
        Bson bson_projection = (Bson) DAOUtilities.parse(projection);
        return query(bson_query, bson_projection);
    }

    public List<Document> query(Bson bson, Bson projection) {

        List<Document> documents = get(bson, projection);
        return documents;
    }

    public List<Document> query(RowSearchCriteria searchCriteria) {

        List<SearchCriterion> searchCriteria_metadata = searchCriteria.getMetadataSearchCriteria();
        List<SearchCriterion> searchCriteria_data = searchCriteria.getDataSearchCriteria();

//        CriterionAndItsNumberOfMatches criterionWithTheFewestMatches_metadata = null;
        CriterionAndItsNumberOfMatches criterionWithTheFewestMatches_data = null;

        if (searchCriteria_data.isEmpty() == false) {
            criterionWithTheFewestMatches_data = determineCriterionWithTheFewestMatches(searchCriteria_data);
        }

        if (criterionWithTheFewestMatches_data.getNubmerOfMatches() == 0) {
            // this means there can be no matches
            return new ArrayList();
        }

//        if (searchCriteria_metadata.isEmpty() == false) {
//            criterionWithTheFewestMatches_metadata =
//                    determineCriterionWithTheFewestMatches(searchCriteria_metadata);
//        }

//        long numberOfMatches_metadata = criterionWithTheFewestMatches_metadata.getNubmerOfMatches();
//        long numberOfMatches_data = criterionWithTheFewestMatches_data.getNubmerOfMatches();
//
//        if (numberOfMatches_metadata != -1) {
//
//            if (numberOfMatches_data < numberOfMatches_metadata) {
//                new
//            }
//        }
//        else {
//
//            if (nubmerOfMatches_data < numberOfMatches_metadata) {
//
//            }
//        }
//
//        if (nubmerOfMatches_data == -1) {
//
//        }
//        else if (criterionWithTheFewestMatches_metadata.getNubmerOfMatches()){
//            if ()
//        }

        // get the row numbers of all cells that match the first criterion
        SearchCriterion criterionWithTheFewestMatches = criterionWithTheFewestMatches_data.getCriterion();
        List<SearchCriterion> searchCriteria_data_whatsLeftToDo = new ArrayList(searchCriteria_data);
        searchCriteria_data_whatsLeftToDo.remove(criterionWithTheFewestMatches);

        Set<ObjectId> matchingRowIds = getIdsOfRowsThatMatch(criterionWithTheFewestMatches);

        for (SearchCriterion searchCondition : searchCriteria_data_whatsLeftToDo) {

            // DESIGN NOTE: Each time this is called, it'll likely make the set of IDs smaller.
            matchingRowIds = getIdsOfSubsetOfRowsThatMatch(matchingRowIds, searchCondition);
        }

        for (SearchCriterion searchCondition : searchCriteria_metadata) {

            // DESIGN NOTE: Each time this is called, it'll likely make the set of IDs smaller.
            matchingRowIds = getIdsOfSubsetOfRowsThatMatch(matchingRowIds, searchCondition);
        }


        List<String> dataColumnNamesToIncludedInQueryResults = new ArrayList();

        for (SearchCriterion searchCriterion : searchCriteria_data) {
            dataColumnNamesToIncludedInQueryResults.add(searchCriterion.getName());
        }

        List<Document> rows = getRows(matchingRowIds, dataColumnNamesToIncludedInQueryResults);

        return rows;
    }

    protected List<Document> getRows(Set<ObjectId> matchingIds, List<String> dataColumnNamesToIncludedInQueryResults) {

        List<String> attributesToInclude = new ArrayList<>();
        attributesToInclude.add(RowDocument.ATTR_KEY__ID);
        attributesToInclude.add(RowDocument.ATTR_KEY__DATASET_ID);
        attributesToInclude.add(RowDocument.ATTR_KEY__METADATA);

        for (String columnIncludedInQuery : dataColumnNamesToIncludedInQueryResults) {
            attributesToInclude.add(RowDocument.ATTR_KEY__DATA + "." + toMongoSafeFieldName(columnIncludedInQuery));
        }

        Bson projection = fields(include(attributesToInclude));

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "getting rows for the matching IDs");
        List<Document> results = new ArrayList();
        for (ObjectId matchingId : matchingIds) {

            Document document = getOne(matchingId, projection);

            Document data = (Document) document.get(RowDocument.ATTR_KEY__DATA);
            convertTotoClientSideFieldName(data);

            results.add(document);
        }

        performanceLogger.done();
        log.info("[RESULTS] results.size() = " + results.size() +
                ", row.count() = " + getCollection().count() +
                ", cell.count() = " + ((IMongodbDAO)cellDAO).getCollection().count());

        return results;
    }

    protected CriterionAndItsNumberOfMatches determineCriterionWithTheFewestMatches(
            List<SearchCriterion> searchConditia) {

        long lowestNumber = -1;
        SearchCriterion criterionWithTheFewestMatches = null;

        for (SearchCriterion searchCriterion : searchConditia) {

            long count = getCountOfRowsThatMatch(searchCriterion);

            if (count == 0) {
                return new CriterionAndItsNumberOfMatches(searchCriterion, 0);
            }

            if (lowestNumber == -1 || (count < lowestNumber)) {

                lowestNumber = count;
                criterionWithTheFewestMatches = searchCriterion;
            }
        }

        return new CriterionAndItsNumberOfMatches(criterionWithTheFewestMatches, lowestNumber);
    }

    protected Set<ObjectId> getIdsOfSubsetOfRowsThatMatch(Set<ObjectId> rowIds, SearchCriterion secondCondition) {
        Set<ObjectId> matchingIds = new HashSet();

        for (ObjectId rowId : rowIds) {

            if (rowMatchesTheCriterion(rowId, secondCondition)) {
                matchingIds.add(rowId);
            }
        }
        return matchingIds;
    }

    public long getCountOfRowsThatMatch(SearchCriterion searchCriterion) {

        // CAUTION: Do NOT change the order of these, as these reflect an index set within the
        //          database.  If you do, you'll have to change the index as well.

        Bson query = and(

                eq(
                        CellDocument.ATTR_KEY__COLUMN_NAME,
                        toMongoSafeFieldName(searchCriterion.getName())),

                DAOUtilities.toCriterion(
                        CellDocument.ATTR_KEY__VALUE,
                        searchCriterion.getValue(),
                        searchCriterion.getComparisonOperator()));

        PerformanceLogger performanceLogger = new PerformanceLogger(
                log,
                "[getCountOfRowsThatMatch()] cellDAO.getCollection().count(" + query.toString() + ")",
                true);
        long count = ((IMongodbDAO)cellDAO).getCollection().count(query);
        performanceLogger.done();

        return count;
    }

    protected String toMongoSafeFieldName(String name) {

        return MongoFieldNameEncoder.toMongoSafeFieldName(name);
    }


    public Set<ObjectId> getIdsOfRowsThatMatch(SearchCriterion searchCriterion) {

        Bson rowIdCriterion = eq(CellDocument.ATTR_KEY__COLUMN_NAME, toMongoSafeFieldName(searchCriterion.getName()));

        Bson valueCriterion = DAOUtilities.toCriterion(
                CellDocument.ATTR_KEY__VALUE,
                searchCriterion.getValue(),
                searchCriterion.getComparisonOperator());

        // CAUTION: Do NOT change the order of these, as these reflect an index set within the
        //          database.  If you do, you'll have to change the index as well.
        Bson query = and(rowIdCriterion, valueCriterion);

        Bson projection = fields(include(CellDocument.ATTR_KEY__ROW_ID));

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "[getIdsOfRowsThatMatch()] cellDAO.get(" + query.toString() + ")", true);
        List<Document> documents = cellDAO.get(query, projection);
        performanceLogger.done();
        log.info("[RESULTS] results.size() = " + documents.size() +
                ", row.count() = " + getCollection().count() +
                ", cell.count() = " + ((IMongodbDAO)cellDAO).getCollection().count());

        return toSetOfRowIds(documents);
    }

    @Override
    public ICellDAO getCellDAO(String columnName) {
        return getCellDAO();
    }

    protected boolean rowMatchesTheCriterion(ObjectId rowId, SearchCriterion searchCriterion) {

        // CAUTION: Do NOT change the order of these, as these reflect an index set within the
        //          database.  If you do, you'll have to change the index as well.
        Bson query = and(

                eq(
                        CellDocument.ATTR_KEY__ROW_ID,
                        rowId),

                eq(
                        CellDocument.ATTR_KEY__COLUMN_NAME,
                        toMongoSafeFieldName(searchCriterion.getName())),

                DAOUtilities.toCriterion(
                        CellDocument.ATTR_KEY__VALUE,
                        searchCriterion.getValue(),
                        searchCriterion.getComparisonOperator()));

        Bson projection = fields(include(
                CellDocument.ATTR_KEY__ROW_ID));

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "[rowMatchesTheCriterion()] cellDAO.getOne(" + query.toString() + ")", true);
        Document document = cellDAO.getOne(query, projection);
        performanceLogger.done();

        return document != null;
    }

    private Set<ObjectId> toSetOfRowIds(List<Document> documents) {

        Set<ObjectId> objectIds = new HashSet();

        for (Document document : documents) {

            objectIds.add((ObjectId) document.get(CellDocument.ATTR_KEY__ROW_ID));
        }

        return objectIds;
    }

    public ICellDAO getCellDAO() {
        return cellDAO;
    }

    protected void convertTotoClientSideFieldName(Document document) {

        Map<String, Object> temporaryMapToAvoidConcurrentModificationOfTheDocument = new HashMap();

        for (String key : document.keySet()) {

            String clientSideName = MongoFieldNameEncoder.toClientSideFieldName(key);
            temporaryMapToAvoidConcurrentModificationOfTheDocument.put(clientSideName, document.get(key));
        }

        document.clear();

        for (String key : temporaryMapToAvoidConcurrentModificationOfTheDocument.keySet()) {

            document.put(key, temporaryMapToAvoidConcurrentModificationOfTheDocument.get(key));
        }
    }

    private class CriterionAndItsNumberOfMatches {

        private final SearchCriterion criterion;
        private final long nubmerOfMatches;

        public CriterionAndItsNumberOfMatches(SearchCriterion criterion, long nubmerOfMatches) {

            this.criterion = criterion;
            this.nubmerOfMatches = nubmerOfMatches;
        }

        public SearchCriterion getCriterion() {
            return criterion;
        }

        public long getNubmerOfMatches() {
            return nubmerOfMatches;
        }
    }

    private static boolean HAVE_MADE_SURE_TABLE_COLUMNS_ARE_INDEXED = false;

    protected void makeSureTableColumnsIRelyUponAreIndexed() {
        if (HAVE_MADE_SURE_TABLE_COLUMNS_ARE_INDEXED == false) {

            // DESIGN NOTE: Even though these indexes are on the cell collectoin, they are being set here because this
            //              is where the code exists that relies upon them. I figured if they were here, they would less
            //              likely get removed by someone who didn't realize they were used somewhere.

            MongoCollection<Document> cellCollection = ((IMongodbDAO)cellDAO).getCollection();

            cellCollection.createIndex(new BasicDBObject(CellDocument.ATTR_KEY__ROW_ID, 1));
            cellCollection.createIndex(new BasicDBObject(CellDocument.ATTR_KEY__COLUMN_NAME, 1));
            cellCollection.createIndex(new BasicDBObject(CellDocument.ATTR_KEY__VALUE, 1));

            BasicDBObject compoundIndex = new BasicDBObject();
            compoundIndex.put(CellDocument.ATTR_KEY__ROW_ID, 1);
            compoundIndex.put(CellDocument.ATTR_KEY__COLUMN_NAME, 1);
            compoundIndex.put(CellDocument.ATTR_KEY__VALUE, 1);
            cellCollection.createIndex(compoundIndex);

            compoundIndex = new BasicDBObject();
            compoundIndex.put(CellDocument.ATTR_KEY__COLUMN_NAME, 1);
            compoundIndex.put(CellDocument.ATTR_KEY__VALUE, 1);
            cellCollection.createIndex(compoundIndex);

            log.info("Row Indexes");
            ListIndexesIterable<Document> indexes = cellCollection.listIndexes();
            for (Document index : indexes) {
                log.info(index.toJson());
            }

            HAVE_MADE_SURE_TABLE_COLUMNS_ARE_INDEXED = true;
        }
    }
}
