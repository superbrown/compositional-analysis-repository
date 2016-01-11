package gov.energy.nrel.dataRepositoryApp.model.common.mongodb;

import com.mongodb.BasicDBObject;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.model.document.IThingWithAnId;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.Document;
import org.bson.types.ObjectId;

public abstract class AbstractDocument extends Document implements IThingWithAnId {

    public static final String MONGO_KEY__ID = "_id";

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

    protected abstract void init(Document document);

    public String toJson() {

        return DAOUtilities.serialize(this);
    }

    protected void initObjectId(Document document) {

        ObjectId objectId = document.getObjectId(MONGO_KEY__ID);

        if (objectId != null) {
            put(MONGO_KEY__ID, objectId);
        }
    }

    public ObjectId getObjectId() {

        return getObjectId(MONGO_KEY__ID);
    }

    @Override
    public String getId() {

        return getObjectId().toHexString();
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
}
