package com.example.recipeapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.BuildConfig;
import com.example.recipeapp.R;
import com.example.recipeapp.Recipe;
import com.example.recipeapp.adapters.RecipesAdapter;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipesFragment extends Fragment implements RecipesAdapter.onClickListener {

    public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;
    public static final String BASE_URL = "https://api.spoonacular.com/recipes/findByIngredients";
    public static final String TAG = "RecipesFragment";
    String SEARCH_RECIPES_URL;

    RecyclerView rvRecipes;
    List<Recipe> allRecipes;
    RecipesAdapter adapter;
    ParseUser currentUser;
    List<String> ingredients;
    String ingredientsString;

    public RecipesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvRecipes = view.findViewById(R.id.rvRecipes);

        allRecipes = new ArrayList<>();
        adapter = new RecipesAdapter(getContext(), allRecipes, RecipesFragment.this);

        // create data for one row in list
        // create adapter
        // create data source
        // set adapter on recycler view
        rvRecipes.setAdapter(adapter);
        // set layout manager on recycler view
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUser = ParseUser.getCurrentUser();
        ingredients = (List<String>) currentUser.get("ingredientsOwned");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ingredients.size()-1; i++) {
            sb.append(ingredients.get(i));
            sb.append(",+");
        }
        sb.append(ingredients.get(ingredients.size()-1));
        ingredientsString = sb.toString();

        SEARCH_RECIPES_URL = BASE_URL + "?ingredients=" + ingredientsString + "&number=20&ranking=2&apiKey=" + REST_CONSUMER_KEY;
        Log.i(TAG, SEARCH_RECIPES_URL);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(SEARCH_RECIPES_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                try {
                    allRecipes.addAll(Recipe.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                    Log.i(TAG, "Recipes: " + allRecipes.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure: " + response + throwable);
            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        final Recipe recipe = allRecipes.get(position);
        FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        RecipeDetailsFragment recipeDetailsFragment = new RecipeDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("recipe", recipe);
        recipeDetailsFragment.setArguments(bundle);
        ft.replace(R.id.flRecipesContainer, recipeDetailsFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}