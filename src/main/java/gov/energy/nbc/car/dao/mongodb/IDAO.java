package gov.energy.nbc.car.dao.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import gov.energy.nbc.car.ISettings;
import gov.energy.nbc.car.dao.mongodb.dto.DeleteResults;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;

public interface IDAO {

    void init(String collectionName, ISettings settings);

    ObjectId insert(Document document);

    Document getOne(Bson filter);

    Document getOne(Bson filter, Bson projection);

    Document getOneWithId(String id);

    Document getOne(ObjectId objectId);

    Document getOne(ObjectId objectId, Bson projection);

    List<Document> get(Bson filter);

    List<Document> get(Bson filter, Bson projection);

    void dropCollection();

    DeleteResults delete(String id);

    DeleteResults delete(ObjectId objectId);

    MongoCollection<Document> getCollection();

    MongoDatabase getDatabase();

    FindIterable<Document> getAll();

    UpdateResult updateOne(ObjectId objectId, Bson update);

    List<Document> createDocumentsOfTypeDAOHandles(List<Document> documents);

    MongoClient getMongoClient();

    String getCollectionName();

    ISettings getSettings();
}
