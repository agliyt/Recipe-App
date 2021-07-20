package com.example.recipeapp.helpers;

import android.util.Log;

import com.example.recipeapp.models.Recipe;
import com.parse.ParseUser;

import java.util.List;

public class Favorites {

    public static final String TAG = "Favorites";

    public static void favoriteRecipe(Recipe recipe) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> favoriteApiRecipes;
        List<String> favoriteUserRecipes;
        // handle whether recipe is in favorites or not
        if (recipe.isFromApi()) {
            favoriteApiRecipes = (List<String>) currentUser.get("recipesFavoritedAPI");
            if (favoriteApiRecipes.contains(String.valueOf(recipe.getId()))) { // already favorited
                favoriteApiRecipes.remove(String.valueOf(recipe.getId()));
            } else { // not favorited yet
                favoriteApiRecipes.add(String.valueOf(recipe.getId()));
            }
            // Other attributes than "recipesFavoritedAPI" will remain unchanged!
            currentUser.put("recipesFavoritedAPI", favoriteApiRecipes);

            // Saves the object.
            currentUser.saveInBackground(e -> {
                if(e==null){
                    //Save successfull
                    Log.i(TAG, "Save successful: " + favoriteApiRecipes.toString());
                }else{
                    // Something went wrong while saving
                    Log.e(TAG, "Save unsuccessful", e);
                }
            });
        } else {
            favoriteUserRecipes = (List<String>) currentUser.get("recipesFavoritedUser");
            if (favoriteUserRecipes.contains(recipe.getObjectId())) { // already favorited
                favoriteUserRecipes.remove(recipe.getObjectId());
            } else { // not favorited yet
                favoriteUserRecipes.add(recipe.getObjectId());
            }
            // Other attributes than "recipesFavoritedUser" will remain unchanged!
            currentUser.put("recipesFavoritedUser", favoriteUserRecipes);

            // Saves the object.
            currentUser.saveInBackground(e -> {
                if(e==null){
                    //Save successful
                    Log.i(TAG, "Save successful: " + favoriteUserRecipes.toString());
                }else{
                    // Something went wrong while saving
                    Log.e(TAG, "Save unsuccessful", e);
                }
            });
        }
    }
}
