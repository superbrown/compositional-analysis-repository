package gov.energy.nbc.car.dao.mongodb;

import com.mongodb.client.MongoCollection;
import gov.energy.nbc.car.ISettings;
import gov.energy.nbc.car.dao.IDataCategoryDAO;
import gov.energy.nbc.car.model.IDataCategoryDocument;
import gov.energy.nbc.car.model.mongodb.document.DataCategoryDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class DataCategoryDAO extends DAO implements IDataCategoryDAO {

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
    public IDataCategoryDocument getByName(Object name) {

        Bson filter = eq(DataCategoryDocument.ATTR_KEY__SAMPLE_TYPE, name);

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
