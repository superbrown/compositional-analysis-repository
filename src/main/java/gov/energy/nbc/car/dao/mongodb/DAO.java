package gov.energy.nbc.car.dao.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import gov.energy.nbc.car.ISettings;
import gov.energy.nbc.car.dao.IDAO;
import gov.energy.nbc.car.dao.dto.DeleteResults;
import gov.energy.nbc.car.model.mongodb.AbstractDocument;
import gov.energy.nbc.car.utilities.PerformanceLogger;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public abstract class DAO implements IDAO {

    protected MongoDatabase database;
    protected MongoClient mongoClient;
    private MongoCollection<Document> collection;

    private String collectionName;

    protected ISettings settings;
    protected Logger log = Logger.getLogger(this.getClass());

    static {

    }

    public DAO(String collectionName, ISettings settings) {

        init(collectionName, settings);
    }

    @Override
    public void init(String collectionName, ISettings settings) {

        this.collectionName = collectionName;
        this.settings = settings;

        mongoClient = MongoClients.getClientForHost(
                settings.getMongoDbHost(),
                settings.getMongoDbPort());

        database = mongoClient.getDatabase(
                settings.getMongoDatabaseName());
    }

    @Override
    public ObjectId add(Object model) {

        if ((model instanceof Document) == false) {

            throw new InvalidParameterException("Must be Document object, but was a :" + model.getClass().getName());
        }

        Document document = (Document)model;

        // DESIGN NOTE: I don't know why we need to create a wrapper here, but if we don't, Mongo throws the following
        // exception:
        //
        // org.bson.codecs.configuration.CodecConfigurationException: Can't find a codec for class
        // gov.energy.nbc.dataset.model.document.Dataset.

        PerformanceLogger performanceLogger = new PerformanceLogger(log, "new Document(document)");
        Document wrapper = new Document(document);
        performanceLogger.done();

        performanceLogger = new PerformanceLogger(log, "getCollection().insertOne(wrapper)");
        getCollection().insertOne(wrapper);
        performanceLogger.done();

        ObjectId objectId = wrapper.getObjectId("_id");
//        System.out.println("Inserted (ID: " + objectId + "): " + document);
        return objectId;
    }


    @Override
    public Document getOne(Bson filter) {
        Document document = DAOUtilities.getOne(getCollection(), filter);
        return createDocumentOfTypeDAOHandles(document);
    }

    protected abstract Document createDocumentOfTypeDAOHandles(Document document);

    @Override
    public Document getOne(Bson filter, Bson projection) {
        Document document = DAOUtilities.getOne(getCollection(), filter, projection);
        return document;
    }


    @Override
    public Document getOneWithId(String id) {
        ObjectId objectId = new ObjectId(id);
        return getOne(objectId);
    }

    @Override
    public Document getOne(ObjectId objectId) {
        Document idFilter = createIdFilter(objectId);
        return getOne(idFilter);
    }

    @Override
    public Document getOne(ObjectId objectId, Bson projection) {
        Document idFilter = createIdFilter(objectId);
        Document document = getOne(idFilter, projection);
        return document;
    }


    @Override
    public List<Document> get(Bson filter) {

        List<Document> documents = DAOUtilities.get(getCollection(), filter);
        return createDocumentsOfTypeDAOHandles(documents);
    }

    @Override
    public List<Document> get(Bson filter, Bson projection) {

        List<Document> documents = DAOUtilities.get(getCollection(), filter, projection);
        return documents;
    }

    @Override
    public void dropCollection() {

        getCollection().drop();
    }

    @Override
    public DeleteResults delete(String id) {

        return delete(new ObjectId(id));
    }

    @Override
    public DeleteResults delete(ObjectId objectId) {

        Document idFilter = createIdFilter(objectId);

        DeleteResult deleteResult = getCollection().deleteOne(idFilter);

        DeleteResults deleteResults = new DeleteResults(deleteResult);

        return deleteResults;
    }

    public static Document createIdFilter(ObjectId objectId) {

        Document idFilter = new Document().
                append(AbstractDocument.ATTR_KEY__ID, objectId);
        return idFilter;
    }


    @Override
    public MongoCollection<Document> getCollection() {

        if (collection == null) {
            collection = database.getCollection(collectionName).withWriteConcern(WriteConcern.NORMAL);
        }

        return collection;
    }

    @Override
    public MongoDatabase getDatabase() {

        return database;
    }

    @Override
    public FindIterable<Document> getAll() {

        return getCollection().find(new BasicDBObject());
    }

    @Override
    public UpdateResult updateOne(String id, Bson update) {

        Bson filter = createIdFilter(new ObjectId(id));
        UpdateResult updateResult = getCollection().updateOne(filter, new Document("$set", update));
        return updateResult;
    }

    @Override
    public List<Document> createDocumentsOfTypeDAOHandles(List<Document> documents) {

        List<Document> documentsOfTypeDAOHandles = new ArrayList<>();

        for (Document document : documents) {

            Document documentOfTypeDAOHandles = createDocumentOfTypeDAOHandles(document);
            documentsOfTypeDAOHandles.add(documentOfTypeDAOHandles);
        }

        return documentsOfTypeDAOHandles;
    }

    @Override
    public MongoClient getMongoClient() {
        return mongoClient;
    }

    @Override
    public String getCollectionName() {
        return collectionName;
    }

    @Override
    public ISettings getSettings() {
        return settings;
    }
}
