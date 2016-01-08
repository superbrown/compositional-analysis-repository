package gov.energy.nrel.dataRepositoryApp.bo.mongodb;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IBO;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbsBO implements IBO {

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;

    public AbsBO(DataRepositoryApplication dataRepositoryApplication) {

        this.dataRepositoryApplication = dataRepositoryApplication;
        init();
    }

    protected abstract void init();

    @Override
    public DataRepositoryApplication getDataRepositoryApplication() {
        return dataRepositoryApplication;
    }

    @Override
    public ISettings getSettings() {
        return dataRepositoryApplication.getSettings();
    }

    @Override
    public void setDataRepositoryApplication(DataRepositoryApplication dataRepositoryApplication) {
        this.dataRepositoryApplication = dataRepositoryApplication;
    }
}
