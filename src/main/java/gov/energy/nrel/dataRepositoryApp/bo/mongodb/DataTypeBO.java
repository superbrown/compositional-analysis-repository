package gov.energy.nrel.dataRepositoryApp.bo.mongodb;

import com.mongodb.BasicDBList;
import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDataTypeBO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.ComparisonOperator;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.restEndpoint.DataType;
import org.apache.log4j.Logger;
import org.bson.Document;

public class DataTypeBO extends AbsBO implements IDataTypeBO {

    protected static Logger log = Logger.getLogger(DataTypeBO.class);

    public DataTypeBO(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    @Override
    protected void init() {
    }

    @Override
    public String getInventoryOfComparisonOperators(DataType dataType) {

        assert (dataType != null);

        BasicDBList basicDBList = new BasicDBList();

        if (dataType == DataType.NUMBER) {

            basicDBList.add(toNameValuePairDocument(ComparisonOperator.EQUALS.toString(), "is equal to"));
            basicDBList.add(toNameValuePairDocument(ComparisonOperator.GREATER_THAN.toString(), "is greater than"));
            basicDBList.add(toNameValuePairDocument(ComparisonOperator.GREATER_THAN_OR_EQUAL.toString(), "is greater than or equal to"));
            basicDBList.add(toNameValuePairDocument(ComparisonOperator.LESS_THAN.toString(), "is less than"));
            basicDBList.add(toNameValuePairDocument(ComparisonOperator.LESS_THAN_OR_EQUAL.toString(), "is less than or equal to"));
        }
        else if (dataType == DataType.STRING) {

            basicDBList.add(toNameValuePairDocument(ComparisonOperator.EQUALS.toString(), "exactly matches"));
            basicDBList.add(toNameValuePairDocument(ComparisonOperator.CONTAINS.toString(), "contains"));
        }
        else if (dataType == DataType.DATE) {

            basicDBList.add(toNameValuePairDocument(ComparisonOperator.EQUALS.toString(), "is on"));
            basicDBList.add(toNameValuePairDocument(ComparisonOperator.LESS_THAN_OR_EQUAL.toString(), "is on or before"));
            basicDBList.add(toNameValuePairDocument(ComparisonOperator.GREATER_THAN_OR_EQUAL.toString(), "is on or after"));
        }
        else if (dataType == DataType.BOOLEAN) {

            basicDBList.add(toNameValuePairDocument(ComparisonOperator.EQUALS.toString(), "is"));
        }
        else {
            throw new RuntimeException("App needs to be modified to handle data type: " + dataType);
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
