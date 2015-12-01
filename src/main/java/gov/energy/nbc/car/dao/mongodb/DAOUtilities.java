package gov.energy.nbc.car.dao.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.util.JSON;
import gov.energy.nbc.car.dao.dto.ComparisonOperator;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static gov.energy.nbc.car.dao.dto.ComparisonOperator.*;

public class DAOUtilities {

    private static Logger log = Logger.getLogger(DAOUtilities.class);

    public static List<Document> get(MongoCollection<Document> collection, Bson query) {

        try {
            PerformanceLogger performanceLogger = new PerformanceLogger(log, "= = = = = = [MONGO] find(query)");
            FindIterable<Document> resultsCursor = collection.find(query);
            performanceLogger.done();

            performanceLogger = new PerformanceLogger(log, "= = = = = = toList(resultsCursor)");
            List<Document> results = toList(resultsCursor);
            performanceLogger.done();

            return results;

        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static List<Document> get(MongoCollection<Document> collection, Bson query, Bson projection) {

        if (collection == null || query == null || projection == null) {
            throw new RuntimeException(
                    "collection = " + collection + ", query = " + query + ", projection = " + projection);
        }

        try {
            PerformanceLogger performanceLogger = new PerformanceLogger(log, "= = = = = = [MONGO] find(query)");
            FindIterable<Document> resultsCursor = collection.find(query).projection(projection);
            performanceLogger.done();

            performanceLogger = new PerformanceLogger(log, "= = = = = = toList(resultsCursor)");
            List<Document> results = toList(resultsCursor);
            performanceLogger.done();

            return results;

        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
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
}
