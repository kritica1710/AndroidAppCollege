package task1;

import java.io.IOException;
import javax.json.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

// A simple class to wrap a result
class Result {
    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

/*
 * This serves as the Model that calls the TheCocktailDB API,
gets the JSON response string, extracts key value pairs from the
JSON string and formats the JSON string passed to it by the servlet
 */
public class Project4Task1CocktailModel {

    static String globalSearchTerm;

    // read a value associated with a name from the server
    // return either the value read or an error message
    public static String read(String name) {
        Result r = new Result();
        int status = 0;
        if((status = doGet(name,r)) != 200) return "Error from server "+ status;
        return r.getValue();
    }

    //This method does the HTTP GET call to the 3rd party API and returns the status code of the request
    public static int doGet(String name, Result r) {

        // Make an HTTP GET passing the name on the URL line
        r.setValue("");
        String response = "";
        HttpURLConnection conn;
        int status = 0;

        try {


            // pass the name on the URL line
            URL url = new URL("https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" +
                    name.replace(" ", "%20").replace("'", "%27"));
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // tell the server what format we want back
            conn.setRequestProperty("Accept", "text/plain");

            // wait for response
            status = conn.getResponseCode();

            // If things went poorly, don't try to read any response, just return.
            if (status != 200) {
                // not using msg
                String msg = conn.getResponseMessage();
                return conn.getResponseCode();
            }
            String output = "";
            // things went well so let's read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            while ((output = br.readLine()) != null) {
                response += output;

            }

            conn.disconnect();

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }   catch (IOException e) {
            e.printStackTrace();
        }

        // return value from server
        // set the response object
        r.setValue(response);
        // return HTTP status to caller
        return status;
    }

    /*
    This method takes Json string response returned from the API and extracts the required key value pairs
    and and puts it in an ArrayList, calls getJson method to get back a Json and returns the properly formatted
    Json string. Here we are getting the drink name, instructions, ingredients, measure and the image URL
    */
    public static String myJsonReader(String jResponse) {
        try (JsonReader jsonReader = Json.createReader(new StringReader(jResponse))) {

            JsonArray drinkResponse = (JsonArray) jsonReader.readObject().get("drinks");
            ArrayList<Drink> drinkList = new ArrayList<>();

                for (int i = 0; i < drinkResponse.size(); i++) {

                    String name = drinkResponse.getJsonObject(i).getString("strDrink");

                    String instructions = drinkResponse.getJsonObject(i).getString("strInstructions");

                    ArrayList<String> ingredients = new ArrayList<>();
                    for (int j = 1; j < 16; j++) {
                        String ingredient = "strIngredient" + j;
                        String measure = "strMeasure" + j;
                        if (drinkResponse.getJsonObject(i).get(ingredient).toString() != null &&
                                !drinkResponse.getJsonObject(i).get(ingredient).toString().equalsIgnoreCase("null")
                                && !drinkResponse.getJsonObject(i).get(ingredient).toString().isEmpty()) {
                            ingredients.add(drinkResponse.getJsonObject(i).get(ingredient).toString() + " - "
                                    + drinkResponse.getJsonObject(i).get(measure).toString());
                        }
                    }
                    String image = "";
                   if(drinkResponse.getJsonObject(i).getString("strDrinkThumb")!=null)
                   {
                       image = drinkResponse.getJsonObject(i).get("strDrinkThumb").toString();
                   }



                    drinkList.add(new Drink(name, instructions, ingredients, image));

                }

                return getJson(drinkList);


        }
    }
    /*
     This method prepares response JSON string to be sent to the android app.
     */
    public static String getJson(ArrayList<Drink> drinkList) {
        JsonObjectBuilder jsonBuild = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for (int i = 0; i < drinkList.size(); i++) {

            if (globalSearchTerm.equalsIgnoreCase(drinkList.get(i).name)){

                System.out.println("I only enter on match ");
                JsonObjectBuilder drinkBuilder = Json.createObjectBuilder();
            String ingredient = "";
            for (int j = 0; j < drinkList.get(i).ingredients.size(); j++) {

                if (drinkList.get(i).ingredients.get(j) != null && !drinkList.get(i).ingredients.get(j).equalsIgnoreCase("null")) {
                    if (j == drinkList.get(i).ingredients.size() - 1) {
                        ingredient = ingredient.concat(drinkList.get(i).ingredients.get(j).replace("\"", ""));
                    } else {
                        ingredient = ingredient.concat(drinkList.get(i).ingredients.get(j).replace("\"", "")).concat(",");
                    }

                }
            }
            System.out.print("Ingredients concat " + ingredient);
                System.out.println("image url " + drinkList.get(i).image);
            JsonObject drinkJson = drinkBuilder.add("name", drinkList.get(i).name).add("instructions", drinkList.get(i).instructions)
                    .add("ingredients", ingredient).add("image", drinkList.get(i).image.replace("\"", "")).build();
            arrayBuilder.add(drinkJson);
        }

            return jsonBuild.add("drinks", arrayBuilder).build().toString();
        }

        return null;

    }

    /*
    This method extract the searched drink from the Json string passed to it as an input and returns a
    String which is the drink name input by the user on the app.
    */
    public String extractDrink(String jsonInput)
    {
        String searchString;

        try(JsonReader jsonReader = Json.createReader((new StringReader(jsonInput))))
        {
            JsonObject jsonObject = jsonReader.readObject();
            searchString = jsonObject.getString("name");
        }

        globalSearchTerm = searchString;
        System.out.println("Extract " + searchString);
        return searchString;
    }



}
