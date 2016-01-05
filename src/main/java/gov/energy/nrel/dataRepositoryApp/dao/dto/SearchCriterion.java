package gov.energy.nrel.dataRepositoryApp.dao.dto;


import org.apache.commons.lang3.StringUtils;

public class SearchCriterion {

    private String name;
    private Object value;
    private ComparisonOperator comparisonOperator;

    public SearchCriterion(String name, Object value, ComparisonOperator comparisonOperator) {

        this.name = name;
        this.value = value;
        this.comparisonOperator = comparisonOperator;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ComparisonOperator getComparisonOperator() {
        return comparisonOperator;
    }

    @Override
    public String toString() {
        return "SearchCriterion{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", comparisonOperator=" + comparisonOperator +
                '}';
    }

    public boolean containsEverthingNeededToDefineASearchFilter() {

        if (comparisonOperator == null) {
            return false;
        }

        if (StringUtils.isBlank(name)) {
            return false;
        }

        if (value == null) {
            return false;
        }

        return true;
    }
}
