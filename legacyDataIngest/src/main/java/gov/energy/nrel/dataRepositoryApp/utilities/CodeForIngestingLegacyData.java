package gov.energy.nrel.dataRepositoryApp.utilities;

import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.DatasetReader_ExcelWorkbook;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.NotAnExcelWorkbook;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Mike Brown (mike.public@superbrown.com) for the National Renewable Energy Laboratory (NREL).
 */
public class CodeForIngestingLegacyData {

    public static void main(String[] args) {

        // Set these to what you want them to be:

        String appBaseURL = "http://localhost:8080/data-repository-app";
        String pathToDirectoryContainingDataFiles = "C:/projects/legacyDataToBeIngested/WOLFRUM";
        String pathToSpreadsheetContainingMetadata = "C:/projects/legacyDataToBeIngested/WOLFRUM/code/Book1.xlsx";
        String dataCategory = "Biomass";

        try {

            List<DatasetMetadata> datasetMetadataList = extractDatasetMetadata(pathToSpreadsheetContainingMetadata);

            for (DatasetMetadata datasetMetadata : datasetMetadataList) {

                CloseableHttpClient closeableHttpClient = null;
                try {
                    // specify the get request
                    HttpPost postRequest = new HttpPost(appBaseURL + "/api/v01/addDataset");

                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();

                    builder.addTextBody("dataCategory", dataCategory);

                    builder.addTextBody("submitter", datasetMetadata.submitter);

                    if (datasetMetadata.comments != null) {
                        builder.addTextBody("comments", datasetMetadata.comments);
                    }

                    if (datasetMetadata.projectName != null) {
                        builder.addTextBody("projectName", datasetMetadata.projectName);
                    }

                    if (datasetMetadata.chargeNumber != null) {
                        builder.addTextBody("chargeNumber", datasetMetadata.chargeNumber);
                    }

                    if (datasetMetadata.projectName != null) {
                        builder.addTextBody("projectName", datasetMetadata.projectName);
                    }

                    builder.addTextBody("nameOfSubdocumentContainingDataIfApplicable", "Digestion");

                    String dateString = Utilities.toString(datasetMetadata.submissionDate, "yyyy-MM-dd");
                    builder.addTextBody("submissionDate", dateString + "T07:00:00.000Z");

                    String pathToSourceDocument = pathToDirectoryContainingDataFiles + "/" + datasetMetadata.sourceDocument;

                    if ((new File(pathToSourceDocument)).exists() == false) {
                        System.out.println("File is not found: " + pathToSourceDocument);
                        continue;
                    }

                    builder.addBinaryBody("sourceDocument", new File(pathToSourceDocument));

                    HttpEntity multipart = builder.build();
                    postRequest.setEntity(multipart);

                    closeableHttpClient = HttpClients.createDefault();
                    CloseableHttpResponse response = closeableHttpClient.execute(postRequest);

                    if (response.getStatusLine().getStatusCode() != 200) {
                        System.out.println("FAILED! " + datasetMetadata);
                    } else {
//                        System.out.println("SUCCESS: " + datasetMetadata.sourceDocument);
                    }
                }
                catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    if (closeableHttpClient != null) {
                        try {
                            closeableHttpClient.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<DatasetMetadata> extractDatasetMetadata(String metadataFilePath)
            throws IOException, UnsupportedFileExtension, NotAnExcelWorkbook {

        File file = new File(metadataFilePath);
        FileInputStream fileInputStream = new FileInputStream(file);

        Workbook metadata = DatasetReader_ExcelWorkbook.createWorkbookObject(fileInputStream, metadataFilePath);

        Sheet sheet = metadata.getSheet("USE THIS");

        List<DatasetMetadata> datasetMetadata = new ArrayList<>();
        for (int rowIndex = 1; rowIndex < sheet.getLastRowNum(); rowIndex++) {

            Row row = sheet.getRow(rowIndex);

            String sourcDocument = row.getCell(0).getStringCellValue();
            Date submissionDate = row.getCell(1).getDateCellValue();
            Cell cell = row.getCell(2);

            String submittedBy = null;
            if (cell == null) {
                submittedBy = "(user did not enter)";
            }
            else {
                submittedBy = cell.getStringCellValue();
            }

            String projectName = getOptionalStringValue(row.getCell(3));
            String chargeNumber = getOptionalStringValue(row.getCell(4));
            String comments = getOptionalStringValue(row.getCell(5));

            datasetMetadata.add(new DatasetMetadata(sourcDocument, submissionDate, submittedBy, projectName, chargeNumber, comments));
        }

        return datasetMetadata;
    }

    private static String getOptionalStringValue(Cell cell) {

        if (cell == null) {
            return null;
        }

        String value;

        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            value = String.valueOf(cell.getNumericCellValue());
        }
        else {
            value = cell.getStringCellValue();
        }

        return value;
    }

    private static class DatasetMetadata {

        public final String sourceDocument;
        public final Date submissionDate;
        public final String submitter;
        public final String projectName;
        public final String chargeNumber;
        public final String comments;

        public DatasetMetadata(String sourceDocument, Date submissionDate, String submitter, String projectName, String chargeNumber, String comments) {

            this.sourceDocument = sourceDocument;
            this.submissionDate = submissionDate;
            this.submitter = submitter;
            this.projectName = projectName;
            this.chargeNumber = chargeNumber;
            this.comments = comments;
        }

        @Override
        public String toString() {
            return "DatasetMetadata{" +
                    "filename='" + sourceDocument + '\'' +
                    ", submissionDate=" + submissionDate +
                    ", submittedBy='" + submitter + '\'' +
                    ", projectName='" + projectName + '\'' +
                    ", chargeNumber='" + chargeNumber + '\'' +
                    ", comments='" + comments + '\'' +
                    '}';
        }
    }
}
