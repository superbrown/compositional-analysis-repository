package gov.energy.nbc.car.dao.mongodb;

public class MongoFieldNameEncoder {

    public static final String DECIMAL_POINT_CHARACTER = ".";
    public static final String DECIMAL_POINT_SUBSTITUTE = "_Dp_";

    public static final String QUESTION_MARK_CHARACTER = "?";
    public static final String QUESTION_MARK_SUBSTITUTE = "_Qm_";

    public static final String SPACE_CHARACTER = " ";
    public static final String SPACE_SUBSTITUTE = "_Sp_";

    public MongoFieldNameEncoder() {
    }


    public static String toMongoSafeFieldName(String value) {

        if (value.startsWith(QUESTION_MARK_CHARACTER)) {
            value = QUESTION_MARK_SUBSTITUTE + value.substring(1);
        }

        value = value.replace(DECIMAL_POINT_CHARACTER, DECIMAL_POINT_SUBSTITUTE);

        value = value.replace(SPACE_CHARACTER, SPACE_SUBSTITUTE);

        return value;
    }

    public static String toClientSideFieldName(String value) {

        if (value.startsWith(QUESTION_MARK_SUBSTITUTE)) {
            value = QUESTION_MARK_CHARACTER + value.substring(1);
        }

        value = value.replace(DECIMAL_POINT_SUBSTITUTE, DECIMAL_POINT_CHARACTER);

        value = value.replace(SPACE_SUBSTITUTE, SPACE_CHARACTER);

        return value;
    }
}
