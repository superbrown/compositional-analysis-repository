package gov.energy.nbc.car.servlet;

import gov.energy.nbc.car.utilities.Utilities;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * A servlet that implements the methods for downloading an Excel file.
 *
 */
public class DownloadExcelFileServlet extends HttpServlet {

	public static final String EXCEL_FILE_SUFFIX = ".xls";
	public static final String EXCEL07 = ".xlsx";
	public static final String EXCEL97 = ".xls";

	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String XLS_CONTENT_TYPE = "application/vnd.ms-excel";
	public static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static final String XLSM_CONTENT_TYPE = "application/vnd.openxmlformats";
	public static final String SS_FILE_PATH = "excelfile";
	public static final String SS_FILE_NAME = "ssFileName";
	public static final long serialVersionUID = 217721541663385449L;


    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String filePath = request.getParameter(SS_FILE_PATH);
        String fileName = request.getParameter(SS_FILE_NAME);

		setAppropriateFileTypeInTheResponseHeader(response, filePath);

        response.addHeader(CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        OutputStream responseOutputStream = response.getOutputStream();

        Utilities.writeFileToOutputSream(filePath, responseOutputStream);
    }

	protected void setAppropriateFileTypeInTheResponseHeader(HttpServletResponse response, String filePath) {

		String fileSuffix = EXCEL_FILE_SUFFIX;

		int indexOfPeriodBeforeFileExtension = filePath.lastIndexOf(".");

		if (indexOfPeriodBeforeFileExtension > -1) {

        	fileSuffix = filePath.substring(indexOfPeriodBeforeFileExtension);
        }

		if (fileSuffix.equals(EXCEL97)) {

            response.setContentType(XLS_CONTENT_TYPE);
        }
        else if (fileSuffix.equals(EXCEL07)){

            response.setContentType(XLSX_CONTENT_TYPE);
        }
        else {

            response.setContentType(XLSM_CONTENT_TYPE);
        }
	}

}
