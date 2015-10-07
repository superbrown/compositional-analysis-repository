package gov.energy.nbc.spreadsheet.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import gov.energy.nbc.spreadsheet.Settings;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;

public class DAO {

    protected MongoDatabase database;
    protected MongoClient mongoClient;
    private MongoCollection<Document> collection;

    protected String collectionName;
    protected String attributeKey;

    protected Settings settings = new Settings();

    public DAO(String collectionName, String attributeKey) {

        init(collectionName, attributeKey);
    }

    private void init(String collectionName, String attributeKey) {

        this.collectionName = collectionName;
        this.attributeKey = attributeKey;

        mongoClient = new MongoClient(settings.getMongoDbHost(), settings.getMongoDbPort());
        database = mongoClient.getDatabase(settings.getMongoDatabaseName());
    }

    protected ObjectId insert(Document document) {

        getCollection().insertOne(document);
        ObjectId objectId = document.getObjectId("_id");
        System.out.println("Inserted (ID: " + objectId + "): " + document);
        return objectId;
    }


    protected Document encloseInADocumentWithTypeKey(Document document) {

        Document enclosingDocument = new Document();
        enclosingDocument.put(getAttributeKey(), document);
        return enclosingDocument;
    }

    public Document get(ObjectId objectId) {

        Document idFilter = this.createIdFilter(objectId);
        return getOne(idFilter, null);
    }

    public Document getOne(Bson filter, Bson projection) {

        List<Document> documents = get(filter, projection);

        if (documents.size() == 0) {
            return null;
        }

        return documents.get(0);
    }

    public Document getOne(Bson filter) {

        List<Document> documents = get(filter);

        if (documents.size() == 0) {
            return null;
        }

        return documents.get(0);
    }

    public List<Document> get(Bson filter) {

        return get(filter, null);
    }

    public List<Document> get(Bson filter, Bson projection) {

        return DAOUtils.get(getCollection(), filter, projection);
    }

    public DeleteResult deleteAll() {
        return getCollection().deleteMany(new BasicDBObject());
    }

    public DeleteResult delete(ObjectId objectId) {

        Document idFilter = createIdFilter(objectId);
        return getCollection().deleteOne(idFilter);
    }

    protected Document createIdFilter(ObjectId objectId) {

        Document idFilter = new Document();
        idFilter.put("_id", objectId);
        return idFilter;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }


    public MongoCollection<Document> getCollection() {

        if (collection == null) {
            collection = database.getCollection(collectionName).withWriteConcern(WriteConcern.ACKNOWLEDGED);

        }

        return collection;
    }
}
