Original App Design Project - README
===

# Creating Food App

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Users can share what ingredients they have in their house, and the app will give them recipes they can make with what they have on hand.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Food & Drink
- **Mobile:** Mobile is essential for this app because you want to go around your house and look for ingrediants that you have. The camera is used to make account profile pictures and possibly share pictures of food made from the recipes. We need a profile so that you can store which ingredients you have in your house and which ingredients you might need to buy in a personalized shopping list.
- **Story:** Makes it so that even people who aren't as good at cooking or baking could make their own food. Could hopefully stop people from ordering unhealthy or fast food and encourage them to make their own food with the ingredients they have instead.
- **Market:** Basically everyone needs to eat food, so this app would be for everyone (except maybe young children).
- **Habit:** People will be constantly using this app to make their meals (breakfast, lunch dinner) and maybe even making baked goods as a snack or as a way to bond with/hang out with their friends.
- **Scope:** V1 would allow users to input which ingredients they have on hand. V2 would be able to connect to a recipe API (Maybe MyCookbook.io). V3 would be able match the ingredients given with recipes that include all these ingredients, only if it's spelled the same way. V4 would account for different spelling and synonyms for ingredients. V5 would also display recipes that you have almost all the ingredients for. V6 would give you a shopping list and allow you to favorite recipes. V7 would suggest substitutions of certain ingredients (https://www.programmableweb.com/api/bonapi-ingredient-alternatives or https://spoonacular.com/food-api). May account for dietary restrictions or allergies.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Allows users to input which ingredients they have on hand (and in what quantity)
* Allows users to delete ingredients that they have
* Connects to a recipe API (Maybe MyCookbook.io)
* Match the ingredients given with recipes that include all these ingredients (can search for specific recipes ranking recipes from having most ingredients to having least ingredients, or generate a random recipe with the ingredients) 
* Include a "I cooked this!" button that users can press to delete ingredients that they've used
* Accounts for different spelling and synonyms for ingredients
* Displays recipes that you have almost all the ingredients for

**Optional Nice-to-have Stories**

* Gives you a shopping list if you want to make a specific recipe
* You can also manually add/delete items to/from your shopping list
* Allows you to favorite recipes
* Suggests substitutions of certain ingredients (https://www.programmableweb.com/api/bonapi-ingredient-alternatives or https://spoonacular.com/food-api)
* Accounts for dietary restrictions or allergies
* Displays nutritional values/calories for each ingredient
* Allows you to select recipes that are in a certain range of calories
* Automatically adjusts recipe quantity to the number of people you're cooking/baking for
* Scans receipts to automatically add ingredients you bought to your recipe
* Recognizes "required" ingredients vs "optional" ingredients (like certain spices or seasoning)

### 2. Screen Archetypes

* ingredients list
   * Allows users to input which ingredients they have on hand (and in what quantity)
   * Allows users to delete ingredients that they have
* ingredients detailed view
    * Displays nutritional values/calories for each ingredient
* recipes search
   * Connects to a recipe API (Maybe MyCookbook.io)
   * Match the ingredients given with recipes that include all these ingredients
       * can search for specific recipes ranking recipes from having most ingredients to having least ingredients, or
       * generate a random recipe with the ingredients)
   * Displays recipes that you have almost all the ingredients for
   * Suggests substitutions of certain ingredients (https://www.programmableweb.com/api/bonapi-ingredient-alternatives or https://spoonacular.com/food-api)
   * Accounts for dietary restrictions or allergies
   * Allows you to select recipes that are in a certain range of calories
   * Recognizes "required" ingredients vs "optional" ingredients (like certain spices or seasoning)
* recipes detailed view
    * Include a "I cooked this!" button that users can press to delete ingredients that they've used
    * Display the full recipe
    * Allows you to favorite recipes
    * Suggests substitutions of certain ingredients (https://www.programmableweb.com/api/bonapi-ingredient-alternatives or https://spoonacular.com/food-api)
    * Automatically adjusts recipe quantity to the number of people you're cooking/baking for
    * Recognizes "required" ingredients vs "optional" ingredients (like certain spices or seasoning)
* shopping list
    * Gives you a shopping list if you want to make a specific recipe
        * Could have a "Making this next!" section in the favorite recipes page
    * You can also manually add/delete items to/from your shopping list
* favorite recipes
    * Allows you to favorite recipes
* Scan shopping list
    * Scans receipts to automatically add ingredients you bought to your recipe

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* ingredients list
* recipes search
* shopping list
* favorite recipes

**Flow Navigation** (Screen to Screen)

* all screens
   * Scan shopping list (would be a button you click in the action bar)
* ingredients list
   * ingredients detailed view
* recipes search
    * recipes detailed view
* recipes detailed view
    * ingredients detailed view
* Scan shopping list
    * ingredients list
