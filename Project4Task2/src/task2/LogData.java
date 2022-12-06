package task2;


//This method is the placeholder of the useful log information to keep track (i.e. log) of data regarding the web service usage
public class LogData {

    public String searchedDrink;
    public String device;
    public String reqFromBoozStir;
    public String reqToAPI;
    public String repFromAPI;
    public String repToBoozStir;
    public long TotalLatency;

    public LogData(String searchedDrink, String device, String reqFromBoozStir, String reqToAPI, String repFromAPI,
                   String repToBoozStir, long totalLatency) {
        this.searchedDrink = searchedDrink;
        this.device = device;
        this.reqFromBoozStir = reqFromBoozStir;
        this.reqToAPI = reqToAPI;
        this.repFromAPI = repFromAPI;
        this.repToBoozStir = repToBoozStir;
        TotalLatency = totalLatency;
    }
}

