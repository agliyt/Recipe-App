package com.example.recipeapp.models;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.core.util.Preconditions;

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
    String objectId;
    ParseUser currentUser;
    int servings;
    int readyInMinutes;
    String ingredients;
    String instructions;
    List<String> missedIngredients;
    List<String> ingredientsParsed;


    // jsonObject == null if isApiRecipe == false; parseRecipe == null if isApiRecipe == true
    public Recipe(JSONObject jsonObject, ParseRecipe parseRecipe, boolean isApiRecipe, boolean isFullRecipe) throws JSONException {
        fromApi = isApiRecipe;
        if (isApiRecipe) {
            if (isFullRecipe) {
                title = jsonObject.getString("title");
                imageUrl = jsonObject.getString("image");
                id = jsonObject.getInt("id");
                setNumberIngredientsFullRecipe(jsonObject);
            } else {
                title = jsonObject.getString("title");
                imageUrl = jsonObject.getString("image");
                missedIngredientsCount = jsonObject.getInt("missedIngredientCount");
                usedIngredientsCount = jsonObject.getInt("usedIngredientCount");
                id = jsonObject.getInt("id");

                missedIngredients = new ArrayList<>();
                JSONArray missedJsonArray = jsonObject.getJSONArray("missedIngredients");
                for (int i = 0; i < missedJsonArray.length(); i++) {
                    missedIngredients.add(missedJsonArray.getJSONObject(i).getString("name"));
                }
            }
        } else {
            objectId = parseRecipe.getObjectId();
            title = parseRecipe.getTitle();
            image = parseRecipe.getImage();
            servings = parseRecipe.getServings();
            readyInMinutes = parseRecipe.getReadyInMinutes();
            ingredients = parseRecipe.getIngredients();
            instructions = parseRecipe.getInstructions();
            setNumberIngredients(parseRecipe);
        }
    }

    private void setNumberIngredientsFullRecipe(JSONObject jsonObject) throws JSONException {
        List<String> ingredientsFullRecipe = new ArrayList<>();
        JSONArray jsonIngredients = jsonObject.getJSONArray("extendedIngredients");
        for (int i = 0; i < jsonIngredients.length(); i++) {
            ingredientsFullRecipe.add(jsonIngredients.getJSONObject(i).getString("name"));
        }
        // find how many ingredients are in common
        currentUser = ParseUser.getCurrentUser();
        List<String> userIngredients = (List<String>) currentUser.get("ingredientsOwned");
        missedIngredients = new ArrayList<>();
        for (String ing: ingredientsFullRecipe) {
            missedIngredients.add(ing);
        }
        usedIngredientsCount = 0;
        List<String> usedIngredients = new ArrayList<>();
        for (int i = 0; i < ingredientsFullRecipe.size(); i++) {
            for (int j = 0; j < userIngredients.size(); j++) {
                String ingredient = ingredientsFullRecipe.get(i);
                String userIngredient = userIngredients.get(j);
                if (ingredient.equals(userIngredient)) {
                    usedIngredients.add(ingredient);
                    usedIngredientsCount++;
                }
            }
        }
        missedIngredients.removeAll(usedIngredients);
        missedIngredientsCount = ingredientsFullRecipe.size() - usedIngredientsCount;
    }

    private void setNumberIngredients(ParseRecipe parseRecipe) {
        ingredientsParsed = parseRecipe.getIngredientsParsed();
        // find how many ingredients are in common
        currentUser = ParseUser.getCurrentUser();
        List<String> userIngredients = (List<String>) currentUser.get("ingredientsOwned");
        missedIngredients = new ArrayList<>();
        for (String ing: ingredientsParsed) {
            missedIngredients.add(ing);
        }
        usedIngredientsCount = 0;
        List<String> usedIngredients = new ArrayList<>();
        for (int i = 0; i < ingredientsParsed.size(); i++) {
            for (int j = 0; j < userIngredients.size(); j++) {
                String ingredient = ingredientsParsed.get(i);
                String userIngredient = userIngredients.get(j);
                if (ingredient.equals(userIngredient)) {
                    usedIngredients.add(ingredient);
                    usedIngredientsCount++;
                }
            }
        }
        missedIngredients.removeAll(usedIngredients);
        missedIngredientsCount = ingredientsParsed.size() - usedIngredientsCount;
    }

    public static List<Recipe> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            recipes.add(new Recipe(jsonArray.getJSONObject(i), null, true, false));
        }
        return recipes;
    }

    public static List<Recipe> fromJsonArrayFullRecipe(JSONArray jsonArray) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            recipes.add(new Recipe(jsonArray.getJSONObject(i), null, true, true));
        }
        return recipes;
    }

    public static List<Recipe> fromParseRecipeArray(List<ParseRecipe> parseRecipes) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < parseRecipes.size(); i++) {
            recipes.add(new Recipe(null, parseRecipes.get(i), false, false));
        }
        return recipes;
    }

    public boolean isFromApi() {
        return fromApi;
    }

    // REQUiRES isFromApi() == false
    public String getObjectId() { return objectId; }

    public String getTitle() {
        return title;
    }

    // REQUiRES isFromApi() == true
    public String getImageUrl() {
        // Preconditions.checkArgument(isFromApi());
        return imageUrl;
    }

    // REQUiRES isFromApi() == false
    public ParseFile getImage() {
        return image;
    }

    public int getMissedIngredientsCount() { return missedIngredientsCount; }

    public int getUsedIngredientsCount() {
        return usedIngredientsCount;
    }

    // REQUiRES isFromApi() == true
    public int getId() {
        return id;
    }

    // REQUiRES isFromApi() == false
    public int getServings() { return servings; }

    // REQUiRES isFromApi() == false
    public int getReadyInMinutes() {
        return readyInMinutes;
    }

    // REQUiRES isFromApi() == false
    public String getIngredients() {
        return ingredients;
    }

    // REQUiRES isFromApi() == false
    public String getInstructions() {
        return instructions;
    }

    public List<String> getMissedIngredients() {
        return missedIngredients;
    }

    // REQUiRES isFromApi() == false
    public List<String> getIngredientsParsed() { return ingredientsParsed; }

}
