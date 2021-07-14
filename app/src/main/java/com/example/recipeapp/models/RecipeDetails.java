package com.example.recipeapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetails {

    String title;
    String imageUrl;
    int servings;
    int readyInMinutes;
    List<String> ingredients;
    String instructions;

    public RecipeDetails(JSONObject jsonObject) throws JSONException {
        title = jsonObject.getString("title");
        imageUrl = jsonObject.getString("image");
        servings = jsonObject.getInt("servings");
        readyInMinutes = jsonObject.getInt("readyInMinutes");

        ingredients = new ArrayList<>();
        JSONArray jsonIngredients = jsonObject.getJSONArray("extendedIngredients");
        for (int i = 0; i < jsonIngredients.length(); i++) {
            ingredients.add(jsonIngredients.getJSONObject(i).getString("original"));
        }

        instructions = jsonObject.getString("instructions");
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getServings() {
        return servings;
    }

    public int getReadyInMinutes() {
        return readyInMinutes;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }
}
