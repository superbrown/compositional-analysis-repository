package gov.energy.nbc.car.servlet;


/**
 * This interface holds constants.
 *
 * @author James Albersheim
 */
public interface AppConstants {

    // initialization-------------------------------------------

	public final String ALPHANUMERICPUNCT_PATTERN = "[a-zA-Z0-9!#$%&()\\*+,\\./:;<=>^_`{|}~@\\- ]*";
	public final String ALPHANUMERIC_PATTERN = "[a-zA-Z0-9 \\-]*";
	public final String NUMERIC_PATTERN = "[0-9\\.\\-]*";
	public final String LONG_PATTERN = "[0-9\\-]*";
	public final String BADINPUTCHARS = "\\<>?`';";
	public final String BADTABLECHARS = "[`/\\,\\.><|:()&;?\\*]*";
	
	// DB Types
	public final long LONG=1;
	public final long REAL=2;
	public final long DATE=3;
	public final long STRING=4;
	public final long BOOLEAN=5;
	
	public final short DBHEADER = 1;
	public final short SSHEADER = 2;
	public final short HEADER = 1;
	public final short SYNONYM = 2;
	
	public final short META = 1;
	public final short CELL = 2;
	
	public final long DEFAULT_LONG = -9999;
	public final double DEFAULT_REAL = -9999.0;
	
    /** The Hibernate properties file name. */
    public final String SPREADSHEET_PROPERTIES_FILE_NAME = "spreadsheet";
	
    /**
     * Default Excel file name for file returned to user
     */
	public final String EXCEL_FILE_NAME = "output";
	
	/**
	 * Default Excel suffix standard
	 */
	public final String EXCEL_FILE_SUFFIX = ".xls";

	/**
	 * Default Excel suffix standard
	 */
	public final String EXCEL_FILE_SUFFIX_2007 = ".xlsx";

	public final String EXCEL97 = ".xls";

	public final String EXCEL07 = ".xlsx";

	public final String EXCELMACRO = ".xlsm";

	public static final String CSV = ".csv";
	
	public static final String TAB = ".tab";
	
	public static final String NUMBERS = ".numbers";
	
	public static final String NC = ".nc";

	public static final String TAB_DELIMITER = "\t";

	public static final String COMMA_DELIMITER = ",";

	public final String DEFAULT_WORKSHEET_NAME = "query";

	/**
	 * Property name for the path to store the spreadsheet files locally.
	 */
	public final String FILE_DIR = "fileDirectory";
    
    /**
     * Property name for the location of the processed file log
     */
//    public final String PROCESSED_FILE_LOG = "logDirectory";
    
    /**
     * Label for navigation panel - Submission
     */
	public final String INIT_STATE = "initState";
    
	public final String MULTI_STATE = "multiState";
	     /**
     * Label for navigation panel - Search
     */
	public final String SEARCH_STATE = "searchState";
	
	public final String WORKBOOK_ID_COLUMN = "workbook_id";
	
	public final String WORKBOOK_ID = "Workbook ID";
	
	public final String ATTACHMENT_EXT = "Attachment Extension";
	
	public final String ATTACHMENT_EXT_COLUMN = "ext";
	
	public final String SHEET_SEPARATOR = "|";
	
	public final String HDR_ROW = "hdr_row";
	public final String HDR_COL = "hdr_col";
	public final String DATA_ROW = "data_row";
	public final String DATA_COL = "data_col";
	public final String META_ROW = "meta_row";
	public final String META_COL = "meta_col";
	
	public final int ALL = 0;
	public final int INTERNAL = 1;
	public final int EXTERNAL = 2;

	public final String WORKBOOK_TO_MODIFY_NAME = "workbook2modify";
	
	public final String CREATE_OR_MODIFY_NAME = "c_or_m";
	
	public final String CONFIG = "workbookConfigName";

	public final String BLANKS = "---";
	
	public final String HANDLE_ERRORS = "Handle parsing errors:";
	public final String AS_BLANKS = "As Blanks";
	public final String FAILED = "As Failed Upload";


	public final int SPREADSHEET_ERRORS = -1;
	public final int MISSING_WORKSHEET = -2;
	public final int MISMATCHED_COLUMN_NUMBERS = -3;
	public final int MISSING_COLUMN = -4;
	public final int MISMATCHED_TYPE = -5;
	public final int MISMATCHED_CONFIG = -6;
	public final int MISSING_INTERNAL = -7;
	public final int PARSING_PROBLEMS = -8;
	public final int PARTIAL_INGESTION = -9;
	public final int INVALID_PAIR = -10;
	
	public final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

	public final String URL="hibernate.connection.url";
	public final String USERTAG="hibernate.connection.username";
    public final String PASSTAG="hibernate.connection.password";
	
	public final String DATA_TABLE_ENDING = "_data";
	
	public final String FRONT_DATE_PART = " str_to_date(";//'08/10/2010'
	public final String BACK_DATE_PART = ",'%m/%d/%Y') ";

	public final String BEGIN_SELECT = "select wd.workbook_id ";
	
	public final String FROM_WHERE_PART = " row_data rd, " 
		+ " workbook_data wd, workbook_config wc, "
		+ " sheet_config sc, sheet_data sd  where "
		+ " wd.workbook_config_id = wc.workbook_config_id and "
		+ " sc.sheet_config_id = cd.sheet_config_id and "
		+ " cd.row_data_id = rd.row_data_id and "
		+ " rd.sheet_data_id = sd.sheet_data_id and "
		+ " sd.sheet_config_id = cd.sheet_config_id and "
		+ " sd.workbook_id = wd.workbook_id ";
	public final String ATTACHMENT_COUNT_SQL = "select count(att.attachment_id) from workbook_data wd, "
		+ " workbook_attachments wa, attachments att, attachmentTypes attt "
		+ " where wd.workbook_id = wa.workbook_id and wa.attachment_id = att.attachment_id and "
		+ " att.type_id = attt.type_id "; 
	public final String ATTACHMENT_WORKBOOK_SQL = "select wd.workbook_id from workbook_data wd, "
		+ " workbook_attachments wa, attachments att, attachmentTypes attt "
		+ " where wd.workbook_id = wa.workbook_id and wa.attachment_id = att.attachment_id and "
		+ " att.type_id = attt.type_id "; 
	public final String ID_COLUMN = "row_data_id";
	public final String ORDER_BY = " order by wd.workbook_id, cd."+ID_COLUMN;
	public final String GROUP_BY = " group by wa.workbook_id ";
	public final int PAGE = 5;
}
