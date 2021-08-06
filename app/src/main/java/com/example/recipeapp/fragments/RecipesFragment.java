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
import com.example.recipeapp.helpers.ApiUrlHelper;
import com.example.recipeapp.helpers.FavoritesHelper;
import com.example.recipeapp.models.ParseRecipe;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.adapters.RecipesAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipesFragment extends Fragment implements RecipesAdapter.OnClickListener {

    public static final String TAG = "RecipesFragment";

    private RecyclerView rvRecipes;
    private List<Recipe> allRecipes;
    private RecipesAdapter adapter;
    private ParseUser currentUser;
    private List<String> ingredients;
    private String ingredientsString;

    private boolean finishedUserQuery;
    private boolean finishedApiQuery;

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
        // set adapter on recycler view
        rvRecipes.setAdapter(adapter);
        // set layout manager on recycler view
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        finishedUserQuery = false;
        finishedApiQuery = false;

        currentUser = ParseUser.getCurrentUser();
        ingredients = (List<String>) currentUser.get("ingredientsOwned");
        if (ingredients.size() > 0) {
            queryUserRecipes();
            queryApiRecipes();
        }
    }

    class RecipeComparator implements Comparator<Recipe> {
        @Override
        public int compare(Recipe r1, Recipe r2) {
            int r1MissedCount = r1.getMissedIngredientsCount();
            int r2MissedCount = r2.getMissedIngredientsCount();
            return r1MissedCount < r2MissedCount ? -1 : r1MissedCount == r2MissedCount ? 0 : 1;
        }
    }

    protected void queryUserRecipes() {
        ParseQuery<ParseRecipe> query = ParseQuery.getQuery(ParseRecipe.class);
        query.findInBackground(new FindCallback<ParseRecipe>() {
            @Override
            public void done(List<ParseRecipe> parseRecipes, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting recipes", e);
                    return;
                }
                try {
                    allRecipes.addAll(Recipe.fromParseRecipeArray(parseRecipes));
                    finishedUserQuery = true;

                    if (finishedApiQuery) {
                        // add all user recipes that fit into allRecipes (based on # ingredients missed)
                        Collections.sort(allRecipes, new RecipeComparator());
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        });
    }

    private void queryApiRecipes() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ingredients.size()-1; i++) {
            sb.append(ingredients.get(i));
            sb.append(",+");
        }
        sb.append(ingredients.get(ingredients.size()-1));
        ingredientsString = sb.toString();

        String SEARCH_RECIPES_URL = ApiUrlHelper.getApiUrl("findByIngredients?ingredients=" + ingredientsString + "&number=20&ranking=2");
        Log.i(TAG, SEARCH_RECIPES_URL);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(SEARCH_RECIPES_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                try {
                    allRecipes.addAll(Recipe.fromJsonArray(jsonArray));
                    finishedApiQuery = true;

                    if (finishedUserQuery) {
                        // add all user recipes that fit into allRecipes (based on # ingredients missed)
                        Collections.sort(allRecipes, new RecipeComparator());
                        adapter.notifyDataSetChanged();
                    }
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
        RecipeDetailsFragment recipeDetailsFragment = new RecipeDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("recipe", recipe);
        recipeDetailsFragment.setArguments(bundle);
        recipeDetailsFragment.show(getActivity().getSupportFragmentManager(), recipeDetailsFragment.getTag());
    }

    @Override
    public void onFavoritesClicked(int position) {
        final Recipe recipe = allRecipes.get(position);
        FavoritesHelper.favoriteRecipe(recipe);
    }
}