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
import com.example.recipeapp.adapters.RecipesAdapter;
import com.example.recipeapp.helpers.ApiUrlHelper;
import com.example.recipeapp.helpers.FavoritesHelper;
import com.example.recipeapp.models.ParseRecipe;
import com.example.recipeapp.models.Recipe;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment implements RecipesAdapter.OnClickListener {

    public static final String TAG = "FavoritesFragment";

    private ParseUser currentUser;
    private List<String> favoriteApiRecipes;
    private List<String> favoriteUserRecipes;
    private RecyclerView rvRecipes;
    private List<Recipe> allRecipes;
    private RecipesAdapter adapter;
    private String recipeIdString;

    public FavoritesFragment() {
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
        currentUser = ParseUser.getCurrentUser();
        favoriteApiRecipes = (List<String>) currentUser.get("recipesFavoritedAPI");
        favoriteUserRecipes = (List<String>) currentUser.get("recipesFavoritedUser");

        rvRecipes = view.findViewById(R.id.rvRecipes);

        allRecipes = new ArrayList<>();
        if (favoriteUserRecipes.size() > 0) {
            queryUserRecipes();
        }

        adapter = new RecipesAdapter(getContext(), allRecipes, FavoritesFragment.this);
        // set adapter on recycler view
        rvRecipes.setAdapter(adapter);
        // set layout manager on recycler view
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        if (favoriteApiRecipes.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < favoriteApiRecipes.size() - 1; i++) {
                sb.append(favoriteApiRecipes.get(i));
                sb.append(",");
            }
            sb.append(favoriteApiRecipes.get(favoriteApiRecipes.size() - 1));
            recipeIdString = sb.toString();

            String SEARCH_RECIPES_URL = ApiUrlHelper.getApiUrl("informationBulk?ids=" + recipeIdString);
            Log.i(TAG, SEARCH_RECIPES_URL);

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(SEARCH_RECIPES_URL, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    JSONArray jsonArray = json.jsonArray;
                    try {
                        allRecipes.addAll(Recipe.fromJsonArrayFullRecipe(jsonArray));
                        adapter.notifyDataSetChanged();
                        Log.i(TAG, "Recipes: " + allRecipes.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.d(TAG, "onFailure: " + response + throwable);
                }
            });
        }
    }

    protected void queryUserRecipes() {
        ParseQuery<ParseRecipe> query = ParseQuery.getQuery(ParseRecipe.class);
        for (String objectId : favoriteUserRecipes) {
            query.whereEqualTo("objectId", objectId);
        }
        query.findInBackground(new FindCallback<ParseRecipe>() {
            @Override
            public void done(List<ParseRecipe> parseRecipes, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting recipes", e);
                    return;
                }
                try {
                    allRecipes.addAll(Recipe.fromParseRecipeArray(parseRecipes));
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
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
        allRecipes.remove(position);
        adapter.notifyItemRemoved(position);
    }
}