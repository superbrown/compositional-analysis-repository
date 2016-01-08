package gov.energy.nrel.dataRepositoryApp.bo;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;


public interface IBO {

    DataRepositoryApplication getDataRepositoryApplication();

    ISettings getSettings();

    void setDataRepositoryApplication(DataRepositoryApplication dataRepositoryApplication);
}
