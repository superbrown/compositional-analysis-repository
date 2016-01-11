package gov.energy.nrel.dataRepositoryApp.model.document;

import gov.energy.nrel.dataRepositoryApp.model.common.IMetadata;

public interface IDatasetDocument extends IThingWithAnId {

    String DISPLAY_FIELD__SOURCE_UUID = " Source UUID";

    IMetadata getMetadata();
}
