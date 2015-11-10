package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.ISettings;
import gov.energy.nbc.car.bo.exception.DeletionFailure;
import gov.energy.nbc.car.dao.dto.IDeleteResults;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;


public interface IDAO {

    void init(String collectionName, ISettings settings);

    ObjectId add(Object model);

    Document getOne(Bson filter);

    Document getOne(Bson filter, Bson projection);

    Document getOneWithId(String id);

    Document getOne(ObjectId objectId);

    Document getOne(ObjectId objectId, Bson projection);

    List<Document> get(Bson filter);

    List<Document> get(Bson filter, Bson projection);

    IDeleteResults delete(String id) throws DeletionFailure;

    IDeleteResults delete(ObjectId objectId);

    Iterable<Document> getAll();

//    UpdateResult updateOne(String id, Bson update);

    List<Document> createDocumentsOfTypeDAOHandles(List<Document> documents);

    String getCollectionName();

    ISettings getSettings();
}
