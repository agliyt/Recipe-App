package com.example.recipeapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recipe implements Serializable {

    String title;
    String imageUrl;
    int missedIngredientsCount;
    int usedIngredientsCount;
    int id;

    public Recipe(JSONObject jsonObject) throws JSONException {
        title = jsonObject.getString("title");
        imageUrl = jsonObject.getString("image");
        missedIngredientsCount = jsonObject.getInt("missedIngredientCount");
        usedIngredientsCount = jsonObject.getInt("usedIngredientCount");
        id = jsonObject.getInt("id");
    }

    public static List<Recipe> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            recipes.add(new Recipe(jsonArray.getJSONObject(i)));
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

    public int getId() {
        return id;
    }
}
