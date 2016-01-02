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
    String ATTR_KEY__SOURCE_DOCUMENT = " Source Document";
    String ATTR_KEY__SUB_DOCUMENT_CONTAINING_DATA = "Sub-Document Containing Data";
    String ATTR_KEY__ATTACHMENTS = " Attachments";

    String getDataCategory();

    Date getSubmissionDate();

    String getSubmitter();

    String getChargeNumber();

    String getProjectName();

    String getNameOfSubdocumentContainingData();

    String getComments();

    IStoredFile getSourceDocument();

    List<IStoredFile> getAttachments();

    String setDataCategory(String value);

    Date setSubmissionDate(Date value);

    String setSubmitter(String value);

    String setChargeNumber(String value);

    String setProjectName(String value);

    String setComments(String value);

    String setNameOfSubdocumentContainingData(String value);

    IStoredFile setSourceDocument(IStoredFile value);

    List<IStoredFile> setAttachments(List<IStoredFile> value);
}
