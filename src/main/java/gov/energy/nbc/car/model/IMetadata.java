package gov.energy.nbc.car.model;

import java.util.List;


public interface IMetadata {

    String getDataCategory();

    String getSubmissionDate();

    String getSubmitter();

    String getChargeNumber();

    String getProjectName();

    String getComments();

    IStoredFile getUploadedFile();

    List<IStoredFile> getAttachments();
}
