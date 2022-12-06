package task1;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;


/*
 * This program acts is the servlet takes an HTTP request which calls models methods to get the drink name that is
 * user input and then get the corresponding Json from the models method which is then sent as response to
 * the app in form of Json String
 */

//URL pattern to access the service
@WebServlet(name = "getMyCocktail", urlPatterns = {"/getMyCocktail"})

public class Project4Task1CocktailServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String jb = "";
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb+=line;
        } catch (Exception e) { /*report an error*/ }

        //instantiating model
        Project4Task1CocktailModel model = new Project4Task1CocktailModel();

        //calling model methods to get the drink name from the json received in request and then
        //passing this drink name to models method to get the Json response from the API
        String searchString = model.extractDrink(jb);

        String jsonResponse = model.myJsonReader(model.read(searchString));

        response.setStatus(200);

        response.setContentType("text/plain;charset=UTF-8");

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));

        //writing back to the app
        out.write(jsonResponse);
        out.flush();
        out.close();

    }

}
