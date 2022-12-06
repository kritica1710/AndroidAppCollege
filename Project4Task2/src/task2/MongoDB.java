package task2;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
/*
 *
This program connects to MongoDB and inserts the logs collected from the HTTP request
and response interactions and performs CRUD operations programmatically from a Java program to a database
and performs analytics on them to be displayed on dashboard
 */
public class MongoDB {

    static int requestNum;
    // Creating a Mongo client and access the database containing the required collection
    static MongoClientURI uri = new MongoClientURI(
            "mongodb+srv://kriticas:nancy17@cluster0-sk2bl.mongodb.net/projectLogs?retryWrites=true&w=majority");

    static MongoClient mongoClient = new MongoClient(uri);
    static MongoDatabase database = mongoClient.getDatabase("projectLogs");

    /*
    This method returns an ArrayList of type LogData which contains value it extracts from
    each document in the MongoCollection
    */
    public ArrayList<LogData> getLogData() {

        ArrayList<LogData> BoozStirLogs = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("analytics");
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document json = cursor.next();
                String searchedDrink = "";
                String device = "";
                String reqFromBoozStir = "";
                String reqToAPI = "";
                String repFromAPI = "";
                String repToBoozStir = "";
                long totalLatency = 0;
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                if (json.containsKey("Searched Drink")) {
                    searchedDrink = (String) json.get("Searched Drink");
                }
                if (json.containsKey("User-Agent")) {
                    device = (String) json.get("User-Agent");
                }
                if (json.containsKey("Timestamp of request from BoozStir")) {
                    reqFromBoozStir =  dateFormat.format(json.get("Timestamp of request from BoozStir"));
                }
                if (json.containsKey("Timestamp of request to API")) {
                    reqToAPI = dateFormat.format(json.get("Timestamp of request to API"));
                }
                if (json.containsKey("Timestamp of response from API")) {
                    repFromAPI = dateFormat.format(json.get("Timestamp of response from API"));
                }
                if (json.containsKey("Timestamp of response to BoozStir")) {
                    repToBoozStir = dateFormat.format(json.get("Timestamp of response to BoozStir"));
                }
                if (json.containsKey("Total latency in communication (ms)")) {
                    totalLatency = (long) json.get("Total latency in communication (ms)");
                }

                BoozStirLogs.add(new LogData(searchedDrink, device, reqFromBoozStir, reqToAPI, repFromAPI, repToBoozStir, totalLatency));
            }

        }
        finally {
            {
                cursor.close();
            }
        }
        return BoozStirLogs;
    }

    /*
    This method is for producing the operation analytics from the logs from getLogData
    It then returns an ArrayList of type LogResults which holds the analysis data.
    This is displayed in the dashboard
    */
    public ArrayList<LogResults> analysisResults() {

        ArrayList<LogData>  BoozStirLogs = getLogData();
        HashMap<String, Integer> drinkCount = new HashMap<>();

        int totalRequests = BoozStirLogs.size();
        int totalLatencySum = 0;
        double averageTotalLatency = 0;

        for(int i = 0; i < BoozStirLogs.size() ; i++)
        {
            totalLatencySum += BoozStirLogs.get(i).TotalLatency;

            if(!drinkCount.containsKey(BoozStirLogs.get(i).searchedDrink))
            {
                drinkCount.put(BoozStirLogs.get(i).searchedDrink, 1);
            }
            else
            {
                drinkCount.put(BoozStirLogs.get(i).searchedDrink, (drinkCount.get(BoozStirLogs.get(i).searchedDrink) + 1));
            }
        }


        averageTotalLatency = (double)(totalLatencySum/totalRequests)/1000;


        HashMap<String, Integer> sortedMap = sortByValue(drinkCount);


        String mostSearchedDrink = "";
        int mostSearchedDrinkCount = 0;

        for (Map.Entry<String, Integer> s : sortedMap.entrySet()) {
            mostSearchedDrink = s.getKey();
            mostSearchedDrinkCount = s.getValue();
            break;
        }

        ArrayList<LogResults> getResults = new ArrayList<>();
        getResults.add(new LogResults(totalRequests, averageTotalLatency, mostSearchedDrink, mostSearchedDrinkCount));

        return getResults;

    }
    /*
     This method takes logging information and inserts them to MongoDB collection, analytics, as a new Document
     */
    public void logDataInMongoDB(long reqFromBoozStir, long reqToAPI, long repFromAPI, long repToBoozStir,
                               String globalSearchTerm, String device) {

        System.out.println("Log values: " + reqFromBoozStir + reqToAPI + repFromAPI + repToBoozStir + globalSearchTerm + device);

        MongoCollection<Document> collection = database.getCollection("analytics");

        requestNum = requestNum + 1;
        Document doc = new Document("requestID", requestNum)
                .append("Searched Drink", globalSearchTerm)
                .append("User-Agent", device)
                .append("Timestamp of request from BoozStir", new Date(reqFromBoozStir))
                .append("Timestamp of request to API", new Date(reqToAPI))
                .append("Timestamp of response from API", new Date(repFromAPI))
                .append("Timestamp of response to BoozStir", new Date(repToBoozStir))
                .append("Total latency in communication (ms)", (repToBoozStir - reqFromBoozStir));


        collection.insertOne(doc);



    }

    //Recycled code, thanks to https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values

    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list = new LinkedList<>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}
