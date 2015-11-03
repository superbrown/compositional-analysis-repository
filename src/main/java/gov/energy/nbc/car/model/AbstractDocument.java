package gov.energy.nbc.car.model;

import com.mongodb.BasicDBObject;
import gov.energy.nbc.car.dao.DAOUtilities;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.Document;
import org.bson.types.ObjectId;

public abstract class AbstractDocument extends Document {

    public static final String ATTRIBUTE_KEY__ID = "_id";

    public AbstractDocument() {

    }

    public AbstractDocument(Object object) {

        String json = DAOUtilities.serialize(object);
        initWithJson(json);
    }

    public AbstractDocument(Document document) {
        init(document);
    }

    public AbstractDocument(String json) {
        initWithJson(json);
    }

    protected void initWithJson(String json) {

        Document document = new Document((BasicDBObject) DAOUtilities.parse(json));
        init(document);
    }

    protected abstract void init(Document object);

    public String toJson() {

        return DAOUtilities.serialize(this);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof AbstractDocument)) return false;

        AbstractDocument that = (AbstractDocument) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(this.toJson(), that.toJson())
                .isEquals();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(this.toJson())
                .toHashCode();
    }

    protected void initializeId(Document parsedJson) {

        ObjectId objectId = parsedJson.getObjectId(ATTRIBUTE_KEY__ID);

        if (objectId != null) {
            put(ATTRIBUTE_KEY__ID, objectId);
        }
    }

    public ObjectId getObjectId() {

        return getObjectId(ATTRIBUTE_KEY__ID);
    }
}
