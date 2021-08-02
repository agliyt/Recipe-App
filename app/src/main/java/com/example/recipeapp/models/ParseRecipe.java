package com.example.recipeapp.models;

import android.view.LayoutInflater;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("ParseRecipe")
public class ParseRecipe extends ParseObject {

    public ParseRecipe() {
    }

    public ParseRecipe(Recipe recipe) {
        setTitle(recipe.getTitle());
        setImage(recipe.getImage());
        setServings(recipe.getServings());
        setReadyInMinutes(recipe.getReadyInMinutes());
        setIngredients(recipe.getIngredients());
        setInstructions(recipe.getInstructions());
        setIngredientsParsed(recipe.getIngredientsParsed());
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public ParseFile getImage() {
        return getParseFile("image");
    }

    public void setImage(ParseFile image) {
        put("image", image);
    }

    public ParseUser getAuthor() {
        return getParseUser("author");
    }

    public void setAuthor(ParseUser author) {
        put("author", author);
    }

    public int getServings() {
        return getInt("servings");
    }

    public void setServings(int servings) {
        put("servings", servings);
    }

    public int getReadyInMinutes() { return getInt("readyInMinutes"); }

    public void setReadyInMinutes(int readyInMinutes) { put("readyInMinutes", readyInMinutes); }

    public String getIngredients() { return getString("ingredients"); }

    public void setIngredients(String ingredients) { put("ingredients", ingredients); }

    public List<String> getIngredientsParsed() { return (List<String>) get("ingredientsParsed"); }

    public void setIngredientsParsed(List<String> ingredientsParsed) { put("ingredientsParsed", ingredientsParsed); }

    public String getInstructions() { return getString("instructions"); }

    public void setInstructions(String instructions) { put("instructions", instructions); }

}
