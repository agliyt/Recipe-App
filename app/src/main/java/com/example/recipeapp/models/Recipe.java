package com.example.recipeapp.models;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.BuildConfig;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class Recipe implements Serializable {

    String title;
    String imageUrl;
    int missedIngredientsCount;
    int usedIngredientsCount;
    int id;
    ParseUser currentUser;

    public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;

    public Recipe(JSONObject jsonObject, boolean isApiRecipe) throws JSONException {
        title = jsonObject.getString("title");
        imageUrl = jsonObject.getString("image");
        if (isApiRecipe) {
            missedIngredientsCount = jsonObject.getInt("missedIngredientCount");
            usedIngredientsCount = jsonObject.getInt("usedIngredientCount");
            id = jsonObject.getInt("id");
        } else {
            setNumberIngredients(jsonObject);
        }
    }

    private void setNumberIngredients(JSONObject jsonObject) throws JSONException {
        String URL = "https://api.spoonacular.com/recipes/parseIngredients?ingredientList=" + jsonObject.getString("ingredients") + "&servings=" + String.valueOf(jsonObject.getInt("servings")) + "&includeNutrition=false&apiKey=" + REST_CONSUMER_KEY;
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i("Recipe", "onSuccess");
                JSONArray jsonArray = json.jsonArray;
                List<String> ingredients = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        ingredients.add(jsonArray.getJSONObject(i).getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // find how many ingredients are in common
                currentUser = ParseUser.getCurrentUser();
                List<String> userIngredients = (List<String>) currentUser.get("ingredientsOwned");
                usedIngredientsCount = 0;
                for (int i = 0; i < ingredients.size(); i++) {
                    for (int j = 0; j < userIngredients.size(); j++) {
                        String ingredient = ingredients.get(i);
                        String userIngredient = userIngredients.get(j);
                        if (ingredient == userIngredient) {
                            usedIngredientsCount++;
                        }
                    }
                }
                missedIngredientsCount = ingredients.size() - usedIngredientsCount;

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("onFailure", response + throwable);
            }
        });
    }

    public static List<Recipe> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            recipes.add(new Recipe(jsonArray.getJSONObject(i), true));
        }
        return recipes;
    }

    public static List<Recipe> fromArrayList(List<ParseObject> parseObjects) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < parseObjects.size(); i++) {
            //recipes.add(new Recipe(parseObjects.get(i), false));
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
