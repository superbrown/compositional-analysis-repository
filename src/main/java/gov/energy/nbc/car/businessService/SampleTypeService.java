package gov.energy.nbc.car.businessService;

import com.mongodb.client.FindIterable;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.dao.DAOUtilities;
import gov.energy.nbc.car.dao.DeleteResults;
import gov.energy.nbc.car.dao.SampleTypeDocumentDAO;
import gov.energy.nbc.car.model.document.SampleTypeDocument;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

public class SampleTypeService {

    Logger log = Logger.getLogger(this.getClass());

    protected SampleTypeDocumentDAO sampleTypeDocumentDAO;
    protected SampleTypeDocumentDAO sampleTypeDocumentDAO_FOR_UNIT_TESTING_PURPOSES;

    protected BusinessServiceUtilities businessServiceUtilities;

    public SampleTypeService(Settings settings,
                             Settings settings_forUnitTestingPurposes) {

        sampleTypeDocumentDAO = new SampleTypeDocumentDAO(settings);
        sampleTypeDocumentDAO_FOR_UNIT_TESTING_PURPOSES = new SampleTypeDocumentDAO(settings_forUnitTestingPurposes);

        businessServiceUtilities = new BusinessServiceUtilities();
    }

    public String getSampleType(TestMode testMode,
                                 String sampleTypeId) {

        SampleTypeDocument sampleTypeDocument = getSampleTypeDocument(testMode, sampleTypeId);
        if (sampleTypeDocument == null) { return null; }

        String jsonOut = DAOUtilities.serialize(sampleTypeDocument);
        return jsonOut;
    }

    public String getSampleTypeWithName(TestMode testMode,
                                        String name) {

        SampleTypeDocument sampleTypeDocument = getSampleTypeDAO(testMode).getByName(name);
        if (sampleTypeDocument == null) { return null; }

        String jsonOut = DAOUtilities.serialize(sampleTypeDocument);
        return jsonOut;
    }

    public String getAllSampleTypes(TestMode testMode) {

        FindIterable<Document> sampleTypeDocuments = getSampleTypeDAO(testMode).getAll();

        String jsonOut = DAOUtilities.serialize(sampleTypeDocuments);
        return jsonOut;
    }

    public long deleteSampleType(TestMode testMode,
                                  String sampleTypeId) throws DeletionFailure {

        DeleteResults deleteResults = getSampleTypeDAO(testMode).delete(sampleTypeId);

        if (deleteResults.wasAcknowledged() == false) {
            throw new DeletionFailure(deleteResults);
        }

        long numberOfObjectsDeleted = deleteResults.getDeletedCount();
        return numberOfObjectsDeleted;
    }

    public String addSampleType(TestMode testMode,
                                 String jsonIn) {

        SampleTypeDocumentDAO sampleTypeDocumentDAO = getSampleTypeDAO(testMode);

        SampleTypeDocument sampleTypeDocument = new SampleTypeDocument(jsonIn);
        ObjectId objectId = sampleTypeDocumentDAO.add(sampleTypeDocument);

        return objectId.toHexString();
    }

    public SampleTypeDocument getSampleTypeDocument(TestMode testMode,
                                                      String sampleTypeId) {

        SampleTypeDocument document = getSampleTypeDAO(testMode).get(sampleTypeId);
        return document;
    }

    public SampleTypeDocumentDAO getSampleTypeDAO(TestMode testMode) {

        if (testMode == TestMode.NOT_TEST_MODE) {
            return sampleTypeDocumentDAO;
        }
        else {
            return sampleTypeDocumentDAO_FOR_UNIT_TESTING_PURPOSES;
        }
    }
}
