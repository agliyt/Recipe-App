package com.example.recipeapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.recipeapp.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeORM {

    private static final String TAG = "RecipeORM";

    private static final String TABLE_NAME = "recipe";

    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY";
    private static final String COLUMN_ID = "id";

    private static final String COLUMN_TITLE_TYPE = "TEXT";
    private static final String COLUMN_TITLE = "title";

    private static final String COLUMN_IMAGE_URL_TYPE = "TEXT";
    private static final String COLUMN_IMAGE_URL = "image_url";

    private static final String COLUMN_SERVINGS_TYPE = "INTEGER";
    private static final String COLUMN_SERVINGS = "servings";

    private static final String COLUMN_READY_IN_MINUTES_TYPE = "INTEGER";
    private static final String COLUMN_READY_IN_MINUTES = "ready_in_minutes";

    private static final String COLUMN_INGREDIENTS_TYPE = "TEXT";
    private static final String COLUMN_INGREDIENTS = "ingredients";

    private static final String COLUMN_INSTRUCTIONS_TYPE = "TEXT";
    private static final String COLUMN_INSTRUCTIONS = "instructions";


    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " " + COLUMN_ID_TYPE + COMMA_SEP +
                    COLUMN_TITLE  + " " + COLUMN_TITLE_TYPE + COMMA_SEP +
                    COLUMN_IMAGE_URL + " " + COLUMN_IMAGE_URL_TYPE + COMMA_SEP +
                    COLUMN_SERVINGS + " " + COLUMN_SERVINGS_TYPE + COMMA_SEP +
                    COLUMN_READY_IN_MINUTES + " " + COLUMN_READY_IN_MINUTES_TYPE + COMMA_SEP +
                    COLUMN_INGREDIENTS + " " + COLUMN_INGREDIENTS_TYPE + COMMA_SEP +
                    COLUMN_INSTRUCTIONS + " " + COLUMN_INSTRUCTIONS_TYPE +
                    ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static void insertRecipe(Context context, Recipe recipe) {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getWritableDatabase();

        ContentValues values = recipeToContentValues(recipe);
        long recipeId = database.insert(RecipeORM.TABLE_NAME, "null", values);
        Log.i(TAG, "Inserted new Recipe with ID: " + recipeId);

        database.close();
    }

    /**
     * Packs a Post object into a ContentValues map for use with SQL inserts.
     * @param recipe
     */
    private static ContentValues recipeToContentValues(Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(RecipeORM.COLUMN_ID, recipe.getId());
        values.put(RecipeORM.COLUMN_TITLE, recipe.getTitle());
        values.put(RecipeORM.COLUMN_IMAGE_URL, recipe.getImageUrl());
        values.put(RecipeORM.COLUMN_SERVINGS, recipe.getServings());
        values.put(RecipeORM.COLUMN_READY_IN_MINUTES, recipe.getReadyInMinutes());
        values.put(RecipeORM.COLUMN_INGREDIENTS, recipe.getIngredients());
        values.put(RecipeORM.COLUMN_INSTRUCTIONS, recipe.getInstructions());

        return values;
    }

    public static Recipe getRecipe(Context context, int id) {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT id FROM " + RecipeORM.TABLE_NAME + " WHERE id = ?", new String[] {String.valueOf(id)});

        Log.i(TAG, "Loaded " + cursor.getCount() + " Recipes..."); // should be 1
        Recipe recipe = null;

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                recipe = cursorToRecipe(cursor);
                cursor.moveToNext();
            }
            Log.i(TAG, "Recipes loaded successfully.");
        }

        database.close();

        return recipe;
    }

    /**
     * Populates a Post object with data from a Cursor
     * @param cursor
     * @return
     */
    private static Recipe cursorToRecipe(Cursor cursor) {
        // set up recipe
        Recipe recipe = new Recipe();

//        recipe.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
//        recipe.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));

        return recipe;
    }

}
