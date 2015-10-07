package gov.energy.nbc.spreadsheet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.bson.Document;

import java.util.List;


public class JsonUtils {

    public static final GsonBuilder GSON_BUILDER = new GsonBuilder().setPrettyPrinting();

    public static String toPrettyJsonString(Document document) {
        return toPrettyJsonString(document.toJson());
    }

    public static String toPrettyJsonString(String value) {

        try {
            Gson gson = GSON_BUILDER.create();
            return gson.toJson(value);
        }
        catch (Exception e) {
            return value;
        }
    }

    public static String getJson(Document document, String key) {

        Object o = document.get(key);
        return toJson(o);
    }

    public static Document jsonToDocument(String json) {
        return Document.parse(json);
    }

    public static DBObject jsonToBson(String json) {
        return (DBObject)JSON.parse(json);
    }

    public static String toJson(Object o) {

        String json;

        if (o instanceof Document) {

            json = ((Document) o).toJson();
        }
        else if (o instanceof List) {

            List list = (List) o;

            json = "[ ";
            if (list.size() > 0) {
                for (Object element : list) {
                    json += toJson(element);
                    json += ", ";
                }
                json = json.substring(0, json.length() - 2);
            }

            json += "]";
        }
        else {

            json = o.toString();
        }

        return json;
    }
}
