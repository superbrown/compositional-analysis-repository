package gov.energy.nrel.dataRepositoryApp.restEndpoint;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IRowBO;
import gov.energy.nrel.dataRepositoryApp.bo.ResultsMode;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownRow;
import gov.energy.nrel.dataRepositoryApp.utilities.valueSanitizer.IValueSanitizer;
import gov.energy.nrel.dataRepositoryApp.utilities.Utilities;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_Rows extends EndpointController {

    protected static Logger log = Logger.getLogger(Endpoints_Rows.class);

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;


    @RequestMapping(
            value="/api/v02/rows",
            method = RequestMethod.POST,
            produces = "application/json")
    public ResponseEntity getRows(
            @RequestBody String query) throws CleanupOperationIsOccurring {

        // NOTE: This is logically a GET operation. It's a POST because data must be sent in the
        // requuest body.

        throwExceptionIfCleanupOperationsIsOccurring();

        String rows = getRowBO().getRows(query, ResultsMode.INCLUDE_ONLY_DATA_COLUMNS_BEING_FILTERED_UPON);

        return create_SUCCESS_response(rows);
    }

    @RequestMapping(
            value="/api/v02/rows/flat",
            method = RequestMethod.POST,
            produces = "application/json")
    public ResponseEntity getRowsFlat(
            @RequestBody String query) throws CleanupOperationIsOccurring {

        // NOTE: This is logically a GET operation. It's a POST because data must be sent in the
        // requuest body.

        throwExceptionIfCleanupOperationsIsOccurring();

        String rowsFlat = getRowBO().getRowsFlat(query);

        return create_SUCCESS_response(rowsFlat);
    }

    @RequestMapping(
            value="/api/v02/rows/asFile",
            method = RequestMethod.POST,
            produces = "application/binary")
    public ResponseEntity<InputStreamResource> getRowsAsFile(
            @RequestBody String query)
            throws IOException, CleanupOperationIsOccurring {

        // NOTE: This is logically a GET operation. It is a POST because data must be sent in the
        // requuest body.

        throwExceptionIfCleanupOperationsIsOccurring();

        query = URLDecoder.decode(query, "UTF-8");

        // This is all necessary because the calling code is passing the query string in as an HTML form element.
        query = query.replaceFirst("query=", "");
        query = query.replaceAll("&quot;", "'");
        query = query.replaceAll("\n", " ");

        XSSFWorkbook workbook = getRowBO().getRowsAsExcelWorkbook(query);

        InputStream inputStream = Utilities.toInputStream(workbook);

        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .header("content-disposition", "attachment; filename=SearchResults.xlsx")
                .body(inputStreamResource);
    }

    @RequestMapping(
            value="/api/v02/rows/{rowId}",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getRow(
            @PathVariable(value = "rowId") String rowId)
            throws UnknownRow, CleanupOperationIsOccurring {

        throwExceptionIfCleanupOperationsIsOccurring();

        // not certain this is necessary, but doing as a precaution
        rowId = getValueSanitizer().sanitize(rowId);

        String row = getRowBO().getRow(rowId);
        return create_SUCCESS_response(row);
    }

    protected IRowBO getRowBO() {

        return dataRepositoryApplication.getBusinessObjects().getRowBO();
    }

    protected IValueSanitizer getValueSanitizer() {

        return dataRepositoryApplication.getValueSanitizer();
    }
}
