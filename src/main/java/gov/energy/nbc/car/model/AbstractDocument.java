package gov.energy.nbc.car.model;

import com.mongodb.util.JSON;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.Document;

public abstract class AbstractDocument extends Document {

    public static final String ATTRIBUTE_KEY__ID = "_id";

    public AbstractDocument() {

    }

    public AbstractDocument(Object object) {

        String json = JSON.serialize(object);
        init(json);
    }

    public AbstractDocument(String json) {

        init(json);
    }

    protected abstract void init(String json);


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
}
