package com.example.recipeapp.fragments;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.BuildConfig;
import com.example.recipeapp.R;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.RecipeDetails;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeDetailsFragment extends Fragment {

    public static final String TAG = "RecipeDetailsFragment";
    public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;
    public static String RECIPE_DETAILS_URL;

    private Recipe recipe;
    private RecipeDetails recipeDetails;
    private int recipeId;

    private TextView tvTitle;
    private ImageView ivImage;
    private TextView tvServings;
    private TextView tvReadyInMinutes;
    private TextView tvIngredients;
    private TextView tvInstructions;
    private RelativeLayout rlDetailsView;

    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        recipe = (Recipe) bundle.getSerializable("recipe");
        recipeId = recipe.getId();

        tvTitle = view.findViewById(R.id.tvTitle);
        ivImage = view.findViewById(R.id.ivImage);
        tvServings = view.findViewById(R.id.tvServings);
        tvReadyInMinutes = view.findViewById(R.id.tvReadyInMinutes);
        tvIngredients = view.findViewById(R.id.tvIngredientsText);
        tvInstructions = view.findViewById(R.id.tvInstructionsText);
        rlDetailsView = view.findViewById(R.id.rlDetailsView);

        int nightModeFlags =
                getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                rlDetailsView.setBackgroundColor(getResources().getColor(R.color.black));
                break;

            case Configuration.UI_MODE_NIGHT_NO:

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                rlDetailsView.setBackgroundColor(getResources().getColor(R.color.white));
                break;
        }

        if (recipe.isFromApi()) {
            RECIPE_DETAILS_URL = "https://api.spoonacular.com/recipes/" + String.valueOf(recipeId) + "/information?includeNutrition=false&apiKey=" + REST_CONSUMER_KEY;
            Log.i(TAG, RECIPE_DETAILS_URL);

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(RECIPE_DETAILS_URL, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        recipeDetails = new RecipeDetails(jsonObject);
                        tvTitle.setText(recipeDetails.getTitle());
                        tvServings.setText("Servings: " + String.valueOf(recipeDetails.getServings()));
                        tvReadyInMinutes.setText("Ready in " + String.valueOf(recipeDetails.getReadyInMinutes()) + " minutes");
                        tvInstructions.setText(recipeDetails.getInstructions());

                        List<String> ingredients = recipeDetails.getIngredients();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < ingredients.size() - 1; i++) {
                            sb.append(ingredients.get(i));
                            sb.append("\n");
                        }
                        sb.append(ingredients.get(ingredients.size() - 1));

                        tvIngredients.setText(sb.toString());

                        Glide.with(getContext())
                                .load(recipeDetails.getImageUrl())
                                .into(ivImage);

                    } catch (JSONException e) {
                        Log.e(TAG, "Hit json exception", e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "onFailure", throwable);
                }
            });
        } else {
            tvTitle.setText(recipe.getTitle());
            tvServings.setText("Servings: " + String.valueOf(recipe.getServings()));
            tvReadyInMinutes.setText("Ready in " + String.valueOf(recipe.getReadyInMinutes()) + " minutes");
            tvInstructions.setText(recipe.getInstructions());
            tvIngredients.setText(recipe.getIngredients());

            Glide.with(getContext())
                    .load(recipe.getImage().getUrl())
                    .into(ivImage);
        }
    }
}