package gov.energy.nrel.dataRepositoryApp.restEndpoint;

import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.app.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.DatasetReader_ExcelWorkbook;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static gov.energy.nrel.dataRepositoryApp.utilities.HTTPResponseUtility.*;


@RestController
public class Endpoints_Utils {

    protected Logger log = Logger.getLogger(getClass());

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;


    @RequestMapping(value="/api/getNamesOfSheetsWithinExcelWorkbook", method = RequestMethod.POST)
    public ResponseEntity addDataset(
            @RequestParam(value = "sourceDocument", required = true) MultipartFile sourceDocument)
            throws IOException {

        if (sourceDocument == null) {
            return create_BAD_REQUEST_missingRequiredParam_response("sourceDocument");
        }

        InputStream inputStream = sourceDocument.getInputStream();

        List<String> namesOfSheetsWithinExcelWorkbook = null;
        try {
            namesOfSheetsWithinExcelWorkbook = DatasetReader_ExcelWorkbook.getNamesOfSheetsWithinWorkbook(
                    sourceDocument.getOriginalFilename(),
                    inputStream);
        }
        catch (UnsupportedFileExtension e) {
            log.info(e);
            return create_BAD_REQUEST_response(e.toString());
        }

        String json = JSON.serialize(namesOfSheetsWithinExcelWorkbook);

        return create_SUCCESS_response(json);
    }
}
