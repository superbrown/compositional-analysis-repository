package gov.energy.nbc.car.model;

import java.util.Date;
import java.util.List;


public interface IMetadata {

    String getDataCategory();

    Date getSubmissionDate();

    String getSubmitter();

    String getChargeNumber();

    String getProjectName();

    String getComments();

    IStoredFile getUploadedFile();

    List<IStoredFile> getAttachments();

    String setDataCategory(String value);

    Date setSubmissionDate(Date value);

    String setSubmitter(String value);

    String setChargeNumber(String value);

    String setProjectName(String value);

    String setComments(String value);

    IStoredFile setUploadedFile(IStoredFile value);

    List<IStoredFile> setAttachments(List<IStoredFile> value);
}
