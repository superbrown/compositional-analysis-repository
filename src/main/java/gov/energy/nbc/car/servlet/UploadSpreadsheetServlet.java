//package gov.energy.nbc.car.servlet;
//
//import gov.energy.nbc.car.model.common.Metadata;
//import gov.energy.nbc.car.model.document.Dataset;
//import gov.energy.nbc.car.Settings;
//import gov.energy.nbc.car.businessService.BusinessServices;
//import gov.energy.nbc.car.businessService.TestMode;
//import gov.energy.nbc.car.model.common.Data;
//import gov.energy.nbc.car.model.common.StoredFile;
//import gov.energy.nbc.car.utilities.ServletBasedFileUploadUtility;
//import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.List;
//
///**
// * A servlet that implements the methods for uploading a worksheet file.
// *
// * @author James Albersheim
// *
// */
//public class UploadDatasetServlet extends HttpServlet implements
//		AppConstants {
//
//	/**
//	 * A serialization identifier.
//	 */
//	private static final long serialVersionUID = -4515434818206581128L;
//
//	/**
//	 * Overrides GenericServlet convenience method
//	 */
//	public void init(){
//	}
//
//	@Override
//	@SuppressWarnings("unchecked")
//	public void doPost(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//
//        // checks if the request actually contains upload file
//        if (ServletFileUpload.isMultipartContent(request) == false) {
//            ServletUtilities.printToResponse(response, "Request does not contain upload data");
//            return;
//        }
//
//		String datasetId = (String) request.getAttribute("datasetId");
//		String testMode = (String) request.getAttribute("testMode");
//
//		TestMode testModeValue = TestMode.value(testMode);
//
//		String json = BusinessServices.datasetService.getDataset(testModeValue, datasetId);
//		Dataset dataset = new Dataset(json);
//
//		Settings settings = BusinessServices.getSettings(testModeValue);
//
//		String fullDataFilesDirectoryPath = getServletContext().getRealPath("") + "/" + settings.getDataFilesDirectoryPath();
//
//        List<StoredFile> theFilesThatWereStored = null;
//        try {
//            theFilesThatWereStored = ServletBasedFileUploadUtility.saveFilesInTheRequest(request, fullDataFilesDirectoryPath);
//        }
//        catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//		Metadata metadata = dataset.getMetadata();
//		setTheFileThatWasUploaded(metadata, theFilesThatWereStored.get(0));
//
//		Data data = dataset.getData();
//
//		new Dataset(metadata, data);
//
//        System.out.print(theFilesThatWereStored);
//	}
//
//	private void setTheFileThatWasUploaded(Metadata metadata, ServletBasedFileUploadUtility.StoredFile theFileThatWasStored) {
//
//		StoredFile storedFile_mongoVersion = new StoredFile(
//				theFileThatWasStored.originalFileName,
//				theFileThatWasStored.storageLocation);
//		metadata.put(Metadata.ATTR_KEY__UPLOADED_FILE, storedFile_mongoVersion);
//	}
//}
