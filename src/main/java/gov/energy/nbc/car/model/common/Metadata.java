package gov.energy.nbc.car.model.common;

import com.mongodb.BasicDBObject;
import gov.energy.nbc.car.dao.DAOUtilities;
import gov.energy.nbc.car.model.AbstractDocument;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Metadata extends AbstractDocument {

    public static final String ATTRIBUTE_KEY__SAMPLE_TYPE = "sampleType";
    public static final String ATTRIBUTE_KEY__SUBMISSION_DATE = "submissionDate";
    public static final String ATTRIBUTE_KEY__SUBMITTER = "submitter";
    public static final String ATTRIBUTE_KEY__CHARGE_NUMBER = "chargeNumber";
    public static final String ATTRIBUTE_KEY__PROJECT_NAME = "projectName";
    public static final String ATTRIBUTE_KEY__COMMENTS = "comments";
    public static final String ATTRIBUTE_KEY__UPLOADED_FILE = "uploadedFile";
    public static final String ATTRIBUTE_KEY__ATTACHMENTS = "attachments";

    public Metadata(
            String sampleType,
            Date submissionDate,
            String submitter,
            String chargeNumber,
            String projectName,
            String comments,
            StoredFile uploadedFile,
            List<StoredFile> attachments) {

        super();

        init(sampleType,
                submissionDate,
                submitter,
                chargeNumber,
                projectName,
                comments,
                uploadedFile,
                attachments);
   }

    public Metadata(Document document) {
        super(document);
    }

    public Metadata(String json) {
        super(json);
    }

    private void init(String sampleType,
                      Date submissionDate,
                      String submitter,
                      String chargeNumber,
                      String projectName,
                      String comments,
                      StoredFile uploadedFile,
                      List<StoredFile> attachments) {

        put(ATTRIBUTE_KEY__SAMPLE_TYPE, sampleType);
        put(ATTRIBUTE_KEY__SUBMISSION_DATE, submissionDate);
        put(ATTRIBUTE_KEY__SUBMITTER, submitter);
        put(ATTRIBUTE_KEY__CHARGE_NUMBER, chargeNumber);
        put(ATTRIBUTE_KEY__PROJECT_NAME, projectName);
        put(ATTRIBUTE_KEY__COMMENTS, comments);
        put(ATTRIBUTE_KEY__UPLOADED_FILE, uploadedFile);
        put(ATTRIBUTE_KEY__ATTACHMENTS, attachments);

        verifyRequiredFieldsAreSet();
    }

    @Override
    protected void initWithJson(String json) {

        Document parsedJson = new Document((BasicDBObject) DAOUtilities.parse(json));
        init(parsedJson);
    }

    protected void init(Document document) {

        if (document == null) {
            return;
        }

        String sampleType = (String) document.get(ATTRIBUTE_KEY__SAMPLE_TYPE);
        Date submissionDate = (Date) document.get(ATTRIBUTE_KEY__SUBMISSION_DATE);
        String submitter = (String) document.get(ATTRIBUTE_KEY__SUBMITTER);
        String chargeNumber = (String) document.get(ATTRIBUTE_KEY__CHARGE_NUMBER);
        String projectName = (String) document.get(ATTRIBUTE_KEY__PROJECT_NAME);
        String comments = (String) document.get(ATTRIBUTE_KEY__COMMENTS);
        StoredFile uploadedFile = new StoredFile((BasicDBObject)document.get(ATTRIBUTE_KEY__UPLOADED_FILE));

        List<BasicDBObject> attachmentObjects = (List<BasicDBObject>) document.get(ATTRIBUTE_KEY__ATTACHMENTS);

        List<StoredFile> attachments = new ArrayList();

        if (attachmentObjects != null) {

            for (BasicDBObject attachmentObject : attachmentObjects) {

                attachments.add(new StoredFile(attachmentObject));
            }
        }

        init(sampleType,
             submissionDate,
             submitter,
             chargeNumber,
             projectName,
             comments,
             uploadedFile,
             attachments);
    }

    private void verifyRequiredFieldsAreSet() {
        verify(StringUtils.isNotBlank((String)this.get(ATTRIBUTE_KEY__SAMPLE_TYPE)), "sampleType is blank");
        verify(this.get(ATTRIBUTE_KEY__UPLOADED_FILE) != null, "spreadsheetPath is blank");
    }

    private void verify(boolean condition, String errorMessage) {
        if (condition == false) throw new IllegalArgumentException(errorMessage);
    }

    public String getSampleType() { return (String) get(ATTRIBUTE_KEY__SAMPLE_TYPE); }
    public String getSubmissionDate() { return (String) get(ATTRIBUTE_KEY__SUBMISSION_DATE); }
    public String getSubmitter() { return (String) get(ATTRIBUTE_KEY__SUBMITTER); }
    public String getChargeNumber() { return (String) get(ATTRIBUTE_KEY__CHARGE_NUMBER); }
    public String getProjectName() { return (String) get(ATTRIBUTE_KEY__PROJECT_NAME); }
    public String getComments() { return (String) get(ATTRIBUTE_KEY__COMMENTS); }
    public StoredFile getUploadedFile() { return (StoredFile) get(ATTRIBUTE_KEY__UPLOADED_FILE); }
    public List<StoredFile> getAttachments() { return (List<StoredFile>) get(ATTRIBUTE_KEY__ATTACHMENTS); }

}
