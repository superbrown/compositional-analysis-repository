package gov.energy.nbc.car.model;

import java.util.Date;
import java.util.List;


public interface IMetadata {

    String ATTR_KEY__DATA_CATEGORY = " Data Category";
    String ATTR_KEY__SUBMISSION_DATE = " Submission Date";
    String ATTR_KEY__SUBMITTER = " Submitter";
    String ATTR_KEY__CHARGE_NUMBER = " Charge Number";
    String ATTR_KEY__PROJECT_NAME = " Project Name";
    String ATTR_KEY__COMMENTS = " Comments";
    String ATTR_KEY__UPLOADED_FILE = " Uploaded File";
    String ATTR_KEY__NAME_OF_SHEET_CONTAINING_DATA = "Name Of Sheet Containing Data";
    String ATTR_KEY__ATTACHMENTS = " Attachments";

    String getDataCategory();

    Date getSubmissionDate();

    String getSubmitter();

    String getChargeNumber();

    String getProjectName();

    String getNameOfSheetContainingData();

    String getComments();

    IStoredFile getUploadedFile();

    List<IStoredFile> getAttachments();

    String setDataCategory(String value);

    Date setSubmissionDate(Date value);

    String setSubmitter(String value);

    String setChargeNumber(String value);

    String setProjectName(String value);

    String setComments(String value);

    String setNameOfSheetContainingData(String value);

    IStoredFile setUploadedFile(IStoredFile value);

    List<IStoredFile> setAttachments(List<IStoredFile> value);
}
