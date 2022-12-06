package task2;



import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;

/*
 * This program acts is the servlet takes an HTTP request which calls models methods to get the drink name that is
 * user input and then get the corresponding Json from the models method which is then sent as response to
 * the app in form of Json String. It also takes a HTTP GET request which calls the dashboard.jsp to
 * display the log details on the web browser.
 */

//URL pattern to access the service and the dashboard
@WebServlet(name = "getMyCocktail", urlPatterns = {"/getMyCocktail", "/dashboard"})


public class Project4Task2CocktailServlet extends HttpServlet {

    static MongoDB mongo = new MongoDB();

    //if the url pattern returns /dashboard, then call this method to set the jsp attributes with the
    //mongoDB document containing log details and the analysis results.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getServletPath().equalsIgnoreCase("/dashboard")) {

            ArrayList<LogData> document = mongo.getLogData();
            ArrayList<LogResults> results = mongo.analysisResults();
            request.setAttribute("document", document);
            request.setAttribute("results", results);
            RequestDispatcher view = request.getRequestDispatcher("dashboard.jsp");
            view.forward(request, response);
        }
    }

    //for url pattern /getMyCocktail
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getServletPath().equalsIgnoreCase("/getMyCocktail")) {
            String jb = "";
            String line = null;

            //instantiating model
            Project4Task2CocktailModel model = new Project4Task2CocktailModel();
            try {
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null)
                    jb += line;
            } catch (Exception e) { /*report an error*/ }

            //noting the device being use and timestamps
            model.device = request.getHeader("User-Agent");
            model.reqFromBoozStir = System.currentTimeMillis();

            //calling model methods to get the drink name from the json received in request and then
            //passing this drink name to models method to get the Json response from the API
            String searchString = model.extractDrink(jb);

            String jsonResponse = model.myJsonReader(model.read(searchString, model));

            response.setStatus(200);

            response.setContentType("text/plain;charset=UTF-8");

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
            out.write(jsonResponse);
            out.flush();
            out.close();

            //noting the response timestamps and pushing the mongoDB object to be logged in the document
            model.repToBoozStir = System.currentTimeMillis();
            model.pushLogsToMongoDB(mongo);
        }

    }

}
