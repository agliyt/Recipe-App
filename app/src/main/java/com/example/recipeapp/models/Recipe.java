package com.example.recipeapp.models;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.BuildConfig;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class Recipe implements Serializable {

    boolean fromApi;
    String title;
    String imageUrl;
    ParseFile image;
    int missedIngredientsCount;
    int usedIngredientsCount;
    int id;
    ParseUser currentUser;
    int servings;
    int readyInMinutes;
    String ingredients;
    String instructions;


    public Recipe(JSONObject jsonObject, ParseRecipe parseRecipe, boolean isApiRecipe) throws JSONException {
        fromApi = isApiRecipe;
        if (isApiRecipe) {
            title = jsonObject.getString("title");
            imageUrl = jsonObject.getString("image");
            missedIngredientsCount = jsonObject.getInt("missedIngredientCount");
            usedIngredientsCount = jsonObject.getInt("usedIngredientCount");
            id = jsonObject.getInt("id");
        } else {
            title = parseRecipe.getTitle();
            image = parseRecipe.getImage();
            servings = parseRecipe.getServings();
            readyInMinutes = parseRecipe.getReadyInMinutes();
            ingredients = parseRecipe.getIngredients();
            instructions = parseRecipe.getInstructions();
            setNumberIngredients(parseRecipe);
        }
    }

    private void setNumberIngredients(ParseRecipe parseRecipe) {
        List<String> ingredientsParsed = parseRecipe.getIngredientsParsed();
        // find how many ingredients are in common
        currentUser = ParseUser.getCurrentUser();
        List<String> userIngredients = (List<String>) currentUser.get("ingredientsOwned");
        usedIngredientsCount = 0;
        for (int i = 0; i < ingredientsParsed.size(); i++) {
            for (int j = 0; j < userIngredients.size(); j++) {
                String ingredient = ingredientsParsed.get(i);
                String userIngredient = userIngredients.get(j);
                if (ingredient == userIngredient) {
                    usedIngredientsCount++;
                }
            }
        }
        missedIngredientsCount = ingredientsParsed.size() - usedIngredientsCount;
    }

    public static List<Recipe> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            recipes.add(new Recipe(jsonArray.getJSONObject(i), null, true));
        }
        return recipes;
    }

    public static List<Recipe> fromParseRecipeArray(List<ParseRecipe> parseRecipes) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < parseRecipes.size(); i++) {
            recipes.add(new Recipe(null, parseRecipes.get(i), false));
        }
        return recipes;
    }

    public boolean isFromApi() {
        return fromApi;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ParseFile getImage() {
        return image;
    }

    public int getMissedIngredientsCount() {
        return missedIngredientsCount;
    }

    public int getUsedIngredientsCount() {
        return usedIngredientsCount;
    }

    public int getId() {
        return id;
    }

    public int getServings() {
        return servings;
    }

    public int getReadyInMinutes() {
        return readyInMinutes;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

}
