package gov.energy.nrel.dataRepositoryApp.bo.exception;


import gov.energy.nrel.dataRepositoryApp.dao.exception.UnknownEntity;

public class UnknownDataCatogory extends Exception {

    public UnknownDataCatogory() {
        super();
    }

    public UnknownDataCatogory(UnknownEntity e) {
        super(e);
    }
}
