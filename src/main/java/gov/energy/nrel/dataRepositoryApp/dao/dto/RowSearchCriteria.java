package gov.energy.nbc.car.dao.dto;

import java.util.ArrayList;
import java.util.List;

public class RowSearchCriteria {

    private List<SearchCriterion> metadataSearchCriteria = new ArrayList();
    private List<SearchCriterion> dataSearchCriteria = new ArrayList();

    public void addCriterion_metadata(String name, Object value, ComparisonOperator comparisonOperator) {
        metadataSearchCriteria.add(new SearchCriterion(name, value, comparisonOperator));
    }

    public void addCriterion_data(String name, Object value, ComparisonOperator comparisonOperator) {

        dataSearchCriteria.add(new SearchCriterion(name, value, comparisonOperator));
    }

    public List<SearchCriterion> getMetadataSearchCriteria() {
        return metadataSearchCriteria;
    }

    public List<SearchCriterion> getDataSearchCriteria() {
        return dataSearchCriteria;
    }

    @Override
    public String toString() {
        return "RowSearchCriteria{" +
                "metadataSearchCriteria=" + metadataSearchCriteria +
                ", dataSearchCriteria=" + dataSearchCriteria +
                '}';
    }
}
