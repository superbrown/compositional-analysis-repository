package gov.energy.nbc.car.dao.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import gov.energy.nbc.car.model.IMetadata;
import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.dao.IDataCategoryDAO;
import gov.energy.nbc.car.model.IDataCategoryDocument;
import gov.energy.nbc.car.model.mongodb.document.DataCategoryDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;

public class DataCategoryDAO extends AbsDAO implements IDataCategoryDAO {

    public static final String COLLECTION_NAME = "dataCategory";

    public DataCategoryDAO(ISettings settings) {
        super(COLLECTION_NAME, settings);
    }

    @Override
    public IDataCategoryDocument get(String id) {

        return (DataCategoryDocument) getOneWithId(id);
    }

    @Override
    public IDataCategoryDocument get(ObjectId objectId) {

        Document idFilter = this.createIdFilter(objectId);
        return (DataCategoryDocument) getOne(idFilter);
    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(Document document) {
        return new DataCategoryDocument(document);
    }

    @Override
    public List<String> getAllNames() {

        List<Document> documents = DAOUtilities.get(
                getCollection(),
                new BasicDBObject(),
                new BasicDBObject().append(DataCategoryDocument.ATTR_KEY__NAME, 1));

        List<String> names = new ArrayList<>();
        for (Document document : documents) {

            names.add((String) document.get(DataCategoryDocument.ATTR_KEY__NAME));
        }

        return names;
    }

    @Override
    public IDataCategoryDocument getByName(Object name) {

        Bson filter = eq(DataCategoryDocument.ATTR_KEY__NAME, name);

        MongoCollection<Document> collection = getCollection();
        Document document = DAOUtilities.getOne(collection, filter, null);

        if (document == null) {
            return null;
        }

        String json = DAOUtilities.serialize(document);

        DataCategoryDocument dataCategoryDocument = new DataCategoryDocument(json);

        return dataCategoryDocument;
    }
}
