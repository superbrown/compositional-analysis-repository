package gov.energy.nrel.dataRepositoryApp.dao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;
import gov.energy.nrel.dataRepositoryApp.dao.exception.UnknownEntity;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;


public interface IDAO {

    void init(String collectionName, ISettings settings);

    ObjectId add(Object model);

    void addMany(List<Document> models);

    Document getOne(Bson filter);

    Document createDocumentOfTypeDAOHandles(Document document);

    Document getOne(Bson filter, Bson projection);

    Document getOneWithId(String id);

    Document getOne(ObjectId objectId);

    Document getOne(ObjectId objectId, Bson projection);

    List<Document> get(Bson filter);

    List<Document> get(Bson filter, Bson projection);

    void dropCollection();

    IDeleteResults delete(String id) throws UnknownEntity;

    IDeleteResults delete(ObjectId objectId) throws UnknownEntity;

    MongoCollection<Document> getCollection();

    MongoDatabase getDatabase();

    Iterable<Document> getAll();

//    UpdateResult updateOne(String id, Bson update);

    UpdateResult updateOne(String id, Bson update);

    List<Document> createDocumentsOfTypeDAOHandles(List<Document> documents);

    MongoClient getMongoClient();

    String getCollectionName();

    ISettings getSettings();

    Long getCount();

    void makeSureTableColumnsIRelyUponAreIndexed();

    void logCollectionIndexes();
}
