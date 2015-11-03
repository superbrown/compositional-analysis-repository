package gov.energy.nbc.car.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.model.AbstractDocument;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;

public abstract class DAO {

    protected MongoDatabase database;
    protected MongoClient mongoClient;
    private MongoCollection<Document> collection;

    protected String collectionName;

    protected Settings settings = new Settings();

    public DAO(String collectionName, Settings settings) {

        init(collectionName, settings);
    }

    private void init(String collectionName, Settings settings) {

        this.collectionName = collectionName;
        this.settings = settings;

        mongoClient = new MongoClient(settings.getMongoDbHost(), settings.getMongoDbPort());
        database = mongoClient.getDatabase(settings.getMongoDatabaseName());
    }

    protected ObjectId insert(Document document) {

        // DESIGN NOTE: I don't know why we need to create a wrapper here, but if we don't, Mongo throws the following
        // exception:
        //
        // org.bson.codecs.configuration.CodecConfigurationException: Can't find a codec for class
        // gov.energy.nbc.spreadsheet.model.document.SpreadsheetDocument.

        PerformanceLogger performanceLogger = new PerformanceLogger("new Document(document)");
        Document wrapper = new Document(document);
        performanceLogger.done();

        performanceLogger = new PerformanceLogger("getCollection().insertOne(wrapper)");
        getCollection().insertOne(wrapper);
        performanceLogger.done();

        ObjectId objectId = wrapper.getObjectId("_id");
        System.out.println("Inserted (ID: " + objectId + "): " + document);
        return objectId;
    }


    protected Document queryForOne(Bson filter, Bson projection) {

        List<Document> documents = query(filter, projection);

        if (documents.size() == 0) {
            return null;
        }

        if (documents.size() > 1) {
            throw new RuntimeException("getOne() was called with a filer that matches more than one document: " +
                    DAOUtilities.serialize(filter));
        }


        Document document = documents.get(0);
        Document documentOfTypeDAOHandles = createDocumentOfTypeDAOHandles(document);

        return documentOfTypeDAOHandles;
    }

    abstract protected Document createDocumentOfTypeDAOHandles(Document document);

    public Document queryForOne(Bson filter) {

        List<Document> documents = query(filter);

        if (documents.size() == 0) {
            return null;
        }

        return documents.get(0);
    }

    public List<Document> query(Bson filter) {

        return query(filter, null);
    }

    protected Document queryForOneWithId(String id) {

        ObjectId objectId = new ObjectId(id);
        Document idFilter = createIdFilter(objectId);
        return queryForOne(idFilter, null);
    }

    public List<Document> query(Bson filter, Bson projection) {

        MongoCollection<Document> collection = getCollection();
        return DAOUtilities.get(collection, filter, projection);
    }

    public DeleteResult removeAllDataFromCollection() {

        MongoCollection<Document> collection = getCollection();
        DeleteResult deleteResult = collection.deleteMany(new BasicDBObject());
        if (deleteResult.wasAcknowledged() == false) {
            return deleteResult;
        }

        return deleteResult;
    }

    public void dropCollection() {

        getCollection().drop();
    }

    public DeleteResults delete(String id) {

        return delete(new ObjectId(id));
    }

    public DeleteResults delete(ObjectId objectId) {

        Document idFilter = createIdFilter(objectId);

        DeleteResult deleteResult = getCollection().deleteOne(idFilter);

        DeleteResults deleteResults = new DeleteResults(deleteResult);

        return deleteResults;
    }

    public static Document createIdFilter(ObjectId objectId) {

        Document idFilter = new Document().
                append(AbstractDocument.ATTRIBUTE_KEY__ID, objectId);
        return idFilter;
    }


    public MongoCollection<Document> getCollection() {

        if (collection == null) {
            collection = database.getCollection(collectionName).withWriteConcern(WriteConcern.ACKNOWLEDGED);

        }

        return collection;
    }

    public MongoDatabase getDatabase() {

        return database;
    }

    public FindIterable<Document> getAll() {

        return getCollection().find(new BasicDBObject());
    }


    public UpdateResult updateOne(ObjectId objectId, Bson update) {

        Bson filter = createIdFilter(objectId);
        UpdateResult updateResult = getCollection().updateOne(filter, new Document("$set", update));
        return updateResult;
    }
}
