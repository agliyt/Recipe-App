package com.example.recipeapp.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.BuildConfig;
import com.example.recipeapp.R;
import com.example.recipeapp.models.ParseRecipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeFragment extends Fragment {

    public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;
    public static final String TAG = "ComposeFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public String photoFileName = "photo.jpg";

    private EditText etTitle;
    private ImageView ivRecipeImage;
    private Button btnCaptureImage;
    private EditText etServings;
    private EditText etReadyInMinutes;
    private EditText etIngredients;
    private EditText etInstructions;
    private Button btnSubmit;
    private File photoFile;
    BottomNavigationView bottomNavigationView;

    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etTitle = view.findViewById(R.id.etTitle);
        ivRecipeImage = view.findViewById(R.id.ivRecipeImage);
        btnCaptureImage = view.findViewById(R.id.btnCaptureImage);
        etServings = view.findViewById(R.id.etServings);
        etReadyInMinutes = view.findViewById(R.id.etReadyInMinutes);
        etIngredients = view.findViewById(R.id.etIngredients);
        etInstructions = view.findViewById(R.id.etInstructions);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        ivRecipeImage.setVisibility(View.GONE);

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (photoFile == null || ivRecipeImage.getDrawable() == null) {
                    Toast.makeText(getContext(), "There is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int servings = Integer.parseInt(etServings.getText().toString());
                int readyInMinutes = Integer.parseInt(etReadyInMinutes.getText().toString());
                String ingredients = etIngredients.getText().toString();
                if (ingredients.isEmpty()) {
                    Toast.makeText(getContext(), "Ingredients cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                String instructions = etInstructions.getText().toString();
                if (instructions.isEmpty()) {
                    Toast.makeText(getContext(), "Instructions cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(title, currentUser, photoFile, servings, readyInMinutes, ingredients, instructions);

                // reload compose tab
                bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                Menu menu = bottomNavigationView.getMenu();
                bottomNavigationView.setSelectedItemId(R.id.composeTab);
            }
        });
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.recipeapp.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivRecipeImage.setVisibility(View.VISIBLE);
                ivRecipeImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    private void savePost(String title, ParseUser currentUser, File photoFile, int servings, int readyInMinutes, String ingredients, String instructions) {
        ParseRecipe parseRecipe = new ParseRecipe();
        parseRecipe.setTitle(title);
        parseRecipe.setImage(new ParseFile(photoFile));
        parseRecipe.setAuthor(currentUser);
        parseRecipe.setServings(servings);
        parseRecipe.setReadyInMinutes(readyInMinutes);
        parseRecipe.setIngredients(ingredients);
        parseRecipe.setInstructions(instructions);

        String URL = "https://api.spoonacular.com/recipes/parseIngredients?ingredientList=" + ingredients + "&servings=" + String.valueOf(servings) + "&includeNutrition=false&apiKey=" + REST_CONSUMER_KEY;
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                List<String> ingredientsParsed = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        ingredientsParsed.add(jsonArray.getJSONObject(i).getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                parseRecipe.setIngredientsParsed(ingredientsParsed);

                parseRecipe.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while saving", e);
                            Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.i(TAG, "Post save was successful");
                        // reset all compose elements
                        etTitle.setText("");
                        ivRecipeImage.setImageResource(0);
                        ivRecipeImage.setVisibility(View.GONE);
                        etServings.setText("");
                        etReadyInMinutes.setText("");
                        etIngredients.setText("");
                        etInstructions.setText("");
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure: " + response + throwable);
            }
        });
    }

}