//package gov.energy.nbc.car.dao.mongodb.dto;
//
//public class MongoFieldName {
//
//    private static MongoFieldNameEncoder mongoFieldNameEncoder = new MongoFieldNameEncoder();
//
//    private String encodedValue;
//
//    public MongoFieldName(String value) {
//
//        this.encodedValue = mongoFieldNameEncoder.toMongoSafeFieldName(value);
//    }
//
//    public String getMongoSafeValue() {
//
//        return this.encodedValue;
//    }
//
//    public String getClientSideName() {
//
//        return mongoFieldNameEncoder.toClientSideFieldName(this.encodedValue);
//    }
//}
