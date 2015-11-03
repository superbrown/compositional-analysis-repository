package gov.energy.nbc.car.fileReader;

public class MongoFieldNameEncoder {

    public static final String DECIMAL_POINT_CHARACTER = ".";
    public static final String DECIMAL_POINT_SUBSTITUTE = "[DP]";

    public static final String QUESTION_MARK_CHARACTER = "?";
    public static final String QUESTION_MARK_SUBSTITUTE = "[QM]";

    public MongoFieldNameEncoder() {
    }


    public String encode(String value) {

        if (value.startsWith(QUESTION_MARK_CHARACTER)) {
            value = QUESTION_MARK_SUBSTITUTE + value.substring(1);
        }

        value = value.replace(DECIMAL_POINT_CHARACTER, DECIMAL_POINT_SUBSTITUTE);

        return value;
    }

    public String decode(String value) {

        if (value.startsWith(QUESTION_MARK_SUBSTITUTE)) {
            value = QUESTION_MARK_CHARACTER + value.substring(1);
        }

        value = value.replace(DECIMAL_POINT_SUBSTITUTE, DECIMAL_POINT_CHARACTER);

        return value;
    }
}
