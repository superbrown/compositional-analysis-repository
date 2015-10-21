package gov.energy.nbc.car.servlet;

import gov.energy.nbc.car.model.common.Metadata;
import gov.energy.nbc.car.model.document.SpreadsheetDocument;
import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.businessService.BusinessServices;
import gov.energy.nbc.car.businessService.TestMode;
import gov.energy.nbc.car.model.common.Data;
import gov.energy.nbc.car.model.common.StoredFile;
import gov.energy.nbc.car.utilities.FileUploadUtility;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * A servlet that implements the methods for uploading a worksheet file.
 *
 * @author James Albersheim
 *
 */
public class UploadSpreadsheetServlet extends HttpServlet implements
		AppConstants {

	/**
	 * A serialization identifier.
	 */
	private static final long serialVersionUID = -4515434818206581128L;

	/**
	 * Overrides GenericServlet convenience method
	 */
	public void init(){
	}

	@Override
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

        // checks if the request actually contains upload file
        if (ServletFileUpload.isMultipartContent(request) == false) {
            ServletUtilities.printToResponse(response, "Request does not contain upload data");
            return;
        }

		String spreadsheetId = (String) request.getAttribute("spreadsheetId");
		String testMode = (String) request.getAttribute("testMode");

		TestMode testModeValue = TestMode.value(testMode);

		String json = BusinessServices.spreadsheetService.getSpreadsheet(testModeValue, spreadsheetId);
		SpreadsheetDocument spreadsheetDocument = new SpreadsheetDocument(json);

		Settings settings = BusinessServices.getSettings(testModeValue);

		String fullDataFilesDirectoryPath = getServletContext().getRealPath("") + "/" + settings.getDataFilesDirectoryPath();

        List<FileUploadUtility.StoredFile> theFilesThatWereStored = null;
        try {
            theFilesThatWereStored = FileUploadUtility.saveFilesInTheRequest(request, fullDataFilesDirectoryPath);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

		Metadata metadata = spreadsheetDocument.getMetadata();
		setTheFileThatWasUploaded(metadata, theFilesThatWereStored.get(0));

		Data data = spreadsheetDocument.getData();

		new SpreadsheetDocument(metadata, data);

        System.out.print(theFilesThatWereStored);
	}

	private void setTheFileThatWasUploaded(Metadata metadata, FileUploadUtility.StoredFile theFileThatWasStored) {

		StoredFile storedFile_mongoVersion = new StoredFile(
				theFileThatWasStored.originalFileName,
				theFileThatWasStored.storageLocation);
		metadata.put(Metadata.ATTRIBUTE_KEY__UPLOADED_FILE, storedFile_mongoVersion);
	}
}
