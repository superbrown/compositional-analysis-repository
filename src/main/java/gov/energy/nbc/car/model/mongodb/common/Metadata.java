package gov.energy.nbc.car.model.mongodb.common;

import com.mongodb.BasicDBObject;
import gov.energy.nbc.car.dao.mongodb.DAOUtilities;
import gov.energy.nbc.car.model.mongodb.AbstractDocument;
import gov.energy.nbc.car.model.IMetadata;
import gov.energy.nbc.car.model.IStoredFile;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Metadata extends AbstractDocument implements IMetadata {

    public static final String ATTR_KEY__SAMPLE_TYPE = "dataCategory";
    public static final String ATTR_KEY__SUBMISSION_DATE = "submissionDate";
    public static final String ATTR_KEY__SUBMITTER = "submitter";
    public static final String ATTR_KEY__CHARGE_NUMBER = "chargeNumber";
    public static final String ATTR_KEY__PROJECT_NAME = "projectName";
    public static final String ATTR_KEY__COMMENTS = "comments";
    public static final String ATTR_KEY__UPLOADED_FILE = "uploadedFile";
    public static final String ATTR_KEY__ATTACHMENTS = "attachments";

    public Metadata(
            String dataCategory,
            Date submissionDate,
            String submitter,
            String chargeNumber,
            String projectName,
            String comments,
            IStoredFile uploadedFile,
            List<IStoredFile> attachments) {

        super();

        init(dataCategory,
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

    private void init(String dataCategory,
                      Date submissionDate,
                      String submitter,
                      String chargeNumber,
                      String projectName,
                      String comments,
                      IStoredFile uploadedFile,
                      List<IStoredFile> attachments) {

        put(ATTR_KEY__SAMPLE_TYPE, dataCategory);
        put(ATTR_KEY__SUBMISSION_DATE, submissionDate);
        put(ATTR_KEY__SUBMITTER, submitter);
        put(ATTR_KEY__CHARGE_NUMBER, chargeNumber);
        put(ATTR_KEY__PROJECT_NAME, projectName);
        put(ATTR_KEY__COMMENTS, comments);
        put(ATTR_KEY__UPLOADED_FILE, uploadedFile);
        put(ATTR_KEY__ATTACHMENTS, attachments);

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

        initObjectId(document);

        String dataCategory = (String) document.get(ATTR_KEY__SAMPLE_TYPE);
        Date submissionDate = (Date) document.get(ATTR_KEY__SUBMISSION_DATE);
        String submitter = (String) document.get(ATTR_KEY__SUBMITTER);
        String chargeNumber = (String) document.get(ATTR_KEY__CHARGE_NUMBER);
        String projectName = (String) document.get(ATTR_KEY__PROJECT_NAME);
        String comments = (String) document.get(ATTR_KEY__COMMENTS);

        IStoredFile uploadedFile = null;
        Object o = document.get(ATTR_KEY__UPLOADED_FILE);
        // It's not clear to me why this is not always the same type of object
        if (o instanceof BasicDBObject) {
            uploadedFile = new StoredFile((BasicDBObject) o);
        }
        else if (o instanceof Document) {
            uploadedFile = new StoredFile((Document) o);
        }

        List<Document> attachmentObjects = (List<Document>) document.get(ATTR_KEY__ATTACHMENTS);

        List<IStoredFile> attachments = new ArrayList();

        if (attachmentObjects != null) {

            for (Document attachmentObject : attachmentObjects) {

                attachments.add(new StoredFile(attachmentObject));
            }
        }

        init(dataCategory,
             submissionDate,
             submitter,
             chargeNumber,
             projectName,
             comments,
             uploadedFile,
             attachments);
    }

    private void verifyRequiredFieldsAreSet() {
        verify(StringUtils.isNotBlank((String)this.get(ATTR_KEY__SAMPLE_TYPE)), "dataCategory is blank");
        verify(this.get(ATTR_KEY__UPLOADED_FILE) != null, "datasetPath is blank");
    }

    private void verify(boolean condition, String errorMessage) {
        if (condition == false) throw new IllegalArgumentException(errorMessage);
    }

    @Override
    public String getDataCategory() { return (String) get(ATTR_KEY__SAMPLE_TYPE); }
    @Override
    public String getSubmissionDate() { return (String) get(ATTR_KEY__SUBMISSION_DATE); }
    @Override
    public String getSubmitter() { return (String) get(ATTR_KEY__SUBMITTER); }
    @Override
    public String getChargeNumber() { return (String) get(ATTR_KEY__CHARGE_NUMBER); }
    @Override
    public String getProjectName() { return (String) get(ATTR_KEY__PROJECT_NAME); }
    @Override
    public String getComments() { return (String) get(ATTR_KEY__COMMENTS); }
    @Override
    public IStoredFile getUploadedFile() { return (IStoredFile) get(ATTR_KEY__UPLOADED_FILE); }
    @Override
    public List<IStoredFile> getAttachments() { return (List<IStoredFile>) get(ATTR_KEY__ATTACHMENTS); }

}
