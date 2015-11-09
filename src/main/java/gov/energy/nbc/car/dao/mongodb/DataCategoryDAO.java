package gov.energy.nbc.car.dao.mongodb;

import com.mongodb.client.MongoCollection;
import gov.energy.nbc.car.ISettings;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.model.document.DataCategoryDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class DataCategoryDAO extends DAO
{
    public static final String COLLECTION_NAME = "dataCategory";

    public DataCategoryDAO(ISettings settings) {
        super(COLLECTION_NAME, settings);
    }

    public DataCategoryDocument get(String id) {

        return (DataCategoryDocument) getOneWithId(id);
    }

    public DataCategoryDocument get(ObjectId objectId) {

        Document idFilter = this.createIdFilter(objectId);
        return (DataCategoryDocument) getOne(idFilter);
    }

    public ObjectId add(DataCategoryDocument dataCategoryDocument) {

        ObjectId objectId = insert(dataCategoryDocument);
        return objectId;
    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(Document document) {
        return new DataCategoryDocument(document);
    }

    public DataCategoryDocument getByName(Object name) {

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
