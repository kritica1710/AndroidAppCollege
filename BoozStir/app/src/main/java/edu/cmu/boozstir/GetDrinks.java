package edu.cmu.boozstir;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;


/*
 * This class provides capabilities to search for a drink on the 3rd party API given a search term.  The method "search" is the entry to the class.
 * Network operations cannot be done from the UI thread, therefore this class makes use of an AsyncTask inner class that will do the network
 * operations in a separate worker thread.  However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 * onPostExecution runs in the UI thread, and it calls the GetDrinks pictureReady and drinkReady method to do the update.
 *
 */
public class GetDrinks {
    AppUI au = null;

    /*
     * search is the public GetDrinks method.  Its arguments are the search term, and the AppUI object that called it.
     * This provides a callback path such that the pictureReady and drinkReady methods in that object are called
     * when the picture and drink information is available from the search.
     */
    public void search(String searchDrink, AppUI au) {
        this.au = au;
        new AsyncTmSearch().execute(searchDrink);
    }

    /*
     * AsyncTask provides a simple way to use a thread separate from the UI thread in which to do network operations.
     * doInBackground is run in the helper thread.
     * onPostExecute is run in the UI thread, allowing for safe UI updates.
     */
    private class AsyncTmSearch extends AsyncTask<String, Void, HashMap<Bitmap, String>> {
        @Override
        protected HashMap<Bitmap, String> doInBackground(String... searchTerms) {
            return search(searchTerms[0]);
        }

        @Override
        protected void onPostExecute(HashMap<Bitmap, String> Response) {

            String JsonRes ="";
            Bitmap bp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            for(Map.Entry<Bitmap, String> m: Response.entrySet())
            {
                JsonRes = m.getValue();
                bp = m.getKey();
            }
            au.drinksReady(JsonRes);
            au.pictureReady(bp);

        }

        /*
         * Search the TheCocktailDB.com API for the searchDrink argument, and return a HashMap containing a
         *  Bitmap and String which is the information related to the drink that can be put in an ImageView and TextView
         */
        private HashMap<Bitmap, String> search(String searchDrink) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", searchDrink);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String jsonRes= getRemoteJson(jsonObject.toString());


            JsonReader jsonReader = Json.createReader(new StringReader(jsonRes));
            JsonArray drinkResponse = (JsonArray) jsonReader.readObject().get("drinks");
            String image = "";
            Bitmap bp;
            HashMap<Bitmap, String> result = new HashMap<>();


                if (drinkResponse.getJsonObject(0).getString("image") != null) {
                    image = drinkResponse.getJsonObject(0).getString("image");

                }

                bp = searchImage(image);
                result.put(bp, jsonRes);

            if (result != null) {
                return result;
            } else {
                return null; //no events found
            }


        }

        /*
         * Given a url that will reach out to the web service through cloud, return a JSON String with that JSON request, else null
         */
        private String getRemoteJson(String jsonRequest) {
            try {

                //dashboard : https://apricot-pie-81976.herokuapp.com/dashboard
                //http://localhost:8080/getMyCocktail
                //task 2 - https://apricot-pie-81976.herokuapp.com/getMyCocktail
                //task1 - https://blueberry-sundae-22185.herokuapp.com/getMyCocktail
                URL url = new URL("https://blueberry-sundae-22185.herokuapp.com/getMyCocktail");

                final HttpURLConnection conn;

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "text/plain");

                BufferedWriter writer  = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                writer.write(jsonRequest);
                writer.flush();
                writer.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String result = reader.readLine();


                reader.close();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        //Using the String URL retrieved for the drink image to make a URL and pass it to getRemoteImage method
        private Bitmap searchImage(String pictureURL) {


            // At this point, we have the URL of the picture that resulted from the search.  Now load the image itself.
            try {
                URL u = new URL(pictureURL);
                return getRemoteImage(u);
            } catch (Exception e) {
                e.printStackTrace();
                return null; // so compiler does not complain
            }

        }
        /*
         * Given a URL referring to an image, return a bitmap of that image
         */
        private Bitmap getRemoteImage(final URL url) {
            try {
                final URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                Bitmap bm = BitmapFactory.decodeStream(bis);
                bis.close();
                return bm;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

}
