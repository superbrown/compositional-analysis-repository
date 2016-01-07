package gov.energy.nrel.dataRepositoryApp.model.mongodb;

import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.Document;
import org.bson.types.ObjectId;

public abstract class AbstractBasicDBObject extends Document {

    public static final String MONGO_KEY__ID = "_id";

    public AbstractBasicDBObject() {

    }

    public AbstractBasicDBObject(Object object) {

        String json = DAOUtilities.serialize(object);
        init(json);
    }

    public AbstractBasicDBObject(String json) {

        init(json);
    }

    protected abstract void init(String json);


    public String toJson() {

        return DAOUtilities.serialize(this);
    }

    public String getJson() {

        return this.toJson();
    }

    protected void initializeId(Document parsedJson) {

        ObjectId objectId = parsedJson.getObjectId(MONGO_KEY__ID);

        if (objectId != null) {
            put(MONGO_KEY__ID, objectId);
        }
    }

    public ObjectId getObjectId() {

        return getObjectId(MONGO_KEY__ID);
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof AbstractBasicDBObject)) return false;

        AbstractBasicDBObject that = (AbstractBasicDBObject) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getJson(), that.getJson())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getJson())
                .toHashCode();
    }
}
