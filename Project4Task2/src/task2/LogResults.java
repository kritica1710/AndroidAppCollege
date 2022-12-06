package task2;

//this method is the placeholder for all the operation analysis performed on the logged data
//It shows three important information number of drink request, average latency, most popular search with count
public class LogResults {

    public int requestNum;
    public double avgLatency;
    public String mostSearchedDrink;
    public int drinkFreq;

    public LogResults(int requestNum, double avgLatency, String mostSearchedDrink, int drinkFreq) {
        this.requestNum = requestNum;
        this.avgLatency = avgLatency;
        this.mostSearchedDrink = mostSearchedDrink;
        this.drinkFreq = drinkFreq;
    }
}
