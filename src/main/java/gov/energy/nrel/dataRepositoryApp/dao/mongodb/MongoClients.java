package gov.energy.nrel.dataRepositoryApp.dao.mongodb;

import com.mongodb.MongoClient;

import java.util.HashMap;
import java.util.Map;

public class MongoClients {

    private static final String STRING_THAT_IS_LIKELY_UNIQUE = "----";
    protected static Map<String, MongoClient> mongoClients = new HashMap<>();

    public static MongoClient getClientForHost(String mongoDbHost, int mongoDbPort) {

        StringBuilder stringBuilder = new StringBuilder().
                append(mongoDbHost).
                append(STRING_THAT_IS_LIKELY_UNIQUE).
                append(mongoDbPort);

        String key = stringBuilder.toString();

        MongoClient mongoClient = mongoClients.get(key);

        if (mongoClient == null) {

            mongoClient = new MongoClient(mongoDbHost, mongoDbPort);
            mongoClients.put(key, mongoClient);
        }

        return mongoClient;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        for (String s : mongoClients.keySet()) {
            mongoClients.get(s).close();
        }
    }
}
