package task1;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;

//This stores the drink related information required for display on the app
public class Drink {

    String name;
    String instructions;
    ArrayList<String> ingredients;
    String image;

    public Drink(String name, String instructions, ArrayList ingredients, String image) {
        this.name = name;
        this.instructions = instructions;
        this.ingredients = ingredients;
        this.image = image;
    }

    //This method overrides the toString method of Object class and returns a Json string with all the information
    // required.
    @Override
    public String toString()
    {
        JsonObjectBuilder block = Json.createObjectBuilder();
        block.add("name", name);
        block.add("instructions", instructions);
        for(String s: ingredients) {
            block.add("ingredients", s);
        }
        block.add("image", image );
        JsonObject JsonBlock = block.build();

        return JsonBlock.toString();
    }
}
