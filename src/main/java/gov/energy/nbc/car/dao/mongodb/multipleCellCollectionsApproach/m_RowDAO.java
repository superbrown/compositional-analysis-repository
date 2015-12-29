package gov.energy.nbc.car.dao.mongodb.multipleCellCollectionsApproach;

import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.car.ResultsMode;
import gov.energy.nbc.car.dao.IRowDAO;
import gov.energy.nbc.car.dao.dto.IDeleteResults;
import gov.energy.nbc.car.dao.dto.SearchCriterion;
import gov.energy.nbc.car.dao.mongodb.AbsDAO;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.dao.mongodb.MongoFieldNameEncoder;
import gov.energy.nbc.car.dao.mongodb.dto.DeleteResults;
import gov.energy.nbc.car.model.*;
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
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

public class m_RowDAO extends AbsDAO implements IRowDAO {

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

//            PerformanceLogger performanceLogger = new PerformanceLogger(log, "insert(rowDocument)");
            ObjectId rowId = add(rowDocument);
//            performanceLogger.done();

            idsOfRowsAdded.add(rowId);

            // add metadata into cell collections

            addCell(rowId, IMetadata.ATTR_KEY__SUBMISSION_DATE, metadata.getSubmissionDate());
            addCell(rowId, IMetadata.ATTR_KEY__SUBMITTER, metadata.getSubmitter());
            addCell(rowId, IMetadata.ATTR_KEY__PROJECT_NAME, metadata.getProjectName());
            addCell(rowId, IMetadata.ATTR_KEY__SUB_DOCUMENT_CONTAINING_DATA, metadata.getNameOfSubdocumentContainingData());
            addCell(rowId, IMetadata.ATTR_KEY__CHARGE_NUMBER, metadata.getChargeNumber());

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

            results.add(DAOUtilities.toDocumentWithClientSideFieldNames(data));
        }

        performanceLogger.done();
        log.info("[RESULTS] results.size() = " + results.size() +
                ", row.count() = " + getCount());

        return results;
    }

    private String toMongoSafeFieldName(String columnIncludedInQuery) {

        return MongoFieldNameEncoder.toMongoSafeFieldName(columnIncludedInQuery);
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

    public long getCountOfCellsThatMatch(SearchCriterion searchCriterion) {

        m_CellDAO cellDAOForThisName = getCellDAO(searchCriterion.getName());

        Bson valueFilter = DAOUtilities.toCriterion(
                CellDocument.ATTR_KEY__VALUE,
                searchCriterion.getValue(),
                searchCriterion.getComparisonOperator());

        PerformanceLogger performanceLogger = new PerformanceLogger(
                log,
                "[getCountOfRowsThatMatch()] cellDAO.getCollection().count(" + DAOUtilities.toJSON(valueFilter) + ")"
        );
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

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "[getIdsOfRowsThatMatch()] cellDAO.get(" + DAOUtilities.toJSON(query) + ")");
        List<Document> documents = cellDAO.get(query, projection);
        performanceLogger.done();
        log.info("[RESULTS] results.size() = " + documents.size() +
                ", row.count() = " + getCount() +
                ", cell.count() = " + cellDAO.getCount());

        return toSetOfRowIds(documents);
    }

    protected Set<ObjectId> getIdsOfRowsInSubsetThatMatch(Set<ObjectId> rowIds, SearchCriterion searchCriterion) {

        m_CellDAO cellDAO = getCellDAO(searchCriterion.getName());

        // CAUTION: Do NOT change the order of these, as these reflect an index set within the
        //          database.  If you do, you'll have to change the index as well.
        Bson query = and(
                in(CellDocument.ATTR_KEY__ROW_ID, rowIds),

                DAOUtilities.toCriterion(
                        CellDocument.ATTR_KEY__VALUE,
                        searchCriterion.getValue(),
                        searchCriterion.getComparisonOperator()));

        Bson projection = fields(include(CellDocument.ATTR_KEY__ROW_ID));

        PerformanceLogger performanceLogger = new PerformanceLogger(
                log,
                "[getIdsOfRowsInSubsetThatMatch()] cellDAO.get(" + DAOUtilities.toJSON(query) + ")"
        );
        List<Document> results = cellDAO.get(query, projection);

        Set<ObjectId> matchingIds = new HashSet();
        for (Document document : results) {
            matchingIds.add((ObjectId) document.get(CellDocument.ATTR_KEY__ROW_ID));
        }

        performanceLogger.done();

        return matchingIds;
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
