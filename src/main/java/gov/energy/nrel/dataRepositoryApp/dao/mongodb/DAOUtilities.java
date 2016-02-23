package gov.energy.nrel.dataRepositoryApp.dao.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.dao.dto.ComparisonOperator;
import gov.energy.nrel.dataRepositoryApp.utilities.PerformanceLogger;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static gov.energy.nrel.dataRepositoryApp.dao.dto.ComparisonOperator.*;

public class DAOUtilities {

    private static Logger log = Logger.getLogger(DAOUtilities.class);

    public static List<Document> get(MongoCollection<Document> collection, Bson query) {

        return get(collection, query, null);
    }

    /**
     * If projection is null, everything will be returned.
     */
    public static List<Document> get(MongoCollection<Document> collection, Bson query, Bson projection) {

        if (collection == null || query == null) {
            throw new RuntimeException(
                    "collection = " + collection + ", query = " + query + ", projection = " + projection);
        }

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "= = = = = = [MONGO] find(query)");

        FindIterable<Document> resultsCursor = collection.find(query);

        if (projection != null) {
            resultsCursor = resultsCursor.projection(projection);
        }

        performanceLogger.done();

        performanceLogger = new PerformanceLogger(log, "= = = = = = toList(resultsCursor)");
        List<Document> results = toList(resultsCursor);
        performanceLogger.done();

        return results;
    }

    public static Document getOne(MongoCollection<Document> collection, Bson query, Bson projection) {

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "= = = = = = [MONGO] find(query).projection(projection).limit(-1)");
        FindIterable<Document> resultsCursor = collection.find(query).projection(projection).
                // If the number is negative, the database will return that number and close the cursor.
                // From: https://docs.mongodb.org/manual/reference/mongodb-wire-protocol/
                        limit(-1);
        performanceLogger.done();

        performanceLogger = new PerformanceLogger(log, "= = = = = = toList(resultsCursor)");
        List<Document> results = toList(resultsCursor);
        performanceLogger.done();

        if (results.size() != 0) {
            return results.get(0);
        }
        else {
            return null;
        }
    }

    public static Document getOne(MongoCollection<Document> collection, Bson query) {

        FindIterable<Document> resultsCursor;

        resultsCursor = collection.find(query).
                // If the number is negative, the database will return that number and close the cursor.
                // From: https://docs.mongodb.org/manual/reference/mongodb-wire-protocol/
                        limit(-1);

        List<Document> results = toList(resultsCursor);

        if (results.size() != 0) {
            return results.get(0);
        }
        else {
            return null;
        }
    }

    public static List<Document> toList(FindIterable<Document> resultsCursor) {

        MongoCursor<Document> resultsIterator = resultsCursor.iterator();

        List<Document> list = new ArrayList<>();

        if (resultsIterator.hasNext() != false) {

            while (resultsIterator.hasNext()) {
                list.add(resultsIterator.next());
            }
        }

        return list;
    }

    private static List<Document> toList(Iterator<Document> resultsIterator) {

        List<Document> list = new ArrayList<>();

        if (resultsIterator.hasNext() == false) {
            return list;
        }

        while (resultsIterator.hasNext()) {
            list.add(resultsIterator.next());
        }

        return list;
    }

    public static String serialize(Object object) {

        try {
            return JSON.serialize(object);
        } catch (RuntimeException e) {
            return object.toString();
        }
    }

    public static Object parse(String json) {

        return JSON.parse(json);
    }

    public static String toJSON(Object object) {

        return serialize(object);
    }

    public static Object fromJSON(String json) {

        return parse(json);
    }

    public static Bson toCriterion(
            String name,
            Object value,
            ComparisonOperator comparisonOperator) {

        Bson criterion;

        if (comparisonOperator == EQUALS) {
            criterion = eq(name, value);
        }
        else if (comparisonOperator == GREATER_THAN) {
            criterion = gt(name, value);
        }
        else if (comparisonOperator == LESS_THAN) {
            criterion = lt(name, value);
        }
        else if (comparisonOperator == GREATER_THAN_OR_EQUAL) {
            criterion = gte(name, value);
        }
        else if (comparisonOperator == LESS_THAN_OR_EQUAL) {
            criterion = lte(name, value);
        }
        else if (comparisonOperator == CONTAINS) {
            criterion = new BasicDBObject();
            ((BasicDBObject)criterion).put(
                    name,
                    java.util.regex.Pattern.compile(value.toString()));
        }
        else {
           throw new RuntimeException("Unknown comparison operator: " + comparisonOperator);
        }

        return criterion;
    }

    public static List<Document> toDocumentsWithClientSideFieldNames(List<Document> documents) {

        List<Document> documentasWithClientSidFiledNames = new ArrayList<>();

        for (Document document : documents) {
            documentasWithClientSidFiledNames.add(toDocumentWithClientSideFieldNames(document));
        }

        return documentasWithClientSidFiledNames;
    }

    public static Document toDocumentWithClientSideFieldNames(Document document) {

        Document clientSideFieldNames = new Document();

        for (String key : document.keySet()) {

            String clientSideName = MongoFieldNameEncoder.toClientSideFieldName(key);
            clientSideFieldNames.put(clientSideName, document.get(key));
        }

        return clientSideFieldNames;
    }

    public static Set<String> toDocumentsWithClientSideFieldNames(Set<String> columnNames) {

        Set<String> clientSideFieldNames = new HashSet<>();

        for (String columnName : columnNames) {

            clientSideFieldNames.add(MongoFieldNameEncoder.toClientSideFieldName(columnName));
        }

        return clientSideFieldNames;
    }
}
