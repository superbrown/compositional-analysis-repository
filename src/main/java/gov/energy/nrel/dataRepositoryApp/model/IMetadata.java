package gov.energy.nrel.dataRepositoryApp.model;

import java.util.Date;
import java.util.List;


public interface IMetadata {

    String getDataCategory();

    Date getSubmissionDate();

    String getSubmitter();

    String getChargeNumber();

    String getProjectName();

    String getComments();

    IStoredFile getSourceDocument();

    /**
     * This is the sub-document containing the data.  For instance, an Excel workbook contains sheets.
     */
    String getSubdocumentName();

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
