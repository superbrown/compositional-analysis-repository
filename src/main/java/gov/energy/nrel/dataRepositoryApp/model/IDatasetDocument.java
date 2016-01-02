package gov.energy.nrel.dataRepositoryApp.model;

public interface IDatasetDocument extends IThingWithAnId {

    IMetadata getMetadata();
}
