package com.example.recipeapp.helpers;

import android.util.Log;

import com.example.recipeapp.models.Recipe;
import com.parse.ParseUser;

import java.util.List;

public class FavoritesHelper {

    public static final String TAG = "FavoritesHelper";

    public static void favoriteRecipe(Recipe recipe) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> missedIngredients = recipe.getMissedIngredients();
        List<String> shoppingList = (List<String>) currentUser.get("shoppingList");
        List<String> favoriteApiRecipes;
        List<String> favoriteUserRecipes;
        // handle whether recipe is in favorites or not
        if (recipe.isFromApi()) {
            favoriteApiRecipes = (List<String>) currentUser.get("recipesFavoritedAPI");
            if (favoriteApiRecipes.contains(String.valueOf(recipe.getId()))) { // already favorited
                favoriteApiRecipes.remove(String.valueOf(recipe.getId()));
                for (int i = 0; i < missedIngredients.size(); i++) {
                    String missedIngredient = missedIngredients.get(i);
                    if (shoppingList.contains(missedIngredient)) {
                        shoppingList.remove(missedIngredient);
                    }
                }
            } else { // not favorited yet
                favoriteApiRecipes.add(String.valueOf(recipe.getId()));
                for (int i = 0; i < missedIngredients.size(); i++) {
                    String missedIngredient = missedIngredients.get(i);
                    if (!shoppingList.contains(missedIngredient)) {
                        shoppingList.add(missedIngredient);
                    }
                }
            }
            // Other attributes than "recipesFavoritedAPI" will remain unchanged!
            currentUser.put("recipesFavoritedAPI", favoriteApiRecipes);
        } else {
            favoriteUserRecipes = (List<String>) currentUser.get("recipesFavoritedUser");
            if (favoriteUserRecipes.contains(recipe.getObjectId())) { // already favorited
                favoriteUserRecipes.remove(recipe.getObjectId());
                for (int i = 0; i < missedIngredients.size(); i++) {
                    String missedIngredient = missedIngredients.get(i);
                    if (shoppingList.contains(missedIngredient)) {
                        shoppingList.remove(missedIngredient);
                    }
                }
            } else { // not favorited yet
                favoriteUserRecipes.add(recipe.getObjectId());
                for (int i = 0; i < missedIngredients.size(); i++) {
                    String missedIngredient = missedIngredients.get(i);
                    if (!shoppingList.contains(missedIngredient)) {
                        shoppingList.add(missedIngredient);
                    }
                }
            }
            // Other attributes than "recipesFavoritedUser" will remain unchanged!
            currentUser.put("recipesFavoritedUser", favoriteUserRecipes);
        }

        currentUser.put("shoppingList", shoppingList);

        // Saves the object.
        currentUser.saveInBackground(e -> {
            if(e==null){
                //Save successful
                Log.i(TAG, "Save successful");
            }else{
                // Something went wrong while saving
                Log.e(TAG, "Save unsuccessful", e);
            }
        });
    }
}
