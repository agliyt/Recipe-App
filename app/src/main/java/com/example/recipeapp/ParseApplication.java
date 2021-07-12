package com.example.recipeapp;

import com.parse.Parse;
import android.app.Application;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models here
        // ParseObject.registerSubclass(Model.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("gCAMkOgLAW5ecgtlOxsjefMMY7gBN2gHBW1q7P80")
                .clientKey("J7zQs71m1z5rxeqfLqy7lye9qFxlupggRLlyuxRg")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}