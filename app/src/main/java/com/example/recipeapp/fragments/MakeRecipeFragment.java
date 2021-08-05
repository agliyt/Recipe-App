package com.example.recipeapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.recipeapp.R;
import com.example.recipeapp.adapters.ComposeAdapter;
import com.example.recipeapp.helpers.FavoritesHelper;
import com.example.recipeapp.helpers.SwipeToDeleteCallback;
import com.example.recipeapp.models.ParseRecipe;
import com.example.recipeapp.models.Recipe;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.

 */
public class MakeRecipeFragment extends Fragment implements ComposeAdapter.OnClickListener {

    public static final String TAG = "MakeRecipesFragment";

    private RecyclerView rvUserRecipes;
    private List<Recipe> allRecipes;
    private ComposeAdapter adapter;
    private Button btnCompose;

    public MakeRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_make_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvUserRecipes = view.findViewById(R.id.rvUserRecipes);

        allRecipes = new ArrayList<>();
        adapter = new ComposeAdapter(getContext(), allRecipes, MakeRecipeFragment.this);
        // set adapter on recycler view
        rvUserRecipes.setAdapter(adapter);
        // set layout manager on recycler view
        rvUserRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        // set up swipe to delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(rvUserRecipes);

        queryUserRecipes();

        btnCompose = view.findViewById(R.id.btnCompose);

        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "compose button clicked");
                ComposeFragment composeFragment = new ComposeFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup)getView().getParent()).getId(), composeFragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    protected void queryUserRecipes() {
        ParseQuery<ParseRecipe> query = ParseQuery.getQuery(ParseRecipe.class);
        query.include("author");
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseRecipe>() {
            @Override
            public void done(List<ParseRecipe> parseRecipes, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting recipes", e);
                    return;
                }
                try {
                    allRecipes.clear();
                    allRecipes.addAll(Recipe.fromParseRecipeArray(parseRecipes));
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        Log.i(TAG, "rvUserRecipes clicked at position " + String.valueOf(position));
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