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

    public Metadata() {
        super();
    }

    public Metadata(
            String dataCategory,
            Date submissionDate,
            String submitter,
            String chargeNumber,
            String projectName,
            String comments,
            IStoredFile sourceDocument,
            String nameOfSubdocumentContainingDataIfApplicable,
            List<IStoredFile> attachments) {

        super();

        init(dataCategory,
                submissionDate,
                submitter,
                chargeNumber,
                projectName,
                comments,
                sourceDocument,
                nameOfSubdocumentContainingDataIfApplicable,
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
                      IStoredFile sourceDocument,
                      String nameOfSubdocumentContainingDataIfApplicable,
                      List<IStoredFile> attachments) {

        put(ATTR_KEY__DATA_CATEGORY, dataCategory);
        put(ATTR_KEY__SUBMISSION_DATE, submissionDate);
        put(ATTR_KEY__SUBMITTER, submitter);
        put(ATTR_KEY__CHARGE_NUMBER, chargeNumber);
        put(ATTR_KEY__PROJECT_NAME, projectName);
        put(ATTR_KEY__COMMENTS, comments);
        put(ATTR_KEY__SOURCE_DOCUMENT, sourceDocument);
        put(ATTR_KEY__ATTACHMENTS, attachments);
        put(ATTR_KEY__SUB_DOCUMENT_CONTAINING_DATA, nameOfSubdocumentContainingDataIfApplicable);

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

        String dataCategory = (String) document.get(ATTR_KEY__DATA_CATEGORY);
        Date submissionDate = (Date) document.get(ATTR_KEY__SUBMISSION_DATE);
        String submitter = (String) document.get(ATTR_KEY__SUBMITTER);
        String chargeNumber = (String) document.get(ATTR_KEY__CHARGE_NUMBER);
        String projectName = (String) document.get(ATTR_KEY__PROJECT_NAME);
        String comments = (String) document.get(ATTR_KEY__COMMENTS);
        String nameOfSubdocumentContainingDataIfApplicable = (String) document.get(ATTR_KEY__SUB_DOCUMENT_CONTAINING_DATA);

        IStoredFile sourceDocument = null;
        Object o = document.get(ATTR_KEY__SOURCE_DOCUMENT);
        // It's not clear to me why this is not always the same type of object
        if (o instanceof BasicDBObject) {
            sourceDocument = new StoredFile((BasicDBObject) o);
        }
        else if (o instanceof Document) {
            sourceDocument = new StoredFile((Document) o);
        }

        List<BasicDBObject> attachmentObjects = (List<BasicDBObject>) document.get(ATTR_KEY__ATTACHMENTS);

        List<IStoredFile> attachments = new ArrayList();

        if (attachmentObjects != null) {

            for (BasicDBObject attachmentObject : attachmentObjects) {

                attachments.add(new StoredFile(attachmentObject));
            }
        }

        init(dataCategory,
             submissionDate,
             submitter,
             chargeNumber,
             projectName,
             comments,
             sourceDocument,
             nameOfSubdocumentContainingDataIfApplicable,
             attachments);
    }

    private void verifyRequiredFieldsAreSet() {
        verify(StringUtils.isNotBlank((String)this.get(ATTR_KEY__DATA_CATEGORY)), "dataCategory is blank");
        verify(this.get(ATTR_KEY__SOURCE_DOCUMENT) != null, "datasetPath is blank");
    }

    private void verify(boolean condition, String errorMessage) {
        if (condition == false) throw new IllegalArgumentException(errorMessage);
    }

    @Override
    public String getDataCategory() { return (String) get(ATTR_KEY__DATA_CATEGORY); }
    @Override
    public Date getSubmissionDate() { return (Date) get(ATTR_KEY__SUBMISSION_DATE); }
    @Override
    public String getSubmitter() { return (String) get(ATTR_KEY__SUBMITTER); }
    @Override
    public String getChargeNumber() { return (String) get(ATTR_KEY__CHARGE_NUMBER); }
    @Override
    public String getProjectName() { return (String) get(ATTR_KEY__PROJECT_NAME); }
    @Override
    public String getNameOfSubdocumentContainingData() { return (String) get(ATTR_KEY__SUB_DOCUMENT_CONTAINING_DATA); }
    @Override
    public String getComments() { return (String) get(ATTR_KEY__COMMENTS); }
    @Override
    public IStoredFile getSourceDocument() { return (IStoredFile) get(ATTR_KEY__SOURCE_DOCUMENT); }
    @Override
    public List<IStoredFile> getAttachments() { return (List<IStoredFile>) get(ATTR_KEY__ATTACHMENTS); }


    @Override
    public String setDataCategory(String value) { return (String)  put(ATTR_KEY__DATA_CATEGORY, value); }
    @Override
    public Date setSubmissionDate(Date value) { return (Date)  put(ATTR_KEY__SUBMISSION_DATE, value); }
    @Override
    public String setSubmitter(String value) { return (String)  put(ATTR_KEY__SUBMITTER, value); }
    @Override
    public String setChargeNumber(String value) { return (String)  put(ATTR_KEY__CHARGE_NUMBER, value); }
    @Override
    public String setProjectName(String value) { return (String)  put(ATTR_KEY__PROJECT_NAME, value); }
    @Override
    public String setComments(String value) { return (String)  put(ATTR_KEY__COMMENTS, value); }
    @Override
    public String setNameOfSubdocumentContainingData(String value) { return (String)  put(ATTR_KEY__SUB_DOCUMENT_CONTAINING_DATA, value); }
    @Override
    public IStoredFile setSourceDocument(IStoredFile value) { return (IStoredFile)  put(ATTR_KEY__SOURCE_DOCUMENT, value); }
    @Override
    public List<IStoredFile> setAttachments(List<IStoredFile> value) { return (List<IStoredFile>)  put(ATTR_KEY__ATTACHMENTS, value); }

    public static boolean isAMetadataFieldName(String name) {

        return ATTR_KEY__DATA_CATEGORY.equals(name) ||
                ATTR_KEY__ATTACHMENTS.equals(name) ||
                ATTR_KEY__CHARGE_NUMBER.equals(name) ||
                ATTR_KEY__COMMENTS.equals(name) ||
                ATTR_KEY__PROJECT_NAME.equals(name) ||
                ATTR_KEY__SUBMISSION_DATE.equals(name) ||
                ATTR_KEY__SUBMITTER.equals(name) ||
                ATTR_KEY__SUB_DOCUMENT_CONTAINING_DATA.equals(name) ||
                ATTR_KEY__SOURCE_DOCUMENT.equals(name);
    }
}
