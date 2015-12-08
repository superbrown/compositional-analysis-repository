package gov.energy.nbc.car.bo.mongodb;

import com.mongodb.BasicDBList;
import gov.energy.nbc.car.bo.IDataTypeBO;
import gov.energy.nbc.car.dao.dto.ComparisonOperator;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.restEndpoint.DataType;
import org.apache.log4j.Logger;
import org.bson.Document;

public class DataTypeBO implements IDataTypeBO {

    protected Logger log = Logger.getLogger(getClass());

    public DataTypeBO() {
    }


    @Override
    public String getInventoryOfComparisonOperators(DataType dataType) {

        BasicDBList basicDBList = new BasicDBList();

        if (dataType == DataType.NUMBER) {

            basicDBList.add(toNameValuePairDocument(ComparisonOperator.EQUALS.toString(), "is equal to"));
            basicDBList.add(toNameValuePairDocument(ComparisonOperator.GREATER_THAN.toString(), "is greater than"));
            basicDBList.add(toNameValuePairDocument(ComparisonOperator.GREATER_THAN_OR_EQUAL.toString(), "is greater than or equals"));
            basicDBList.add(toNameValuePairDocument(ComparisonOperator.LESS_THAN.toString(), "is less than"));
            basicDBList.add(toNameValuePairDocument(ComparisonOperator.LESS_THAN_OR_EQUAL.toString(), "is less than or equals"));
        }
        else if (dataType == DataType.STRING) {

            basicDBList.add(toNameValuePairDocument(ComparisonOperator.EQUALS.toString(), "exactly matches"));
            basicDBList.add(toNameValuePairDocument(ComparisonOperator.CONTAINS.toString(), "contains"));
        }
        else if (dataType == DataType.DATE) {

            basicDBList.add(toNameValuePairDocument(ComparisonOperator.EQUALS.toString(), "is"));
            basicDBList.add(toNameValuePairDocument(ComparisonOperator.LESS_THAN_OR_EQUAL.toString(), "is on or before"));
            basicDBList.add(toNameValuePairDocument(ComparisonOperator.GREATER_THAN_OR_EQUAL.toString(), "is on or after"));
        }
        else if (dataType == DataType.BOOLEAN) {

            basicDBList.add(toNameValuePairDocument(ComparisonOperator.EQUALS.toString(), "is"));
        }

        return DAOUtilities.toJSON(basicDBList);
    }

    @Override
    public String getInventoryOfDataTypes() {

        BasicDBList basicDBList = new BasicDBList();

        basicDBList.add(toNameValuePairDocument(DataType.NUMBER.toString(), "number"));
        basicDBList.add(toNameValuePairDocument(DataType.STRING.toString(), "string"));
        basicDBList.add(toNameValuePairDocument(DataType.DATE.toString(), "date"));
        basicDBList.add(toNameValuePairDocument(DataType.BOOLEAN.toString(), "boolean"));

        return DAOUtilities.toJSON(basicDBList);
    }

    protected Document toNameValuePairDocument(String name, String value) {
        Document document = new Document();
        document.put("id", name);
        document.put("label", value);
        return document;
    }
}
