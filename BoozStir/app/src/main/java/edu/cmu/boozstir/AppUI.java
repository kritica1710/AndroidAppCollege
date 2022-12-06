package edu.cmu.boozstir;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import java.io.StringReader;
import javax.json.*;
import javax.json.JsonReader;
import javax.json.JsonArray;

//This is the UI to set and get the UI elements of the android app
public class AppUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creating autocompletetextview for drinks inspired from solution provided at TutorialsPoint
        AutoCompleteTextView drinks = (AutoCompleteTextView) findViewById(R.id.searchDrink);
        //creating the array adapter to be adapted by the autocompleteview
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, DrinksList.drinkList);

        //Setting the array adapter and setting the number of characters to be entered before the autocomplete list appears
        drinks.setAdapter(adapter);
        drinks.setThreshold(1);

        /*
         * The click listener will need a reference to this object, so that upon successfully finding the right information, it
         * can callback to this object with the resulting information of the drink.  The "this" of the OnClick will be the OnClickListener
         */
        final AppUI au = this;

        /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = (Button) findViewById(R.id.submit);

        // Add a listener to the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewParam) {
                String searchDrink = ((AutoCompleteTextView) findViewById(R.id.searchDrink)).getText().toString();
                AutoCompleteTextView drink = ((AutoCompleteTextView) findViewById((R.id.searchDrink)));
                TextView content = (TextView) findViewById(R.id.drinkContent);
                ImageView pictureView = (ImageView) findViewById(R.id.cocktailPicture);
                pictureView.setImageResource(R.mipmap.ic_launcher);
                pictureView.setVisibility(View.INVISIBLE);
                if (searchDrink != null && !searchDrink.isEmpty()&&checkInput(searchDrink)) {
                    GetDrinks gd = new GetDrinks();
                    gd.search(searchDrink.toLowerCase(), au); // Done asynchronously in another thread.
                } else if(searchDrink.isEmpty()){
                    content.setText("Please enter a cocktail");
                }
                else{
                    String feedback = "Sorry, no " + searchDrink + " named cocktail found";
                    content.setText(feedback);
                    drink.setText("");
                }
            }
        });

    }

    /*
     * This is called by the GetDrinks object when the result JSON string is ready with all information is ready.
     * This passes back the JSON string to be displayed for on the app for the corresponding user input
     */
    public void drinksReady(String drinksJson) {

        AutoCompleteTextView drink = ((AutoCompleteTextView) findViewById((R.id.searchDrink)));
        TextView content = findViewById(R.id.drinkContent);
        String searchDrink = ((AutoCompleteTextView) findViewById(R.id.searchDrink)).getText().toString();


        if (checkInput(searchDrink))
        {

            printDrinks(drinksJson);
        } else {
            String feedback = "Sorry, no " + searchDrink + " named cocktail found";
            content.setText(feedback);
            drink.setText("");
        }
    }

    /*
   This method extracts the drink information from the JSON string passed and then prints the information on the app
   for user view
    */
    public void printDrinks(String eventsJson) {

        String searchDrink = ((AutoCompleteTextView) findViewById(R.id.searchDrink)).getText().toString();
        TextView content = findViewById(R.id.drinkContent);
        content.setText("");

        JsonReader jsonReader = Json.createReader(new StringReader(eventsJson));
        JsonArray drinkResponse = (JsonArray) jsonReader.readObject().get("drinks");

        for (int i = 0; i < drinkResponse.size(); i++) {


            String name = drinkResponse.getJsonObject(i).getString("name"); //based on url change this to strDrink
            String toSet = "";

            if (name.equalsIgnoreCase(searchDrink)) {

                String instructions = drinkResponse.getJsonObject(i).getString("instructions");  //based on url change this to strInstructions
                String[] ingredients = drinkResponse.getJsonObject(i).getString("ingredients").split(",");

                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < ingredients.length; j++) {
                    sb.append(ingredients[j].trim().replace("\"", ""));
                    sb.append("\n");

                }

                toSet = "Cocktail: " + name  + "\n\n Ingredients: \n" + sb + "\n Instructions:\n" + instructions + "\n\nENJOY!";

                content.append(toSet);
            }

        }
    }

    /*
     * This is called by the GetDrinks object when the picture is ready.  This allows for passing back the Bitmap picture for updating the ImageView
     */
    public void pictureReady(Bitmap picture) {
        ImageView pictureView = (ImageView) findViewById(R.id.cocktailPicture);
        if (picture != null) {
            pictureView.setImageBitmap(picture);
            pictureView.setVisibility(View.VISIBLE);

        } else {
            pictureView.setImageResource(R.mipmap.ic_launcher);
            pictureView.setVisibility(View.INVISIBLE);

        }

        pictureView.invalidate();
    }

    public boolean checkInput(String search)
    {

        String[] list = DrinksList.drinkList;
        for(String s: list)
        {
            if(s.equalsIgnoreCase(search))
            {
                return true;
            }
        }
        return false;
    }
}




