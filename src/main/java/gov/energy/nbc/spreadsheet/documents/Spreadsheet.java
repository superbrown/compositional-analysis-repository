package gov.energy.nbc.spreadsheet.documents;

import gov.energy.nbc.spreadsheet.Utils;
import org.bson.Document;

import java.util.List;

public class Spreadsheet extends Document {

    public static final String ATTRIBUTE_KEY__METADATA = "metadata";
    public static final String ATTRIBUTE_KEY__DATA = "data";
    private Metadata metadata;
    private SpreadsheetRowDataList spreadsheetRowDataList;

    public Spreadsheet(Metadata metadata, SpreadsheetRowDataList spreadsheetRowDataList) {

        init(metadata, spreadsheetRowDataList);
    }

    public Spreadsheet(String sampleType,
                       String[] tags,
                       String spreadsheetPath,
                       Object[][] spreadsheetContent,
                       String[] attachments) {

        this(sampleType,
                Utils.toListOrNull(tags),
                spreadsheetPath,
                spreadsheetContent,
                Utils.toListOrNull(attachments));
    }

    public Spreadsheet(String sampleType,
                       List<String> tags,
                       String spreadsheetPath,
                       Object[][] spreadsheetContent,
                       List<String> attachments) {

        Metadata metadata = new Metadata(sampleType, tags, spreadsheetPath, attachments);
        SpreadsheetRowDataList spreadsheetRowDataList = new SpreadsheetRowDataList(spreadsheetContent);

        init(metadata, spreadsheetRowDataList);
    }

    private void init(Metadata metadata, SpreadsheetRowDataList spreadsheetRowDataList) {

        this.metadata = metadata;
        this.spreadsheetRowDataList = spreadsheetRowDataList;

        put(ATTRIBUTE_KEY__METADATA, metadata);
        put(ATTRIBUTE_KEY__DATA, spreadsheetRowDataList);
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public SpreadsheetRowDataList getSpreadsheetRowDataList() {
        return spreadsheetRowDataList;
    }
}
