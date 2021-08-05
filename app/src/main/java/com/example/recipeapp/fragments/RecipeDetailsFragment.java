package com.example.recipeapp.fragments;

import android.app.Dialog;
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
import com.example.recipeapp.MainActivity;
import com.example.recipeapp.R;
import com.example.recipeapp.cache.Cache;
import com.example.recipeapp.helpers.ApiUrlHelper;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.RecipeDetails;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeDetailsFragment extends BottomSheetDialogFragment {

    public static final String TAG = "RecipeDetailsFragment";

    private Cache cache;
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

    private BottomSheetBehavior mBehavior;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        final View view = View.inflate(getContext(), R.layout.fragment_recipe_details, null);

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);


        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    // View is expended
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    // View is collapsed
                }

                if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        return dialog;
    }


    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        recipe = (Recipe) bundle.getSerializable("recipe");
        recipeId = recipe.getId();

        cache = Cache.getCache();
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
            recipeDetails = cache.get(recipeId);
            if (recipeDetails == null) { // if recipeDetails is not in cache
                getRecipeDetails();
            } else { // recipeDetails found in cache
                setRecipeDetails();
            }
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

    public void getRecipeDetails() {
        String RECIPE_DETAILS_URL = ApiUrlHelper.getApiUrl(String.valueOf(recipeId) + "/information?includeNutrition=false");
        Log.i(TAG, RECIPE_DETAILS_URL);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(RECIPE_DETAILS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    recipeDetails = new RecipeDetails(jsonObject);
                    setRecipeDetails();
                    cache.put(recipeId, recipeDetails);

                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure", throwable);
            }
        });
    }

    public void setRecipeDetails() {
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
    }
}