package gov.energy.nbc.car.dao.mongodb.multipleCellSchemaApproach;

import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.car.dao.IRowDAO;
import gov.energy.nbc.car.dao.dto.IDeleteResults;
import gov.energy.nbc.car.dao.dto.SearchCriterion;
import gov.energy.nbc.car.dao.mongodb.DAO;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.mongodb.MongoFieldNameEncoder;
import gov.energy.nbc.car.dao.mongodb.dto.DeleteResults;
import gov.energy.nbc.car.model.*;
import gov.energy.nbc.car.model.mongodb.common.Metadata;
import gov.energy.nbc.car.model.mongodb.document.CellDocument;
import gov.energy.nbc.car.model.mongodb.document.RowDocument;
import gov.energy.nbc.car.settings.ISettings;
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

public class m_RowDAO extends DAO implements IRowDAO {

    public static final String COLLECTION_NAME = "row";

    protected Logger log = Logger.getLogger(this.getClass());
    private Map<String, m_CellDAO> cellDAOs;

    public m_RowDAO(ISettings settings) {

        super(COLLECTION_NAME, settings);
        cellDAOs = new HashMap<>();
    }

    @Override
    public IRowDocument get(String id) {

        return (IRowDocument) getOneWithId(id);
    }

    @Override
    public List<ObjectId> add(ObjectId datasetId, IDatasetDocument datasetDocument, IRowCollection data) {

        List<ObjectId> idsOfRowsAdded = new ArrayList();

        IMetadata metadata = datasetDocument.getMetadata();

        for (IRow row : data.getRows()) {

            RowDocument rowDocument = new RowDocument(
                    datasetId,
                    metadata,
                    row);

            // add row

            PerformanceLogger performanceLogger = new PerformanceLogger(log, "insert(rowDocument)");
            ObjectId rowId = add(rowDocument);
            performanceLogger.done();

            idsOfRowsAdded.add(rowId);

            // add metadata into cell collections

            addCell(rowId, Metadata.ATTR_KEY__SUBMISSION_DATE, metadata.getSubmissionDate());
            addCell(rowId, Metadata.ATTR_KEY__SUBMITTER, metadata.getSubmitter());
            addCell(rowId, Metadata.ATTR_KEY__PROJECT_NAME, metadata.getProjectName());
            addCell(rowId, Metadata.ATTR_KEY__CHARGE_NUMBER, metadata.getChargeNumber());

            // add data into cell collections

            for (String columnName : row.getColumnNames()) {

                addCell(rowId, columnName, row.getValue(columnName));
            }
        }

        return idsOfRowsAdded;
    }

    protected void addCell(ObjectId rowId, String columnName, Object value) {

        m_CellDAO cellDAO = getCellDAO(columnName);
        cellDAO.add(rowId, value);
    }

    public DeleteResults deleteRowsAssociatedWithDataset(ObjectId datasetId) {

        DeleteResults allDeleteResults = new DeleteResults();

        Document datasetIdFilter = new Document().
                append(RowDocument.ATTR_KEY__DATASET_ID, datasetId);

//        Bson projection = fields(include(RowDocument.ATTR_KEY__ID));

//        List<Document> rowIds = get(datasetIdFilter, projection);
        List<Document> rows = get(datasetIdFilter);

        if (rows.size() > 0) {

            Document firstRow = rows.get(0);
            Document data = (Document) firstRow.get(RowDocument.ATTR_KEY__DATA);

            for (String columnName : data.keySet()) {

                m_CellDAO cellDAO = getCellDAO(columnName);

                for (Document rowDocument : rows) {

                    ObjectId rowId = (ObjectId) rowDocument.get("_id");

                    // delete all cells associated with the row
                    allDeleteResults.addAll(cellDAO.deleteCellsAssociatedWithRow(rowId));

                    // detlete the row
                    allDeleteResults.addAll(deleteRow(rowId));
                }
            }
        }

        return allDeleteResults;
    }

    private IDeleteResults deleteRow(ObjectId rowId) {

        Document idFilter = createIdFilter(rowId);

        DeleteResult deleteResult = getCollection().deleteOne(idFilter);

        DeleteResults deleteResults = new DeleteResults(deleteResult);

        return deleteResults;
    }

    protected Document createDocumentOfTypeDAOHandles(Document document) {

        return new RowDocument(document);
    }

    @Override
    public IDeleteResults delete(ObjectId objectId) {

        throw new RuntimeException(
                "This method should not be called because rows should not be deleted  independently of " +
                        "their spreasheet.");
    }

//    @Override
//    public List<Document> query(String query, String projection) {
//
//        Bson bson_query = (Bson) DAOUtilities.parse(query);
//        Bson bson_projection = (Bson)DAOUtilities.parse(projection);
//        return query(bson_query, bson_projection);
//    }

    @Override
    public List<Document> query(List<SearchCriterion> searchCriteria) {

        if (searchCriteria.size() == 0) {
            throw new RuntimeException();
        }

        encodeColumnNamesForMongoSafety(searchCriteria);

        CriterionAndItsNumberOfMatches criterionWithTheFewestMatches = null;

        if (searchCriteria.isEmpty() == false) {
            criterionWithTheFewestMatches = determineCriterionWithTheFewestMatches(searchCriteria);
        }

        if (criterionWithTheFewestMatches.getNubmerOfMatches() == 0) {
            // since the search is performed as an AND operation, this means there can be no matches
            return new ArrayList();
        }

        SearchCriterion firstCriterionToApply = criterionWithTheFewestMatches.getCriterion();

        // get the row numbers of all cells that match the first criterion
        List<SearchCriterion> searchCriteria_allCriteriaButTheFirst = new ArrayList(searchCriteria);
        searchCriteria_allCriteriaButTheFirst.remove(firstCriterionToApply);

        Set<ObjectId> matchingRowIds = getIdsOfRowsThatMatch(firstCriterionToApply);

        for (SearchCriterion searchCondition : searchCriteria_allCriteriaButTheFirst) {

            // DESIGN NOTE: Each time this is called, it'll likely make the set of IDs smaller.
            matchingRowIds = getIdsOfSubsetOfRowsThatMatch(matchingRowIds, searchCondition);
        }

        Set<String> dataColumnNamesToIncludedInQueryResults = new LinkedHashSet<>();

        for (SearchCriterion searchCriterion : searchCriteria) {

            dataColumnNamesToIncludedInQueryResults.add(searchCriterion.getName());
        }

        List<Document> rows = getRows(matchingRowIds, dataColumnNamesToIncludedInQueryResults);

        return rows;
    }

    protected List<Document> getRows(Set<ObjectId> matchingIds, Set<String> dataColumnNamesToIncludedInQueryResults) {

        List<String> attributesToInclude = new ArrayList<>();
        attributesToInclude.add(RowDocument.ATTR_KEY__ID);
        attributesToInclude.add(RowDocument.ATTR_KEY__DATASET_ID);
        attributesToInclude.add(RowDocument.ATTR_KEY__METADATA);

        for (String columnIncludedInQuery : dataColumnNamesToIncludedInQueryResults) {
            attributesToInclude.add(RowDocument.ATTR_KEY__DATA + "." +
                    toMongoSafeFieldName(columnIncludedInQuery));
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
                ", row.count() = " + getCount());

        return results;
    }

    private String toMongoSafeFieldName(String columnIncludedInQuery) {

        return MongoFieldNameEncoder.toMongoSafeFieldName(columnIncludedInQuery);
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

    protected CriterionAndItsNumberOfMatches determineCriterionWithTheFewestMatches(
            List<SearchCriterion> searchCriteria) {

        long lowestNumber = -1;
        SearchCriterion criterionWithTheFewestMatches = null;

        for (SearchCriterion searchCriterion : searchCriteria) {

            long count = getCountOfCellsThatMatch(searchCriterion);

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

    public long getCountOfCellsThatMatch(SearchCriterion searchCriterion) {

        m_CellDAO cellDAOForThisName = getCellDAO(searchCriterion.getName());

        Bson valueFilter = DAOUtilities.toCriterion(
                CellDocument.ATTR_KEY__VALUE,
                searchCriterion.getValue(),
                searchCriterion.getComparisonOperator());

        PerformanceLogger performanceLogger = new PerformanceLogger(
                log,
                "[getCountOfRowsThatMatch()] cellDAO.getCollection().count(" + valueFilter.toString() + ")",
                true);
        long count = cellDAOForThisName.getCollection().count(valueFilter);
        performanceLogger.done();

        return count;
    }


    @Override
    public Set<ObjectId> getIdsOfRowsThatMatch(SearchCriterion searchCriterion) {

        m_CellDAO cellDAO = getCellDAO(searchCriterion.getName());

        Bson query = DAOUtilities.toCriterion(
                CellDocument.ATTR_KEY__VALUE,
                searchCriterion.getValue(),
                searchCriterion.getComparisonOperator());

        Bson projection = fields(include(CellDocument.ATTR_KEY__ROW_ID));

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "[getIdsOfRowsThatMatch()] cellDAO.get(" + query.toString() + ")", true);
        List<Document> documents = cellDAO.get(query, projection);
        performanceLogger.done();
        log.info("[RESULTS] results.size() = " + documents.size() +
                ", row.count() = " + getCount() +
                ", cell.count() = " + cellDAO.getCount());

        return toSetOfRowIds(documents);
    }

    protected boolean rowMatchesTheCriterion(ObjectId rowId, SearchCriterion searchCriterion) {

        m_CellDAO cellDAO = getCellDAO(searchCriterion.getName());

        // CAUTION: Do NOT change the order of these, as these reflect an index set within the
        //          database.  If you do, you'll have to change the index as well.
        Bson query = and(
                eq(
                        CellDocument.ATTR_KEY__ROW_ID,
                        rowId),

                DAOUtilities.toCriterion(
                        CellDocument.ATTR_KEY__VALUE,
                        searchCriterion.getValue(),
                        searchCriterion.getComparisonOperator()));

        Bson projection = fields(include(CellDocument.ATTR_KEY__ROW_ID));

        PerformanceLogger performanceLogger = new PerformanceLogger(
                log,
                "[rowMatchesTheCriterion()] cellDAO.getOne(" + query.toString() + ")",
                true);
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

    @Override
    public m_CellDAO getCellDAO(String columnName) {

        String collectonNameForCell = toCellCollectionName(columnName);

        m_CellDAO cellDAO = cellDAOs.get(collectonNameForCell);

        if (cellDAO == null) {
            cellDAO = new m_CellDAO(collectonNameForCell, settings);
            cellDAOs.put(columnName, cellDAO);
        }

        return cellDAO;
    }

    private static final String PREFIX_FOR_CELL_COLLECTIONS = "CELL_";

    public static String toCellCollectionName(String columnName) {
        return PREFIX_FOR_CELL_COLLECTIONS + columnName;
    }

    protected void encodeColumnNamesForMongoSafety(List<SearchCriterion> searchCriteria_data) {

        for (SearchCriterion searchCriterion : searchCriteria_data) {

            searchCriterion.setName(toMongoSafeFieldName(searchCriterion.getName()));
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
}
