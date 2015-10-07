package gov.energy.nbc.spreadsheet.documents;

import gov.energy.nbc.spreadsheet.Utils;
import org.bson.Document;

import java.util.List;

public class Metadata extends Document {

    public static final String ATTRIBUTE_KEY__TAGS="tags";
    public static final String ATTRIBUTE_KEY__SAMPLE_TYPE = "sampleType";
    public static final String ATTRIBUTE_KEY__SPREADSHEET_PATH = "spreadsheetFileName";
    public static final String ATTRIBUTE_KEY__ATTACHMENTS = "attachments";
    private final List<String> tags;
    private final String sampleType;
    private final String spreadsheetPath;
    private final List<String> attachments;

    public Metadata(
                String sampleType,
                List<String> tags,
                String spreadsheetPath,
                List<String> attachments) {

        this.tags = tags;
        this.sampleType = sampleType;
        this.spreadsheetPath = spreadsheetPath;
        this.attachments = attachments;

        put(ATTRIBUTE_KEY__TAGS, this.tags);
        Utils.putIfNotBlank(this, ATTRIBUTE_KEY__SAMPLE_TYPE, this.sampleType);
        Utils.putIfNotBlank(this, ATTRIBUTE_KEY__SPREADSHEET_PATH, this.spreadsheetPath);
        put(ATTRIBUTE_KEY__ATTACHMENTS, this.attachments);
    }
}
