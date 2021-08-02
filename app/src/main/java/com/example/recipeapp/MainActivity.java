package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.recipeapp.cache.Cache;
import com.example.recipeapp.fragments.ComposeFragment;
import com.example.recipeapp.fragments.FavoritesFragment;
import com.example.recipeapp.fragments.IngredientsFragment;
import com.example.recipeapp.fragments.MakeRecipeFragment;
import com.example.recipeapp.fragments.RecipesFragment;
import com.example.recipeapp.fragments.ShoppingListFragment;
import com.example.recipeapp.helpers.ReceiptProcessor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                menu.findItem(R.id.ingredientsTab).setIcon(R.drawable.ic_round_view_list_24);
                menu.findItem(R.id.recipesTab).setIcon(R.drawable.ic_outline_fastfood_24);
                menu.findItem(R.id.composeTab).setIcon(R.drawable.ic_outline_create_24);
                menu.findItem(R.id.shoppingListTab).setIcon(R.drawable.ic_outline_shopping_cart_24);
                menu.findItem(R.id.favoritesTab).setIcon(R.drawable.ic_round_star_outline_24);

                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.ingredientsTab:
                        fragment = new IngredientsFragment();
                        break;
                    case R.id.recipesTab:
                        item.setIcon(R.drawable.ic_baseline_fastfood_24);
                        fragment = new RecipesFragment();
                        break;
                    case R.id.composeTab:
                        item.setIcon(R.drawable.ic_round_create_24);
                        fragment = new MakeRecipeFragment();
                        break;
                    case R.id.shoppingListTab:
                        item.setIcon(R.drawable.ic_baseline_shopping_cart_24);
                        fragment = new ShoppingListFragment();
                        break;
                    case R.id.favoritesTab:
                        item.setIcon(R.drawable.ic_outline_star_24);
                        fragment = new FavoritesFragment();
                        break;
                    default:
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flRecipesContainer, fragment).commit();
                return true;
            }
        });
        // set default
        bottomNavigationView.setSelectedItemId(R.id.ingredientsTab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            Log.i(TAG, "logout button clicked");
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null

            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        } else if (item.getItemId() == R.id.receiptCamera) {
            Log.i(TAG, "receipt camera button clicked");
            ReceiptProcessor.processReceipt();

            bottomNavigationView.setSelectedItemId(R.id.ingredientsTab);

            FragmentTransaction ft =  fragmentManager.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            IngredientsFragment ingredientsFragment = new IngredientsFragment();

            ft.replace(R.id.flRecipesContainer, ingredientsFragment);
            ft.addToBackStack(null);
            ft.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}