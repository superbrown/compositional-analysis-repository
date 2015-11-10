package gov.energy.nbc.car.model;

public interface IDatasetDocument extends IThingWithAnId {

    IMetadata getMetadata();

    String getDataCategory();
}
