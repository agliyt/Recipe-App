package com.example.recipeapp;

import com.example.recipeapp.models.ParseRecipe;
import com.example.recipeapp.models.Receipt;
import com.parse.Parse;
import com.parse.ParseObject;

import android.app.Application;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models here
        ParseObject.registerSubclass(ParseRecipe.class);
        Receipt.registerSubclass(Receipt.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("gCAMkOgLAW5ecgtlOxsjefMMY7gBN2gHBW1q7P80")
                .clientKey("J7zQs71m1z5rxeqfLqy7lye9qFxlupggRLlyuxRg")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}