package gov.energy.nrel.dataRepositoryApp.restEndpoint;

import gov.energy.nrel.dataRepositoryApp.ResultsMode;
import gov.energy.nrel.dataRepositoryApp.app.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IRowBO;
import gov.energy.nrel.dataRepositoryApp.utilities.Utilities;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLDecoder;

import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_NOT_FOUND_response;
import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.create_SUCCESS_response;


@RestController
public class Endpoints_Rows {

    protected Logger log = Logger.getLogger(getClass());

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;


    @RequestMapping(
            value="/api/rows",
            method = RequestMethod.POST,
            produces = "application/json")
    public ResponseEntity getRows(
            @RequestBody String query) {

        String rows = getRowBO().getRows(query, ResultsMode.INCLUDE_ONLY_DATA_COLUMNS_BEING_FILTERED_UPON);

        return create_SUCCESS_response(rows);
    }

    @RequestMapping(
            value="/api/rows/flat",
            method = RequestMethod.POST,
            produces = "application/json")
    public ResponseEntity getRowsFlat(
            @RequestBody String query) {

        String rowsFlat = getRowBO().getRowsFlat(query);

        return create_SUCCESS_response(rowsFlat);
    }

    @RequestMapping(
            value="/api/rows/asFile",
            method = RequestMethod.POST)
    public ResponseEntity<InputStreamResource> getRowsAsFile(
            @RequestBody String query) throws IOException {

        // It's not clear why this is necessary with this call, but it is.
        query = query.replace("query=", "");
        query = URLDecoder.decode(query, "UTF-8");

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
            value="/api/row/{rowId}",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity getRow(
            @PathVariable(value = "rowId") String rowId) {

        String row = getRowBO().getRow(rowId);

        if (row == null) {
            return create_NOT_FOUND_response();
        }

        return create_SUCCESS_response(row);
    }

    protected IRowBO getRowBO() {

        return dataRepositoryApplication.getBusinessObjects().getRowBO();
    }

}
