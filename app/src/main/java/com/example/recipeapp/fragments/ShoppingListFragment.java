package com.example.recipeapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipeapp.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListFragment extends IngredientsFragment {

    @Override
    public void initializeIngredients() {
        ingredients = (List<String>) currentUser.get("shoppingList");
    }

    @Override
    public void updateIngredients() {
        if (currentUser != null) {
            // Other attributes than "ingredientsOwned" will remain unchanged!
            currentUser.put("shoppingList", ingredients);

            // Saves the object.
            currentUser.saveInBackground(e -> {
                if(e==null){
                    //Save successfull
                    Log.i(TAG, "Save successful: " + ingredients.toString());
                }else{
                    // Something went wrong while saving
                    Log.e(TAG, "Save unsuccessful", e);
                }
            });
        }
    }
}