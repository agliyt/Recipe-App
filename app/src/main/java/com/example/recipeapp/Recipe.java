package com.example.recipeapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    String title;
    String imageUrl;
    int missedIngredientsCount;
    int usedIngredientsCount;

    public Recipe(JSONObject jsonObject) throws JSONException {
        title = jsonObject.getString("title");
        imageUrl = jsonObject.getString("image");
        missedIngredientsCount = jsonObject.getInt("missedIngredientCount");
        usedIngredientsCount = jsonObject.getInt("usedIngredientCount");
    }

    public static List<Recipe> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            recipes.add(new Recipe(movieJsonArray.getJSONObject(i)));
        }
        return recipes;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getMissedIngredientsCount() {
        return missedIngredientsCount;
    }

    public int getUsedIngredientsCount() {
        return usedIngredientsCount;
    }
}
