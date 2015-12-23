package gov.energy.nbc.car.restEndpoint;

import com.mongodb.util.JSON;
import gov.energy.nbc.car.app.DataRepositoryApplication;
import gov.energy.nbc.car.utilities.fileReader.DatasetReader_ExcelWorkbook;
import gov.energy.nbc.car.utilities.fileReader.exception.UnsupportedFileExtension;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static gov.energy.nbc.car.utilities.HTTPResponseUtility.*;


@RestController
public class Endpoints_Utils {

    protected Logger log = Logger.getLogger(getClass());

    @Autowired
    protected DataRepositoryApplication dataRepositoryApplication;


    @RequestMapping(value="/api/getNamesOfSheetsWithinExcelWorkbook", method = RequestMethod.POST)
    public ResponseEntity addDataset(
            @RequestParam(value = "dataFile", required = true) MultipartFile dataFile)
            throws IOException {

        if (dataFile == null) { return create_BAD_REQUEST_missingRequiredParam_response("dataFile");}

        InputStream inputStream = dataFile.getInputStream();

        List<String> namesOfSheetsWithinExcelWorkbook = null;
        try {
            namesOfSheetsWithinExcelWorkbook = DatasetReader_ExcelWorkbook.getNamesOfSheetsWithinWorkbook(
                    dataFile.getOriginalFilename(),
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
