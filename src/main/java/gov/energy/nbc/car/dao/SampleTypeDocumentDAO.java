package gov.energy.nbc.car.dao;

import com.mongodb.util.JSON;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.model.document.SampleTypeDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class SampleTypeDocumentDAO extends DAO
{
    public static final String ATTRIBUTE_KEY__COLLECTION_NAME = "sampleType";

    public SampleTypeDocumentDAO(Settings settings) {

        super(ATTRIBUTE_KEY__COLLECTION_NAME, settings);
    }

    public SampleTypeDocument get(String id) {

        return (SampleTypeDocument) queryForOneWithId(id);
    }

    public SampleTypeDocument get(ObjectId objectId) {

        Document idFilter = this.createIdFilter(objectId);
        return (SampleTypeDocument) queryForOne(idFilter, null);
    }

    public ObjectId add(SampleTypeDocument sampleTypeDocument) {

        ObjectId objectId = insert(sampleTypeDocument);
        return objectId;
    }

    @Override
    public DeleteResults delete(ObjectId objectId) {

        DeleteResults deleteResults = super.delete(objectId);

        if (deleteResults.wasAcknowledged() == false) {
            return deleteResults;
        }

        return deleteResults;
    }

    @Override
    protected Document createDocumentOfTypeDAOHandles(String json) {

        return new SampleTypeDocument(json);
    }

    public SampleTypeDocument getByName(Object name) {

        Bson filter = eq(SampleTypeDocument.ATTRIBUTE_KEY__SAMPLE_TYPE, name);

        List<Document> results = this.query(filter);
        if (results.size() == 0) {
            return null;
        }

        Document object = results.get(0);
        String json = JSON.serialize(object);

        SampleTypeDocument sampleTypeDocument = new SampleTypeDocument(json);
        return sampleTypeDocument;
    }
}
