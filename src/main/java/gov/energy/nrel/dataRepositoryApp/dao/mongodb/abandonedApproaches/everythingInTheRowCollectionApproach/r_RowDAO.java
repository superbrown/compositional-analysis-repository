package gov.energy.nrel.dataRepositoryApp.dao.mongodb.abandonedApproaches.everythingInTheRowCollectionApproach;

import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import gov.energy.nrel.dataRepositoryApp.bo.ResultsMode;
import gov.energy.nrel.dataRepositoryApp.dao.ICellDAO;
import gov.energy.nrel.dataRepositoryApp.dao.IRowDAO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;
import gov.energy.nrel.dataRepositoryApp.dao.dto.SearchCriterion;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.AbsDAO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.IMongodbDAO;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.MongoFieldNameEncoder;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.dto.DeleteResults;
import gov.energy.nrel.dataRepositoryApp.model.*;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.document.CellDocument;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.document.RowDocument;
import gov.energy.nrel.dataRepositoryApp.model.mongodb.document.RowDocument_usingListStructure;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import gov.energy.nrel.dataRepositoryApp.utilities.PerformanceLogger;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

public class r_RowDAO extends AbsDAO implements IRowDAO {

    public static final String COLLECTION_NAME = "row";

    protected r_CellDAO cellDAO;

    protected Logger log = Logger.getLogger(this.getClass());

    public r_RowDAO(ISettings settings) {

        super(COLLECTION_NAME, settings);

        cellDAO = new r_CellDAO(settings);
        makeSureTableColumnsIRelyUponAreIndexed();
    }

    public IRowDocument get(String id) {

        return (IRowDocument) getOneWithId(id);
    }

    public List<ObjectId> add(ObjectId datasetId, IDatasetDocument datasetDocument, IRowCollection data) {

        List<ObjectId> rowIds = new ArrayList();

        IMetadata metadata = datasetDocument.getMetadata();

        for (IRow row : data.getRows()) {

            IRowDocument rowDocument = new RowDocument_usingListStructure(
                    datasetId,
                    metadata,
                    row);

//            PerformanceLogger performanceLogger = new PerformanceLogger(log, "insert(rowDocument)");
            ObjectId rowId = add(rowDocument);
//            performanceLogger.done();

            rowIds.add(rowId);

            cellDAO.add(rowId, row);
        }

        return rowIds;
    }

    public IDeleteResults deleteRowsAssociatedWithDataset(ObjectId datasetId) {

        DeleteResults allDeleteResults = new DeleteResults();

        Document datasetIdFilter = new Document().
                append(RowDocument.MONGO_KEY__DATASET_ID, datasetId);

        Bson projection = fields(include(RowDocument.MONGO_KEY__ID));

        List<Document> rowsAssociatedWithDataset = this.query(datasetIdFilter, projection);

        for (Document document : rowsAssociatedWithDataset) {

            ObjectId rowId = (ObjectId) document.get(RowDocument.MONGO_KEY__ID);

            // delete all cells associated with the row
            allDeleteResults.addAll(cellDAO.deleteCellsAssociatedWithRow(rowId));

            // delete the row
            allDeleteResults.addAll(deleteRow(rowId));
        }

        return allDeleteResults;
    }

    private IDeleteResults deleteRow(ObjectId rowId) {

        Document idFilter = createIdFilter(rowId);

        DeleteResult deleteResult = getCollection().deleteOne(idFilter);

        DeleteResults deleteResults = new DeleteResults(deleteResult);

        return deleteResults;

    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(Document document) {
        return new RowDocument_usingListStructure(document);
    }

    @Override
    public IDeleteResults delete(ObjectId objectId) {

        throw new RuntimeException(
                "This method should not be called because rows should not be deleted independently of " +
                        "their spreasheet.");
    }

    protected List<Document> query(Bson bson, Bson projection) {

        List<Document> documents = get(bson, projection);
        return documents;
    }

    @Override
    public List<Document> query(List<SearchCriterion> searchCriteria, ResultsMode resultsMode) {

        if (searchCriteria.size() == 0) {
            throw new RuntimeException();
        }

        encodeColumnNamesForMongoSafety(searchCriteria);

        List<CriterionAndItsNumberOfMatches> numberOfMatchesForEachCriterion =
                getNumberOfMatchesForEachCriterion(searchCriteria);

        // Sort these in descending order to promote search speed. So we'll first search with the most restrictive
        // criterion and work our way up.
        Collections.sort(numberOfMatchesForEachCriterion);

        CriterionAndItsNumberOfMatches firstCriterion = numberOfMatchesForEachCriterion.get(0);
        if (firstCriterion.getNubmerOfMatches() == 0) {
            // since the search is performed as an AND operation, this means that, by definition, there are no matches
            return new ArrayList();
        }

        // get the row numbers of all cells that match the first criterion
        List<CriterionAndItsNumberOfMatches> criteria_allButTheFirst = new ArrayList(numberOfMatchesForEachCriterion);
        criteria_allButTheFirst.remove(firstCriterion);

        Set<ObjectId> matchingRowIds = getIdsOfRowsThatMatch(firstCriterion.getCriterion());

        for (CriterionAndItsNumberOfMatches criterion : criteria_allButTheFirst) {

            // DESIGN NOTE: Each time this is called, it'll likely make the set of IDs smaller.
            matchingRowIds = getIdsOfRowsInSubsetThatMatch(matchingRowIds, criterion.getCriterion());
        }

        Set<String> dataColumnNamesToIncludedInQueryResults = new LinkedHashSet<>();

        for (SearchCriterion searchCriterion : searchCriteria) {

            dataColumnNamesToIncludedInQueryResults.add(searchCriterion.getName());
        }

        List<Document> rows = getRows(matchingRowIds, dataColumnNamesToIncludedInQueryResults);

        return rows;
    }

    protected List<CriterionAndItsNumberOfMatches> getNumberOfMatchesForEachCriterion(
            List<SearchCriterion> searchCriteria) {

        List<CriterionAndItsNumberOfMatches> results = new ArrayList<>();

        for (SearchCriterion searchCriterion : searchCriteria) {

            long count = getCountOfCellsThatMatch(searchCriterion);
            CriterionAndItsNumberOfMatches result = new CriterionAndItsNumberOfMatches(searchCriterion, count);

            results.add(result);

            if (count == 0) {
                return results;
            }
        }

        return results;
    }

    protected List<Document> getRows(Set<ObjectId> matchingIds, Set<String> dataColumnNamesToIncludedInQueryResults) {

        List<String> attributesToInclude = new ArrayList<>();
        attributesToInclude.add(RowDocument.MONGO_KEY__ID);
        attributesToInclude.add(RowDocument.MONGO_KEY__DATASET_ID);
        attributesToInclude.add(RowDocument.MONGO_KEY__METADATA);

        for (String columnIncludedInQuery : dataColumnNamesToIncludedInQueryResults) {
            attributesToInclude.add(RowDocument.MONGO_KEY__DATA + "." +
                    toMongoSafeFieldName(columnIncludedInQuery));
        }

        Bson projection = fields(include(attributesToInclude));

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "getting rows for the matching IDs");

        List<Document> results = new ArrayList();
        for (ObjectId matchingId : matchingIds) {

            Document document = getOne(matchingId, projection);

            List<Document> data = (List) document.get(RowDocument.MONGO_KEY__DATA);
            DAOUtilities.toDocumentsWithClientSideFieldNames(data);

            results.add(document);
        }

        performanceLogger.done();
        log.info("[RESULTS] results.size() = " + results.size() +
                ", row.count() = " + getCount() +
                ", cell.count() = " + cellDAO.getCollection().count());

        return results;
    }

    protected Set<ObjectId> getIdsOfRowsInSubsetThatMatch(Set<ObjectId> rowIds, SearchCriterion searchCriterion) {

        // CAUTION: Do NOT change the order of these, as these reflect an index set within the
        //          database.  If you do, you'll have to change the index as well.
        Bson query = and(
                in(CellDocument.MONGO_KEY__ROW_ID, rowIds),

                DAOUtilities.toCriterion(
                        CellDocument.MONGO_KEY__COLUMN_NAME,
                        searchCriterion.getName(),
                        searchCriterion.getComparisonOperator()),

                DAOUtilities.toCriterion(
                        CellDocument.MONGO_KEY__VALUE,
                        searchCriterion.getValue(),
                        searchCriterion.getComparisonOperator()));

        Bson projection = fields(include(CellDocument.MONGO_KEY__ROW_ID));

        PerformanceLogger performanceLogger = new PerformanceLogger(
                log,
                "[getIdsOfRowsInSubsetThatMatch()] cellDAO.get(" + DAOUtilities.toJSON(query) + ")"
        );
        List<Document> results = cellDAO.get(query, projection);

        Set<ObjectId> matchingIds = new HashSet();
        for (Document document : results) {
            matchingIds.add((ObjectId) document.get(CellDocument.MONGO_KEY__ROW_ID));
        }

        performanceLogger.done();

        return matchingIds;
    }

    public long getCountOfCellsThatMatch(SearchCriterion searchCriterion) {

        // CAUTION: Do NOT change the order of these, as these reflect an index set within the
        //          database.  If you do, you'll have to change the index as well.

        Bson query = and(

                eq(
                        CellDocument.MONGO_KEY__COLUMN_NAME,
                        toMongoSafeFieldName(searchCriterion.getName())),

                DAOUtilities.toCriterion(
                        CellDocument.MONGO_KEY__VALUE,
                        searchCriterion.getValue(),
                        searchCriterion.getComparisonOperator()));

        PerformanceLogger performanceLogger = new PerformanceLogger(
                log,
                "[getCountOfRowsThatMatch()] cellDAO.getCollection().count(" + DAOUtilities.toJSON(query) + ")"
        );
        long count = ((IMongodbDAO)cellDAO).getCollection().count(query);
        performanceLogger.done();

        return count;
    }

    public Set<ObjectId> getIdsOfRowsThatMatch(SearchCriterion searchCriterion) {

        Bson rowIdCriterion = eq(CellDocument.MONGO_KEY__COLUMN_NAME, toMongoSafeFieldName(searchCriterion.getName()));

        Bson valueCriterion = DAOUtilities.toCriterion(
                CellDocument.MONGO_KEY__VALUE,
                searchCriterion.getValue(),
                searchCriterion.getComparisonOperator());

        // CAUTION: Do NOT change the order of these, as these reflect an index set within the
        //          database.  If you do, you'll have to change the index as well.
        Bson query = and(rowIdCriterion, valueCriterion);

        Bson projection = fields(include(CellDocument.MONGO_KEY__ROW_ID));

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "[getIdsOfRowsThatMatch()] cellDAO.get(" + DAOUtilities.toJSON(query) + ")");
        List<Document> documents = cellDAO.get(query, projection);
        performanceLogger.done();
        log.info("[RESULTS] results.size() = " + documents.size() +
                ", row.count() = " + getCount() +
                ", cell.count() = " + ((IMongodbDAO)cellDAO).getCollection().count());

        return toSetOfRowIds(documents);
    }

    protected boolean rowMatchesTheCriterion(ObjectId rowId, SearchCriterion searchCriterion) {

        // CAUTION: Do NOT change the order of these, as these reflect an index set within the
        //          database.  If you do, you'll have to change the index as well.
        Bson query = and(

                eq(
                        CellDocument.MONGO_KEY__ROW_ID,
                        rowId),

                eq(
                        CellDocument.MONGO_KEY__COLUMN_NAME,
                        toMongoSafeFieldName(searchCriterion.getName())),

                DAOUtilities.toCriterion(
                        CellDocument.MONGO_KEY__VALUE,
                        searchCriterion.getValue(),
                        searchCriterion.getComparisonOperator()));

        Bson projection = fields(include(
                CellDocument.MONGO_KEY__ROW_ID));

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "[rowMatchesTheCriterion()] cellDAO.getOne(" + DAOUtilities.toJSON(query) + ")");
        Document document = cellDAO.getOne(query, projection);
        performanceLogger.done();

        return document != null;
    }

    private Set<ObjectId> toSetOfRowIds(List<Document> documents) {

        Set<ObjectId> objectIds = new HashSet();

        for (Document document : documents) {

            objectIds.add((ObjectId) document.get(CellDocument.MONGO_KEY__ROW_ID));
        }

        return objectIds;
    }

    private static boolean HAVE_MADE_SURE_TABLE_COLUMNS_ARE_INDEXED = false;

    protected void makeSureTableColumnsIRelyUponAreIndexed() {

        if (HAVE_MADE_SURE_TABLE_COLUMNS_ARE_INDEXED == false) {

            getCollection().createIndex(new Document().append(RowDocument.MONGO_KEY__ID, 1));

            // DESIGN NOTE: Even though these indexes are on the cell collectoin, they are being set here because this
            //              is where the code exists that relies upon them. I figured if they were here, they would less
            //              likely get removed by someone who didn't realize they were used somewhere.

            MongoCollection<Document> cellCollection = cellDAO.getCollection();

            BasicDBObject compoundIndex = new BasicDBObject();
            compoundIndex.put(CellDocument.MONGO_KEY__ROW_ID, 1);
            compoundIndex.put(CellDocument.MONGO_KEY__COLUMN_NAME, 1);
            compoundIndex.put(CellDocument.MONGO_KEY__VALUE, 1);
            cellCollection.createIndex(compoundIndex);

            compoundIndex = new BasicDBObject();
            compoundIndex.put(CellDocument.MONGO_KEY__COLUMN_NAME, 1);
            compoundIndex.put(CellDocument.MONGO_KEY__VALUE, 1);
            cellCollection.createIndex(compoundIndex);

            if (log.isInfoEnabled()) {

                StringBuilder message = new StringBuilder();
                message.append("Cell Indexes");

                ListIndexesIterable<Document> indexes = cellCollection.listIndexes();
                for (Document index : indexes) {
                    message.append(index.toJson());
                }

                log.info(message);
            }

            HAVE_MADE_SURE_TABLE_COLUMNS_ARE_INDEXED = true;
        }
    }

    @Override
    public ICellDAO getCellDAO(String columnName) {
        return getCellDAO();
    }

    public ICellDAO getCellDAO() {
        return cellDAO;
    }

    protected void encodeColumnNamesForMongoSafety(List<SearchCriterion> searchCriteria_data) {

        for (SearchCriterion searchCriterion : searchCriteria_data) {

            searchCriterion.setName(toMongoSafeFieldName(searchCriterion.getName()));
        }
    }

    protected String toMongoSafeFieldName(String name) {

        return MongoFieldNameEncoder.toMongoSafeFieldName(name);
    }

    private class CriterionAndItsNumberOfMatches implements Comparable {

        private final SearchCriterion criterion;
        private final Long nubmerOfMatches;

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


        @Override
        public int compareTo(Object o) {

            long delta = this.getNubmerOfMatches() - ((CriterionAndItsNumberOfMatches) o).getNubmerOfMatches();

            if (delta < 0) return -1;
            if (delta > 0) return 1;
            return 0;
        }
    }
}
