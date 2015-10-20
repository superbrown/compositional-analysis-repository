package gov.energy.nbc.spreadsheet.model;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class AbstractBasicDBObject extends BasicDBObject {

    public AbstractBasicDBObject() {

    }

    public AbstractBasicDBObject(Object object) {

        String json = JSON.serialize(object);
        init(json);
    }

    public AbstractBasicDBObject(String json) {

        init(json);
    }

    protected abstract void init(String json);


    public String toJson() {

        return JSON.serialize(this);
    }

    public String getJson() {

        return this.toJson();
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
