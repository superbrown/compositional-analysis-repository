package gov.energy.nbc.car.bo.dto;


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
}
