package gov.energy.nbc.car.model;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.Document;
import org.bson.types.ObjectId;

public abstract class AbstractDocument extends Document {

    public static final String ATTRIBUTE_KEY__ID = "_id";

    public AbstractDocument() {

    }

    public AbstractDocument(Object object) {

        String json = JSON.serialize(object);
        initWithJson(json);
    }

    public AbstractDocument(String json) {

        initWithJson(json);
    }

    protected abstract void initWithJson(String json);


    public String toJson() {

        return JSON.serialize(this);
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

    protected void initializeId(BasicDBObject parsedJson) {

        ObjectId objectId = parsedJson.getObjectId(ATTRIBUTE_KEY__ID);

        if (objectId != null) {
            put(ATTRIBUTE_KEY__ID, objectId);
        }
    }

    public ObjectId getObjectId() {

        return getObjectId(ATTRIBUTE_KEY__ID);
    }
}
