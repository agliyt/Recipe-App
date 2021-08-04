package com.example.recipeapp.helpers;

import com.example.recipeapp.BuildConfig;

public class ApiUrlHelper {

    public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;
    public static final String BASE_URL = "https://api.spoonacular.com/recipes/";
    public static final String KEY_URL = "&apiKey=" + REST_CONSUMER_KEY;

    public static String getApiUrl(String query) {
        return BASE_URL + query + KEY_URL;
    }

    public static String getIngredientsParseUrl() {
        return BASE_URL + "parseIngredients?apiKey=" + REST_CONSUMER_KEY;
    }

}
