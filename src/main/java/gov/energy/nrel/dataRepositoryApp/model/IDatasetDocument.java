package gov.energy.nrel.dataRepositoryApp.model;

public interface IDatasetDocument extends IThingWithAnId {

    String DISPLAY_FIELD__SOURCE_UUID = " Source UUID";

    IMetadata getMetadata();
}
