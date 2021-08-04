package com.example.recipeapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Receipt")
public class Receipt extends ParseObject {

    public ParseFile getImage() {
        return getParseFile("image");
    }

    public void setImage(ParseFile image) {
        put("image", image);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

}
