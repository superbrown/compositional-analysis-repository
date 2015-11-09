package gov.energy.nbc.car.dao.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class DAOUtilities {

    public static List<Document> get(MongoCollection<Document> collection, Bson query) {

        try {
            FindIterable<Document> resultsCursor = collection.find(query);
            List<Document> results = extractResults(resultsCursor);
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
            FindIterable<Document> resultsCursor = collection.find(query).projection(projection);
            List<Document> results = extractResults(resultsCursor);
            return results;

        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static Document getOne(MongoCollection<Document> collection, Bson query, Bson projection) {

        FindIterable<Document> resultsCursor = collection.find(query).projection(projection).
                // If the number is negative, the database will return that number and close the cursor.
                // From: https://docs.mongodb.org/manual/reference/mongodb-wire-protocol/
                        limit(-1);

        List<Document> results = extractResults(resultsCursor);

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

        List<Document> results = extractResults(resultsCursor);

        if (results.size() != 0) {
            return results.get(0);
        }
        else {
            return null;
        }
    }

    protected static List<Document> extractResults(FindIterable<Document> resultsCursor) {

        MongoCursor<Document> resultsIterator = resultsCursor.iterator();
        List<Document> results = toList(resultsIterator);
        return results;
    }

    public static List<Document> toList(MongoCursor<Document> resultsIterator) {

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

        return JSON.serialize(object);
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

}
