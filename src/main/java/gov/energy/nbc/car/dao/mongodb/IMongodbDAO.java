package gov.energy.nbc.car.dao.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public interface IMongodbDAO {

    MongoClient getMongoClient();

    MongoDatabase getDatabase();

    MongoCollection<Document> getCollection();

    void dropCollection();
}
